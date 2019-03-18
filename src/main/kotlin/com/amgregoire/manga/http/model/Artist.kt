package com.amgregoire.manga.http.model

import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "artists")
class Artist : AuditModel()
{
    @NotBlank
    var artist: String = ""
}