package com.asledgehammer.langpack.core

import java.util.*

/**
 * TODO: Document.
 *
 * (Primary identifier is based on Locale objects rather than Strings)
 *
 * @author Jab
 *
 * @property locale
 * @property fallback
 */
class Language(val locale: Locale, val fallback: Language? = null) {

    /**
     * The string-form of the locale.
     */
    val rawLocale = if (locale.country.isNotEmpty()) "${locale.language}_${locale.country}" else locale.language!!

    override fun toString(): String = "Language($rawLocale)"
}