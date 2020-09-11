package com.amgregoire.manga.http.services

import com.amgregoire.manga.http.controller.MangaController
import com.amgregoire.manga.http.exception.ResourceNotFoundException
import com.amgregoire.manga.http.model.Artist
import com.amgregoire.manga.http.model.Author
import com.amgregoire.manga.http.model.Genre
import com.amgregoire.manga.http.model.Manga
import com.amgregoire.manga.http.repository.*
import org.springframework.stereotype.Service
import java.util.*


interface IMangaService
{
    fun getMangaFromLinkAndSource(link: String, source: UUID): Manga

    fun verifyMangaExists(mangaRequest: MangaController.MangaRequest): Manga?

    fun createManga(mangaRequest: MangaController.MangaRequest): Manga

    fun updateManga(manga: Manga, mangaRequest: MangaController.MangaRequest): Manga
}

@Service
class MangaService(val mangaRepository: MangaRepository,
                   val sourceRepository: SourceRepository,
                   val genreRepository: GenreRepository,
                   val artistRepository: ArtistRepository,
                   val authorRepository: AuthorRepository) : IMangaService
{

    override fun getMangaFromLinkAndSource(link: String, sourceId: UUID): Manga
    {
        val source = sourceRepository.findById(sourceId).orElseThrow { ResourceNotFoundException("No sourceId found with id $sourceId") }
        return mangaRepository.findOneByLinkAndSource(link, source) ?: throw ResourceNotFoundException("Manga not found with link [$link] and sourceId id [${source.id}]")
    }

    override fun verifyMangaExists(mangaRequest: MangaController.MangaRequest): Manga?
    {
        val source = sourceRepository.findById(mangaRequest.source).orElseThrow { ResourceNotFoundException("No source found with id ${mangaRequest.source}") }
        return mangaRepository.findOneByLinkAndSource(mangaRequest.link, source)
    }

    override fun createManga(mangaRequest: MangaController.MangaRequest): Manga
    {
        val manga = mangaRepository.save(Manga().apply {
            mangaRequest.link?.let { link = it }
            mangaRequest.name?.let { name = it }
            mangaRequest.description?.let { description = it }
            mangaRequest.image?.let { image = it }
            mangaRequest.status?.let { status = it }
            mangaRequest.alternate?.let { alternateNames = it }
            mangaRequest.source?.let {
                source = sourceRepository.findById(it).orElseThrow { ResourceNotFoundException("Source not found with id ${mangaRequest.source}") }
            }
        })

        mangaRequest.genres?.let {
            for (iGenre in it)
            {
                val genre = genreRepository.findOneByNameAndSource(iGenre, manga.source) ?: genreRepository.save(Genre().apply { name = iGenre; source = manga.source })
                if (!manga.genres.contains(genre)) manga.genres = manga.genres.plus(genre)
            }
        }

        mangaRequest.artist?.let {
            for (iArtist in it)
            {
                val artist = artistRepository.findOneByName(iArtist) ?: artistRepository.save(Artist().apply { name = iArtist })
                if (!manga.artists.contains(artist)) manga.artists = manga.artists.plus(artist)
            }
        }

        mangaRequest.author?.let {
            for (iAuthor in it)
            {
                val author = authorRepository.findOneByName(iAuthor) ?: authorRepository.save(Author().apply { name = iAuthor })
                if (!manga.authors.contains(author)) manga.authors = manga.authors.plus(author)
            }
        }

        return manga
    }

    override fun updateManga(manga: Manga, mangaRequest: MangaController.MangaRequest): Manga
    {
        manga.apply {
            mangaRequest.link.let { link = it }
            mangaRequest.name?.let { name = it }
            mangaRequest.description?.let { description = it }
            mangaRequest.image?.let { image = it }
            mangaRequest.status?.let { status = it }
            mangaRequest.alternate?.let { alternateNames = it }
            mangaRequest.source.let {
                source = sourceRepository.findById(it).orElseThrow { ResourceNotFoundException("Source not found with id ${mangaRequest.source}") }
            }
        }

        mangaRequest.genres?.let {
            for (iGenre in it)
            {
                var genre = genreRepository.findOneByNameAndSource(iGenre, manga.source) ?: genreRepository.save(Genre().apply { name = iGenre; source = manga.source })
                if (!manga.genres.contains(genre)) manga.genres = manga.genres.plus(genre)
            }
        }

        mangaRequest.artist?.let {
            for (iArtist in it)
            {
                var artist = artistRepository.findOneByName(iArtist) ?: artistRepository.save(Artist().apply { name = iArtist })
                if (!manga.artists.contains(artist)) manga.artists = manga.artists.plus(artist)
            }
        }

        mangaRequest.author?.let {
            for (iAuthor in it)
            {
                val author = authorRepository.findOneByName(iAuthor) ?: authorRepository.save(Author().apply { name = iAuthor })
                if (!manga.authors.contains(author)) manga.authors = manga.authors.plus(author)
            }
        }

        return manga
    }
}
