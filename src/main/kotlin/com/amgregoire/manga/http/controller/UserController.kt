package com.amgregoire.manga.http.controller

import com.amgregoire.manga.http.exception.ResourceNotFoundException
import com.amgregoire.manga.http.exception.UnauthorizedException
import com.amgregoire.manga.http.interceptor.UserAuthenticationRequired
import com.amgregoire.manga.http.interceptor.getUser
import com.amgregoire.manga.http.model.User
import com.amgregoire.manga.http.repository.MangaRepository
import com.amgregoire.manga.http.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
class UserController(
        private val userRepository: UserRepository,
        private val mangaRepository: MangaRepository
)
{
    data class DeleteMangaRequest(val mangaId: String)
    data class AddMangaRequest(val mangaId: String, val url: String)
    data class CreateUserRequest(val name: String, val email: String, val password: String)

    companion object
    {
        const val BASE_URL = "/users"
        const val MODEL_URL_PARAMETER = "/{userId}"
    }

    @PostMapping(BASE_URL)
    fun createUser(
            request: HttpServletRequest,
            @RequestBody userRequest: CreateUserRequest
    ): User?
    {
        return userRepository.save({
            val user = User()
            user.name = userRequest.name
            user.email = userRequest.email
            user.password = userRequest.password
            user
        }())
    }

    @DeleteMapping(BASE_URL + MODEL_URL_PARAMETER)
    fun deleteUser(
            request: HttpServletRequest,
            @PathVariable userId: UUID
    ): ResponseEntity<String>?
    {
        val user = userRepository.findById(userId).orElseThrow {
            ResourceNotFoundException("User not found with id $userId")
        }

        userRepository.delete(user)

        return ResponseEntity.noContent().build<String>()
    }

    @UserAuthenticationRequired
    @PostMapping("$BASE_URL$MODEL_URL_PARAMETER/manga")
    fun addManga(
            request: HttpServletRequest,
            @RequestBody mangaRequest: AddMangaRequest
    ): User?
    {
        val user = request.getUser() ?: throw UnauthorizedException()

        val manga = mangaRepository.findById(UUID.fromString(mangaRequest.mangaId)).orElseThrow {
            //TODO : Update request to include url, if manga is not found, query the link and parse the sources data
            ResourceNotFoundException("Manga with id ${mangaRequest.mangaId} not found")
        }

        user.mangas = user.mangas.plus(manga)

        return userRepository.save(user)
    }

    @UserAuthenticationRequired
    @DeleteMapping("$BASE_URL$MODEL_URL_PARAMETER/manga")
    fun deleteManga(
            request: HttpServletRequest,
            @RequestBody mangaRequest: DeleteMangaRequest
    ): User?
    {
        val user = request.getUser() ?: throw UnauthorizedException()

        val manga = mangaRepository.findById(UUID.fromString(mangaRequest.mangaId)).orElseThrow {
            ResourceNotFoundException("Manga with id ${mangaRequest.mangaId} not found")
        }

        if (user.mangas.contains(manga)) throw ResourceNotFoundException("Manga with id ${mangaRequest.mangaId} is not associated with current user")

        user.mangas = user.mangas.minus(manga)
        user.reading = user.reading.minus(manga)
        user.complete = user.complete.minus(manga)
        user.onHold = user.onHold.minus(manga)
        user.planToRead = user.planToRead.minus(manga)

        return userRepository.save(user)
    }

}