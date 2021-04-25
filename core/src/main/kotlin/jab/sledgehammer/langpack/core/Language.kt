package jab.sledgehammer.langpack.core

import java.util.*

class Language(val locale: Locale, val fallback: Language? = null) {
    val rawLocale = if (locale.country.isNotEmpty()) "${locale.language}_${locale.country}" else locale.language!!

    override fun toString(): String = "Language($rawLocale)"
}