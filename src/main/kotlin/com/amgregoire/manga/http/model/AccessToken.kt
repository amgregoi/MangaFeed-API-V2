package com.amgregoire.manga.http.model

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "access_token")
class AccessToken : AuditModel()
{
    @GeneratedValue
    @Column( columnDefinition = "uuid")
    var token: UUID = UUID.randomUUID()

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires_at", nullable = false)
    var expiresAt = Date(Date().time + (365 * 24 * 60 * 60 * 1000L)) // One year access token

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "uuid")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    lateinit var user: User

    fun expireToken()
    {
        expiresAt = Date()
    }
}
