package com.amgregoire.manga.http.repository

import com.amgregoire.manga.http.model.Source
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SourceRepository:JpaRepository<Source, UUID>