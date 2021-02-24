package jab.spigot.language.util

import jab.spigot.language.LangField
import jab.spigot.language.LangPackage
import jab.spigot.language.Language

interface IStringProcessor {

    /**
     * TODO: Document
     *
     * @param string
     */
    fun getFields(string: String): Array<String>

    /**
     * TODO: Document
     *
     * @param field
     */
    fun formatField(field: String): String

    /**
     * TODO: Document
     *
     * @param string
     * @param fields
     */
    fun process(string: String, vararg fields: LangField): String

    /**
     * TODO: Document
     *
     * @param string
     * @param pkg
     * @param lang
     * @param fields
     *
     * @return
     */
    fun process(
        string: String,
        pkg: LangPackage,
        lang: Language = Language.ENGLISH,
        vararg fields: LangField
    ): String
}