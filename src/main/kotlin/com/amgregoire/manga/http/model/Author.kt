package com.amgregoire.manga.http.model

import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "authors")
class Author : AuditModel()
{
    @NotBlank
    var author: String = ""
}