package com.amgregoire.manga.http.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "sources")
class Source(
        @Enumerated(EnumType.STRING)
        @NotBlank
        var source: SourceType = SourceType.Unknown,

        var url: String = ""
) : AuditModel()
{
    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = [
                CascadeType.PERSIST,
                CascadeType.MERGE])
    @JoinTable(name = "source_genres",
            joinColumns = [JoinColumn(name = "source_id")],
            inverseJoinColumns = [JoinColumn(name = "genre_id")])
    @JsonIgnore
    var genres: Set<Genre> = setOf()
}

enum class SourceType
{
    FunManga, Wuxia, ReadLight, MangaHere, MangaEden, Unknown
}
