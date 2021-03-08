package jab.langpack.commons.objects

import jab.langpack.commons.LangArg
import jab.langpack.commons.LangPack
import jab.langpack.commons.Language

/**
 * The **LangComplex** interface is for complex objects that requires a package
 *   reference when processing data into a string for [LangPack] use.
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
    fun process(pkg: LangPack, lang: Language, vararg args: LangArg): String

    /**
     * TODO: Document.
     */
    fun get(): String
}
