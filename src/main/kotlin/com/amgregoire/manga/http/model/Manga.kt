package com.amgregoire.manga.http.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "mangas")
class Manga : AuditModel()
{
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = [
                CascadeType.PERSIST,
                CascadeType.MERGE],
            mappedBy = "mangas")
    @JsonIgnore
    lateinit var users: Set<User>

    @OneToMany(mappedBy = "manga", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    lateinit var chapters: Set<Chapter>

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false, columnDefinition = "uuid")
    lateinit var source: Source

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = [
                CascadeType.PERSIST,
                CascadeType.MERGE])
    @JoinTable(name = "manga_artists",
            joinColumns = [JoinColumn(name = "artist_id")],
            inverseJoinColumns = [JoinColumn(name = "manga_id")])
    var arist: Set<Artist> = setOf()

    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = [
                CascadeType.PERSIST,
                CascadeType.MERGE])
    @JoinTable(name = "manga_authors",
            joinColumns = [JoinColumn(name = "author_id")],
            inverseJoinColumns = [JoinColumn(name = "manga_id")])
    var author: Set<Author> = setOf()


    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = [
                CascadeType.PERSIST,
                CascadeType.MERGE])
    @JoinTable(name = "manga_genres",
            joinColumns = [JoinColumn(name = "genre_id")],
            inverseJoinColumns = [JoinColumn(name = "manga_id")])
    var genres: Set<Genre> = setOf()


    @NotBlank
    var name: String = ""

    @NotBlank
    var description: String = ""

    @NotBlank
    var link: String = ""

    @NotBlank
    var image: String = ""

    @NotBlank
    var status: String = ""

    @NotBlank
    var alternateNames: String = ""

    override fun equals(other: Any?): Boolean
    {
        if (other == null) return false
        return (other is User) && other.id == id
    }

    override fun hashCode() = id.hashCode()
}