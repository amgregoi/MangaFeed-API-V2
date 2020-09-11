package com.amgregoire.manga.http.repository

import com.amgregoire.manga.http.model.AccessToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AccessTokenRepository : JpaRepository<AccessToken, UUID>
{
    fun findOneByUserIdAndToken(userId: UUID, token: UUID): AccessToken?
    fun findOneByUserIdAndTokenAndExpiresAtGreaterThanEqual(userId: UUID, token: UUID, expiresAt: Date): AccessToken?
    fun findAllByUserIdAndExpiresAtGreaterThanEqual(userId: UUID, expiresAt: Date): Set<AccessToken>?
}
