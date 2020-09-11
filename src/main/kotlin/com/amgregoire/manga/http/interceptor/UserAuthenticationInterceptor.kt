package com.amgregoire.manga.http.interceptor

import com.amgregoire.manga.http.exception.BadRequestException
import com.amgregoire.manga.http.exception.ResourceNotFoundException
import com.amgregoire.manga.http.exception.UnauthorizedException
import com.amgregoire.manga.http.extension.removeWhiteSpace
import com.amgregoire.manga.http.model.User
import com.amgregoire.manga.http.repository.AccessTokenRepository
import com.amgregoire.manga.http.repository.UserRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class UserAuthenticationInterceptor(
        val userRepository: UserRepository,
        val accessTokenRepository: AccessTokenRepository
) : HandlerInterceptor
{

    data class Authorization(val userId: UUID, val token: UUID)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean
    {
        // Expect that the handler is a function
        if (handler !is HandlerMethod) throw BadRequestException("")

        // Skip authorization if the function is not annotated with @ApplicationAuthenticationRequired
        handler.method.annotations.find { it is UserAuthenticationRequired } ?: return true

        // Fetch, decode, and parse authorization object from authorization header
        val header = request.getHeader("Authorization")?.replace("Bearer ", "") ?: throw UnauthorizedException()
        val authorization = parseAuthorization(decode(header))

        request.setAccessToken(authorization.token)

        // Fetch application with given id
        val user = userRepository
            .findById(authorization.userId)
            .orElseThrow { ResourceNotFoundException("User not found with id ${authorization.userId}") }

        // Require that access token is valid and not expired
        if (accessTokenRepository.findOneByUserIdAndTokenAndExpiresAtGreaterThanEqual(
                        authorization.userId,
                        authorization.token,
                        Date()) == null)
        {
            throw UnauthorizedException()
        }
        request.setUser(user)

        return true
    }

    private fun decode(encoded: String): String
    {
        try
        {
            val data = Base64.getDecoder().decode(encoded.removeWhiteSpace())
            return String(data, Charsets.US_ASCII)
        }
        catch (e: Exception)
        {
            throw BadRequestException("Failed to decode Base64 Authorization Header: " + (e.message ?: ""))
        }
    }

    private fun parseAuthorization(json: String): Authorization
    {
        try
        {
            return jacksonObjectMapper().readerFor(Authorization::class.java).readValue<Authorization>(json)
        }
        catch (e: Exception)
        {
            throw BadRequestException("Failed to decode Authorization Header: " + (e.message ?: ""))
        }
    }
}


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class UserAuthenticationRequired

fun HttpServletRequest.getUser() = this.getAttribute("User") as? User
fun HttpServletRequest.setUser(user: User) = this.setAttribute("User", user)

fun HttpServletRequest.getAccessToken() = this.getAttribute("token") as? UUID
fun HttpServletRequest.setAccessToken(token: UUID) = this.setAttribute("token", token)
