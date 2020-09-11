package com.amgregoire.manga.http.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "users")
class User : AuditModel()
{
    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = [
                CascadeType.PERSIST
            ])
    @JoinTable(name = "user_library",
            joinColumns = [JoinColumn(name = "user_id")],
            inverseJoinColumns = [JoinColumn(name = "manga_id")])
    var mangas: Set<Manga> = setOf()

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = [
                CascadeType.PERSIST
            ])
    @JoinTable(name = "user_reading",
            joinColumns = [JoinColumn(name = "user_id")],
            inverseJoinColumns = [JoinColumn(name = "manga_id")])
    var reading: Set<Manga> = setOf()

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = [
                CascadeType.PERSIST
            ])
    @JoinTable(name = "user_complete",
            joinColumns = [JoinColumn(name = "user_id")],
            inverseJoinColumns = [JoinColumn(name = "manga_id")])
    var complete: Set<Manga> = setOf()

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = [
                CascadeType.PERSIST
            ])
    @JoinTable(name = "user_on_hold",
            joinColumns = [JoinColumn(name = "user_id")],
            inverseJoinColumns = [JoinColumn(name = "manga_id")])
    var onHold: Set<Manga> = setOf()

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = [
                CascadeType.PERSIST
            ])
    @JoinTable(name = "user_plan_to_read",
            joinColumns = [JoinColumn(name = "user_id")],
            inverseJoinColumns = [JoinColumn(name = "manga_id")])
    var planToRead: Set<Manga> = setOf()

    @NotBlank
    @Column(unique = true, length = 48)
    var email: String = ""

    @NotBlank
    @JsonIgnore
    var password: String = ""
}
