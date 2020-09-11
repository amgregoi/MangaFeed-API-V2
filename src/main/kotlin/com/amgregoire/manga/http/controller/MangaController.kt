package com.amgregoire.manga.http.controller

import com.amgregoire.manga.http.exception.ResourceNotFoundException
import com.amgregoire.manga.http.model.Chapter
import com.amgregoire.manga.http.model.Manga
import com.amgregoire.manga.http.model.Source
import com.amgregoire.manga.http.model.SourceType
import com.amgregoire.manga.http.repository.ChapterRepository
import com.amgregoire.manga.http.repository.MangaRepository
import com.amgregoire.manga.http.services.MangaService
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest


@RestController
class MangaController(
        private val mangaRepository: MangaRepository,
        private val chapterRepository: ChapterRepository,
        private val mangaService: MangaService
)
{
    companion object
    {
        const val BASE_URL = "/manga"
        const val MODEL_URL_PARAMETER = "/{mangaId}"
    }

    @GetMapping(BASE_URL)
    fun getMangaTest():Manga
    {
        return Manga().apply {
            name = "This is a test"
            description = "You can see this is a test because the description says so"
            source = Source(SourceType.Unknown, "localhost")
            authors = setOf()
            artists = setOf()
        }
    }

    @GetMapping("/")
    fun getDefault2() = "Fable is running"

    @PostMapping(BASE_URL)
    fun createManga(
            request: HttpServletRequest,
            @RequestBody mangaRequest: MangaRequest
    ): Manga
    {
        val preExist = mangaService.verifyMangaExists(mangaRequest)
        if (preExist != null) return preExist

        val manga = mangaService.createManga(mangaRequest)

        return mangaRepository.save(manga)
    }

    @PutMapping(BASE_URL + MODEL_URL_PARAMETER)
    fun updateManga(
            request: HttpServletRequest,
            @PathVariable("mangaId") mangaId: UUID,
            @RequestBody mangaRequest: MangaRequest
    ): Manga
    {
        val manga = mangaRepository.findById(mangaId).orElseThrow {
            ResourceNotFoundException("Manga not found with id $mangaId")
        }

        val updatedManga = mangaService.updateManga(manga, mangaRequest)

        return mangaRepository.save(updatedManga)
    }

    @PostMapping("$BASE_URL$MODEL_URL_PARAMETER/chapter")
    fun addChapter(
            request: HttpServletRequest,
            @PathVariable mangaId: UUID,
            @RequestBody chapterRequest: Chapter
    ): Set<Chapter>?
    {
        var manga = mangaRepository.findById(mangaId).orElseThrow { ResourceNotFoundException("Manga with id $mangaId not found") }

        val preChapter = chapterRepository.findOneByLink(chapterRequest.link)
        if(preChapter != null && manga.chapters.contains(preChapter)) return manga.chapters

        val chapter = chapterRepository.save(chapterRequest.apply { this.manga = manga })

        manga.chapters = manga.chapters.plus(chapter)
        manga = mangaRepository.save(manga)

        return manga.chapters
    }

    data class MangaRequest(
            val link: String,
            val source: UUID,
            val name: String?,
            val description: String?,
            val image: String?,
            val status: String?,
            val alternate: String?,
            val genres: ArrayList<String>?,
            val author: ArrayList<String>?,
            val artist: ArrayList<String>?
    )

}
