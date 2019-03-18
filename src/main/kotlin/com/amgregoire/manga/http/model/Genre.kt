package com.amgregoire.manga.http.model

import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "genres")
class Genre : AuditModel()
{
    @NotBlank
    var genre: String = ""
}