package com.amgregoire.manga.http.model

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*


@Entity
@Table(name = "mangas", uniqueConstraints = [UniqueConstraint(columnNames = ["link", "source_id"])])
class Manga : AuditModel()
{
    @ManyToMany(fetch = FetchType.EAGER,
            cascade = [
                CascadeType.PERSIST,
                CascadeType.MERGE],
            mappedBy = "mangas")
    @JsonIgnore
    lateinit var users: Set<User>

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "manga", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    var chapters: Set<Chapter> = setOf()

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_id", nullable = false, columnDefinition = "uuid")
    @JsonIgnore
    lateinit var source: Source

    @JsonIgnore
    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = [
                CascadeType.PERSIST,
                CascadeType.MERGE])
    @JoinTable(name = "manga_artists",
            joinColumns = [JoinColumn(name = "artist_id")],
            inverseJoinColumns = [JoinColumn(name = "manga_id")])
    var artists: Set<Artist> = setOf()

    @JsonIgnore
    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = [
                CascadeType.PERSIST,
                CascadeType.MERGE])
    @JoinTable(name = "manga_authors",
            joinColumns = [JoinColumn(name = "author_id")],
            inverseJoinColumns = [JoinColumn(name = "manga_id")])
    var authors: Set<Author> = setOf()

    @JsonIgnore
    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = [
                CascadeType.PERSIST,
                CascadeType.MERGE])
    @JoinTable(name = "manga_genres",
            joinColumns = [JoinColumn(name = "genre_id")],
            inverseJoinColumns = [JoinColumn(name = "manga_id")])
    var genres: Set<Genre> = setOf()

    var name: String = ""

    var description: String = ""

    var link: String = ""

    var image: String = ""

    var status: String = ""

    var alternateNames: String = ""

    @Transient
    @JsonGetter(value = "genres")
    fun getGenre(): String = genres.joinToString { it.name }

    @Transient
    @JsonGetter(value = "artists")
    fun getArtist(): String = artists.joinToString { it.name }

    @Transient
    @JsonGetter(value = "authors")
    fun getAuthor(): String = authors.joinToString { it.name }

    @Transient
    @JsonGetter(value = "source")
    fun getSourceName(): String = source.source.name

    override fun equals(other: Any?): Boolean
    {
        if (other == null) return false
        return (other is Manga) && other.id == id
    }

    override fun hashCode() = id.hashCode()
}
