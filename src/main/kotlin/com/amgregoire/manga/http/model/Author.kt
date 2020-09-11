package com.amgregoire.manga.http.model

import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "authors", uniqueConstraints = [UniqueConstraint(columnNames = ["name"])])
class Author : AuditModel()
{
    @NotBlank
    var name: String = ""
}