package com.amgregoire.manga.http.controller

import com.amgregoire.manga.http.exception.BadRequestException
import com.amgregoire.manga.http.exception.ResourceNotFoundException
import com.amgregoire.manga.http.exception.UnauthorizedException
import com.amgregoire.manga.http.interceptor.UserAuthenticationRequired
import com.amgregoire.manga.http.interceptor.getAccessToken
import com.amgregoire.manga.http.interceptor.getUser
import com.amgregoire.manga.http.model.AccessToken
import com.amgregoire.manga.http.model.User
import com.amgregoire.manga.http.model.other.MangaSmallQualified
import com.amgregoire.manga.http.model.other.MangaSmallUnqualified
import com.amgregoire.manga.http.repository.AccessTokenRepository
import com.amgregoire.manga.http.repository.MangaRepository
import com.amgregoire.manga.http.repository.UserRepository
import com.amgregoire.manga.http.services.EncryptService
import com.amgregoire.manga.http.services.IUserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
class UserController(
        private val userRepository: UserRepository,
        private val mangaRepository: MangaRepository,
        private val shaService: EncryptService,
        private val accessTokenRepository: AccessTokenRepository,
        private val userService: IUserService
)
{
    /**
     * Requests
     */


    data class CreateUserRequest(val email: String, val password: String)

    data class UserLoginRequest(val email: String, val password: String)
    data class UserLogoutRequest(val accessToken: UUID)

    data class FollowTypeRequest(val followTypeId: UUID? = null)
    data class UnqualifiedMangaRequest(val mangas: List<MangaSmallUnqualified>)
    data class QualifiedMangaRequest(val mangaIds: List<MangaSmallQualified>)
    /**
     * Responses
     */
    class UserLoginResponse(val accessToken: UUID, val user: User)

    companion object
    {
        const val BASE_URL = "/users"
        const val MODEL_URL_PARAMETER = "/{userId}"
    }

    /****************************************************************************
     *
     * User Login routes
     *
     ***************************************************************************/

    /***
     *
     */
    @PostMapping("$BASE_URL/signup")
    fun createUser(request: HttpServletRequest,
                   @RequestBody userRequest: CreateUserRequest
    ): UserLoginResponse
    {
        if (userRepository.findOneByEmail(userRequest.email) != null) throw BadRequestException("User already exists with email ${userRequest.email}")

        val user = userRepository.save({
            val user = User()
            user.email = userRequest.email
            user.password = shaService.encrypt(userRequest.password)

            user
        }())

        val accessToken = accessTokenRepository.save(AccessToken().apply { this.user = user })

        return UserLoginResponse(accessToken.token, userRepository.save(user))
    }

    /***
     *
     */
    @PostMapping("$BASE_URL/login")
    fun userLogin(request: HttpServletRequest,
                  @RequestBody userLoginRequest: UserLoginRequest
    ): UserLoginResponse
    {
        val x = userRepository.findAll()
        val user = userRepository.findOneByEmail(userLoginRequest.email)
                ?: throw ResourceNotFoundException("No user found with email ${userLoginRequest.email}")

        if (!shaService.matches(userLoginRequest.password, user.password)) throw UnauthorizedException("The password you have entered is incorrect")

        // Expire previous token(s)
        userService.expireTokens(user)

        val accessToken = accessTokenRepository.save(AccessToken().apply { this.user = user })

        return UserLoginResponse(accessToken.token, userRepository.save(user))
    }

    /***
     *
     */
    @UserAuthenticationRequired
    @PostMapping("$BASE_URL/logout")
    fun userLogout(request: HttpServletRequest,
                   @RequestBody userLogoutRequest: UserLogoutRequest
    ): ResponseEntity<String>
    {
        val user = request.getUser() ?: throw UnauthorizedException()

        val accessToken = accessTokenRepository.findOneByUserIdAndToken(user.id, userLogoutRequest.accessToken)
                ?: throw ResourceNotFoundException("Access token not found with value [${userLogoutRequest.accessToken}")
        accessToken.expiresAt = Date()
        accessTokenRepository.save(accessToken)

        return ResponseEntity.noContent().build<String>()
    }


    /***
     *
     */
    @UserAuthenticationRequired
    @GetMapping(BASE_URL)
    fun getUser(
            request: HttpServletRequest
    ): UserLoginResponse
    {
        val user = request.getUser() ?: throw UnauthorizedException()
        val token = request.getAccessToken() ?: throw UnauthorizedException()
        return UserLoginResponse(token, userService.getFullUser(user))
    }


    /****************************************************************************
     *
     * User Manga Routes
     *
     ***************************************************************************/

    /***
     *
     */
    @UserAuthenticationRequired
    @PostMapping("$BASE_URL$MODEL_URL_PARAMETER/manga/unqualified")
    fun addMangaToUser(
            request: HttpServletRequest,
            @PathVariable userId: UUID,
            @Valid @RequestBody requestBody: UnqualifiedMangaRequest
    ): User?
    {
        val user = request.getUser() ?: throw UnauthorizedException()
        return userService.addMangaToUserUnqualified(user, requestBody.mangas)
    }

    /***
     *
     */
    @UserAuthenticationRequired
    @PostMapping("$BASE_URL$MODEL_URL_PARAMETER/manga")
    fun addMangaToUser(
            request: HttpServletRequest,
            @PathVariable userId: UUID,
            @Valid @RequestBody requestBody: QualifiedMangaRequest
    ): User?
    {
        val user = request.getUser() ?: throw UnauthorizedException()
        return userService.addMangaToUserQualified(user, requestBody.mangaIds)
    }

    /***
     *
     */
    @UserAuthenticationRequired
    @DeleteMapping("$BASE_URL$MODEL_URL_PARAMETER/manga")
    fun removeMangaFromUser(
            request: HttpServletRequest,
            @PathVariable userId: UUID,
            @Valid @RequestBody requestBody: QualifiedMangaRequest
    ): User?
    {
        val user = request.getUser() ?: throw UnauthorizedException()
        return userService.removeMangaFromUserQualified(user, requestBody.mangaIds)
    }

    /***
     *
     */
    @UserAuthenticationRequired
    @DeleteMapping("$BASE_URL$MODEL_URL_PARAMETER/manga/unqualified")
    fun removeMangaFromUser(
            request: HttpServletRequest,
            @PathVariable userId: UUID,
            @Valid @RequestBody requestBody: UnqualifiedMangaRequest
    ): User?
    {
        val user = request.getUser() ?: throw UnauthorizedException()
        return userService.removeMangaFromUserUnqualified(user, requestBody.mangas)
    }

    /***
     *
     */
    @UserAuthenticationRequired
    @PutMapping("$BASE_URL$MODEL_URL_PARAMETER/manga/{mangaId}")
    fun updateMangaFollowType(
            request: HttpServletRequest,
            @PathVariable userId: UUID,
            @PathVariable mangaId: UUID,
            @Valid @RequestBody requestBody: FollowTypeRequest
    ): User
    {
        val user = request.getUser() ?: throw UnauthorizedException()
        val manga = mangaRepository.findById(mangaId).orElseThrow { ResourceNotFoundException("Manga not found with id [$mangaId]") }
        return userService.updateMangaFollowType(user, manga, requestBody.followTypeId)
    }

}
