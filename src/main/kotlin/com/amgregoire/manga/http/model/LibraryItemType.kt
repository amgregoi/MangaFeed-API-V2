package com.amgregoire.manga.http.model

import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "library_item_types")
class LibraryItemType(
        @NotBlank
        var type: String = "",

        var color: Long = 0xffffffff
) : AuditModel()
