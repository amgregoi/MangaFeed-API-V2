package com.amgregoire.manga.http.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class EncryptService
{
    @Value("\${user.password.salt}")
    lateinit var salt: String

    fun encrypt(value: String): String
    {
        return BCryptPasswordEncoder().encode(salt + value)
    }

    fun matches(raw: String, encrypted: String): Boolean
    {
        return BCryptPasswordEncoder().matches(salt + raw, encrypted)
    }
}
