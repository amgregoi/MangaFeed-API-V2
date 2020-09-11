package com.amgregoire.manga.http.configuration

import com.amgregoire.manga.http.interceptor.UserAuthenticationInterceptor
import com.amgregoire.manga.http.repository.AccessTokenRepository
import com.amgregoire.manga.http.repository.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class InterceptorConfigurer(
        val userRepository: UserRepository,
        val accessTokenRepository: AccessTokenRepository
): WebMvcConfigurer {

    @Bean
    fun exposeUserAuthenticationInterceptor() = UserAuthenticationInterceptor(userRepository, accessTokenRepository)

    override fun addInterceptors(registry: InterceptorRegistry) {
        super.addInterceptors(registry)

        registry.addInterceptor(exposeUserAuthenticationInterceptor())
    }
}

