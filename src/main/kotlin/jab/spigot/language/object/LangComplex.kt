package jab.spigot.language.`object`

import jab.spigot.language.LangArg
import jab.spigot.language.LangPackage
import jab.spigot.language.Language

/**
 * The <i>LangProcessable</i> interface is for complex objects that requires a package
 *   reference when processing data into a string for LangPackage use.
 *
 *   @author Jab
 */
interface LangComplex {

    /**
     * Processes the object into a rendered string for use in LangPackage.
     *
     * @param pkg The package instance.
     * @param lang The language used to process the string.
     * @param args Optional fields passed to the langPackage.
     */
    fun process(pkg: LangPackage, lang: Language, vararg args: LangArg): String

    /**
     * TODO: Document.
     */
    fun get(): String
}