package com.amgregoire.manga.http.extension

fun String.removeWhiteSpace() = this.trim().replace("\\s".toRegex(), "")
