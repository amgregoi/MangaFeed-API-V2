package com.amgregoire.manga.http.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.util.*
import javax.persistence.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
@JsonIgnoreProperties(value = ["createdAt", "updatedAt"], allowGetters = true)
abstract class AuditModel: Serializable {
    @Id
    @GeneratedValue
    @Column( columnDefinition = "uuid", updatable = false )
    var id = UUID(0, 0)
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    var createdAt = Date()
  
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    var updatedAt = Date()

    override fun equals(other: Any?): Boolean
    {
        if(other == null) return false
        return (other is AuditModel) && other.id == id
    }

    override fun hashCode() = id.hashCode()

}