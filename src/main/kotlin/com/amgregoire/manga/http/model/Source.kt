package com.amgregoire.manga.http.model

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "sources")
class Source(
        @Enumerated(EnumType.STRING)
        @NotBlank
        var source: SourceType = SourceType.Unknown
) : AuditModel()

enum class SourceType
{
    FunManga, Wuxia, ReadLight, MangaHere, MangaEden, Unknown
}