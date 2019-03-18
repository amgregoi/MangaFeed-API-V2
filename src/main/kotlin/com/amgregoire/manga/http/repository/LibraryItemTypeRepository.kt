package com.amgregoire.manga.http.repository

import com.amgregoire.manga.http.model.LibraryItemType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface LibraryItemTypeRepository:JpaRepository<LibraryItemType, UUID>