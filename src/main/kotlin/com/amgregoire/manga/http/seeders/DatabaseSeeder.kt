package com.amgregoire.manga.http.seeders

import com.amgregoire.manga.http.model.LibraryItemType
import com.amgregoire.manga.http.model.Source
import com.amgregoire.manga.http.model.SourceType
import com.amgregoire.manga.http.repository.LibraryItemTypeRepository
import com.amgregoire.manga.http.repository.SourceRepository
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DatabaseSeeder(
        val libraryItemTypeRepository: LibraryItemTypeRepository,
        val sourceRepository: SourceRepository
)
{

    @EventListener
    fun seed(event: ContextRefreshedEvent)
    {
        seedLibraryTypes()
        seedSources()

        System.out.println("Seeding Complete")
    }

    private fun seedLibraryTypes()
    {
        if (libraryItemTypeRepository.count() > 0)
        {
            System.out.println("Library Type Seeding Not Required")
            return
        }

        // Save all listed LogTypes
        libraryItemTypeRepository.save(LibraryItemType("Reading", 0xFF3F51B5))
        libraryItemTypeRepository.save(LibraryItemType("Complete", 0xFF2DED4D))
        libraryItemTypeRepository.save(LibraryItemType("Plan to Read", 0xFFC4C4C4))
        libraryItemTypeRepository.save(LibraryItemType("On Hold", 0xFFFF3030))
    }

    private fun seedSources()
    {
        if(sourceRepository.count() > 0)
        {
            System.out.println("Source Seeding Not Required")
            return
        }

        for(source in SourceType.values())
        {
            sourceRepository.save(Source(source))
        }
    }


}