package com.amgregoire.manga.http.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "chapters")
class Chapter : AuditModel()
{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false, columnDefinition = "uuid")
    @JsonIgnore
    lateinit var manga: Manga

    @NotBlank
    var title: String = ""

    @NotBlank
    var link: String = ""

}