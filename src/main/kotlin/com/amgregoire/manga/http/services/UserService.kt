package com.amgregoire.manga.http.services

import com.amgregoire.manga.http.exception.ResourceNotFoundException
import com.amgregoire.manga.http.model.LibraryItemType
import com.amgregoire.manga.http.model.LibraryItemTypes
import com.amgregoire.manga.http.model.Manga
import com.amgregoire.manga.http.model.User
import com.amgregoire.manga.http.model.other.MangaSmallQualified
import com.amgregoire.manga.http.model.other.MangaSmallUnqualified
import com.amgregoire.manga.http.repository.AccessTokenRepository
import com.amgregoire.manga.http.repository.LibraryItemTypeRepository
import com.amgregoire.manga.http.repository.MangaRepository
import com.amgregoire.manga.http.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

interface IUserService
{
    fun expireTokens(user: User)
    fun addMangaToUserUnqualified(user: User, mangas: List<MangaSmallUnqualified>): User
    fun addMangaToUserQualified(user: User, mangaIds: List<MangaSmallQualified>): User
    fun removeMangaFromUserUnqualified(user: User, mangas: List<MangaSmallUnqualified>): User
    fun removeMangaFromUserQualified(user: User, mangas: List<MangaSmallQualified>): User
    fun updateMangaFollowType(user: User, manga: Manga, followTypeId: UUID?): User
    fun getFullUser(user: User): User
}

@Service
class UserService(
        private val accessTokenRepository: AccessTokenRepository,
        private val libraryItemTypeRepository: LibraryItemTypeRepository,
        private val userRepository: UserRepository,
        private val mangaRepository: MangaRepository,
        private val mangaService: MangaService

) : IUserService
{

    override fun expireTokens(user: User)
    {
        val tokens = accessTokenRepository.findAllByUserIdAndExpiresAtGreaterThanEqual(user.id, Date())
        tokens?.let {
            for (token in it)
            {
                token.expireToken()
                accessTokenRepository.save(token)
            }
        }
    }

    override fun addMangaToUserUnqualified(user: User, mangas: List<MangaSmallUnqualified>): User
    {
        var transUser = userRepository.findById(user.id).orElseThrow { ResourceNotFoundException("User not found with id ${user.id}") }

        val followTypes = libraryItemTypeRepository.findAll()
        mangas.forEach { small ->
            try
            {
                val manga = mangaService.getMangaFromLinkAndSource(small.link, small.source)
                transUser = updateMangaFollowType(transUser, manga, small.followType, followTypes)
            }
            catch (ex: Exception)
            {
                // Problem adding manga
            }
        }

        return userRepository.save(transUser)
    }

    override fun addMangaToUserQualified(user: User, mangaIds: List<MangaSmallQualified>): User
    {
        var transUser = userRepository.findById(user.id).orElseThrow { ResourceNotFoundException("User not found with id ${user.id}") }

        val followTypes = libraryItemTypeRepository.findAll()
        mangaIds.forEach { small ->
            try
            {
                val manga = mangaRepository.findById(small.id).orElseThrow { ResourceNotFoundException("Manga not found with id [${small.id}") }
                transUser = updateMangaFollowType(transUser, manga, small.followType, followTypes)
            }
            catch (ex: Exception)
            {
                // Problem adding manga
            }
        }

        return userRepository.save(transUser)

    }

    override fun removeMangaFromUserUnqualified(user: User, mangas: List<MangaSmallUnqualified>): User
    {
        val transUser = userRepository.findById(user.id).orElseThrow { ResourceNotFoundException("User not found with id ${user.id}") }

        mangas.forEach { small ->
            try
            {
                val manga = mangaService.getMangaFromLinkAndSource(small.link, small.source)
                transUser.mangas = transUser.mangas.minus(manga)
                transUser.reading = transUser.reading.minus(manga)
                transUser.complete = transUser.complete.minus(manga)
                transUser.onHold = transUser.onHold.minus(manga)
                transUser.planToRead = transUser.planToRead.minus(manga)
            }
            catch (ex: Exception)
            {
                // Problem adding manga
            }
        }

        return userRepository.save(transUser)
    }

    override fun removeMangaFromUserQualified(user: User, mangas: List<MangaSmallQualified>): User
    {
        val transUser = userRepository.findById(user.id).orElseThrow { ResourceNotFoundException("User not found with id ${user.id}") }

        mangas.forEach { small ->
            try
            {
                val manga = mangaRepository.findById(small.id).orElseThrow { ResourceNotFoundException("Manga not found with id [${small.id}]") }

                transUser.mangas = transUser.mangas.minus(manga)
                transUser.reading = transUser.reading.minus(manga)
                transUser.complete = transUser.complete.minus(manga)
                transUser.onHold = transUser.onHold.minus(manga)
                transUser.planToRead = transUser.planToRead.minus(manga)
            }
            catch (ex: Exception)
            {
                // Problem adding manga
            }
        }

        return userRepository.save(transUser)
    }

    override fun updateMangaFollowType(user: User, manga: Manga, followTypeId: UUID?): User
    {
        val updatedUser = updateMangaFollowType(user, manga, followTypeId, libraryItemTypeRepository.findAll())
        return userRepository.save(updatedUser)
    }

    override fun getFullUser(user: User): User
    {
        return userRepository.findById(user.id).orElseThrow { ResourceNotFoundException("User not found with id [${user.id}]") }
    }

    private fun updateMangaFollowType(user: User, manga: Manga, followTypeId: UUID?, followTypes: List<LibraryItemType>): User
    {
        if (!user.mangas.contains(manga)) user.mangas = user.mangas.plus(manga)

        followTypeId ?: run {
            user.mangas = user.mangas.minus(manga)
            user.reading = user.reading.minus(manga)
            user.complete = user.complete.minus(manga)
            user.onHold = user.onHold.minus(manga)
            user.planToRead = user.planToRead.minus(manga)

            return user
        }

        followTypes.forEach { type ->
            if (type.id == followTypeId)
            {
                user.reading = user.reading.minus(manga)
                user.complete = user.complete.minus(manga)
                user.onHold = user.onHold.minus(manga)
                user.planToRead = user.planToRead.minus(manga)

                when (LibraryItemTypes.fromType(type.type))
                {
                    LibraryItemTypes.Complete -> user.complete = user.complete.plus(manga)
                    LibraryItemTypes.Reading -> user.reading = user.reading.plus(manga)
                    LibraryItemTypes.OnHold -> user.onHold = user.onHold.plus(manga)
                    LibraryItemTypes.PlanToRead -> user.planToRead = user.planToRead.plus(manga)
                }
            }
        }

        return user
    }
}
