package com.amgregoire.manga.http.controller

import com.amgregoire.manga.http.exception.BadRequestException
import com.amgregoire.manga.http.exception.ResourceNotFoundException
import com.amgregoire.manga.http.model.Chapter
import com.amgregoire.manga.http.model.Manga
import com.amgregoire.manga.http.repository.ChapterRepository
import com.amgregoire.manga.http.repository.MangaRepository
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
class MangaController(
        private val mangaRepository: MangaRepository,
        private val chapterRepository: ChapterRepository
)
{
    companion object
    {
        const val BASE_URL = "/manga"
        const val MODEL_URL_PARAMETER = "/{mangaId}"
    }

    @PostMapping(BASE_URL)
    fun createManga(
            request: HttpServletRequest,
            @RequestBody mangaRequest: Manga
    ): Manga?
    {
        return mangaRepository.save(mangaRequest)
    }

    @PostMapping(BASE_URL + MODEL_URL_PARAMETER)
    fun updateManga(
            request: HttpServletRequest,
            @RequestBody mangaRequest: Manga
    ): Manga?
    {
        val manga = mangaRepository.findById(mangaRequest.id).orElseThrow {
            ResourceNotFoundException("Manga with id ${mangaRequest.id} not found")
        }

        manga.link = mangaRequest.link

        // TODO :: Handle the rest of the manga model updates
        // Specifically checking the other model attributes (Artist, Author, etc..)

        return mangaRepository.save(mangaRequest)
    }

    @PostMapping("$BASE_URL$MODEL_URL_PARAMETER/chapter")
    fun addChapter(
            request: HttpServletRequest,
            @PathVariable mangaId: UUID,
            @RequestBody chapterRequest: Chapter
    ): Manga?
    {
        val manga = mangaRepository.findById(mangaId).orElseThrow { ResourceNotFoundException("Manga with id $mangaId not found") }

        if (chapterRepository.findById(chapterRequest.id).isPresent) throw BadRequestException("Chapter with link ${chapterRequest.link} already exists")

        val chapter = chapterRepository.save(chapterRequest)

        manga.chapters = manga.chapters.plus(chapter)

        return mangaRepository.save(manga)
    }


}