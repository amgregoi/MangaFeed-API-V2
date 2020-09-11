package com.amgregoire.manga.http.controller

import com.amgregoire.manga.http.repository.ChapterRepository
import com.amgregoire.manga.http.repository.MangaRepository
import org.springframework.web.bind.annotation.RestController

@RestController
class ArtistController(
        private val mangaRepository: MangaRepository,
        private val chapterRepository: ChapterRepository
)
{
    companion object
    {
        const val BASE_URL = "/name"
    }

    /***
     * Add name
     */

}