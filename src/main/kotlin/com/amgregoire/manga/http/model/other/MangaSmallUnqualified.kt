package com.amgregoire.manga.http.model.other

import java.util.*

data class MangaSmallUnqualified(val link: String, val source: UUID, val followType: UUID?)
data class MangaSmallQualified(val id: UUID, val followType: UUID?)
