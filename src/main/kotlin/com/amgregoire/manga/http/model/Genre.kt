package com.amgregoire.manga.http.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "genres", uniqueConstraints = [UniqueConstraint(columnNames = ["name", "source_id"])])
class Genre : AuditModel()
{
    @NotBlank
    var name: String = ""

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false, columnDefinition = "uuid")
    @JsonIgnore
    lateinit var source: Source
}