package com.amgregoire.manga.http.interceptor

import com.amgregoire.manga.http.exception.BadRequestException
import com.amgregoire.manga.http.exception.ResourceNotFoundException
import com.amgregoire.manga.http.exception.UnauthorizedException
import com.amgregoire.manga.http.model.User
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
        val userRepository: UserRepository
) : HandlerInterceptor {

    data class Authorization(val userId: UUID)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // Expect that the handler is a function
        if (handler !is HandlerMethod) throw BadRequestException("")

        // Skip authorization if the function is not annotated with @ApplicationAuthenticationRequired
        handler.method.annotations.find { it is UserAuthenticationRequired } ?: return true

        // Fetch, decode, and parse authorization object from authorization header
        val header = request.getHeader("Authorization") ?: throw UnauthorizedException()
        val authorization = parseAuthorization(decode(header))

        // Fetch application with given id
        val user = userRepository
                .findById(authorization.userId)
                .orElseThrow { ResourceNotFoundException("User not found with id ${authorization.userId}") }

        request.setUser(user)

        return true
    }

    private fun decode(encoded: String): String {
        val data = Base64.getDecoder().decode(encoded)
        try {
            return String(data)
        } catch (e: Exception) {
            throw BadRequestException("Failed to decode Base64 Authorization Header: " + (e.message ?: ""))
        }
    }

    private fun parseAuthorization(json: String): Authorization {
        try {
            return jacksonObjectMapper().readerFor(Authorization::class.java).readValue<Authorization>(json)
        } catch (e: Exception) {
            throw BadRequestException("Failed to decode Authorization Header: " + (e.message ?: ""))
        }
    }
}


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class UserAuthenticationRequired

fun HttpServletRequest.getUser() = this.getAttribute("User") as? User
fun HttpServletRequest.setUser(user: User) = this.setAttribute("User", user)
