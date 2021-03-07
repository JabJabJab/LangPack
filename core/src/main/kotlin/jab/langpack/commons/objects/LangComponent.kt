package jab.langpack.commons.objects

import jab.langpack.commons.LangArg
import jab.langpack.commons.LangPack
import jab.langpack.commons.Language
import net.md_5.bungee.api.chat.TextComponent

/**
 * The <i>LangComponent</i> interface is for complex objects that requires a package
 *   reference when processing data into a string for LangPackage use.
 *
 *   @author Jab
 */
interface LangComponent {

    /**
     * Processes the object into a rendered string for use in LangPackage.
     *
     * @param pkg The package instance.
     * @param lang The language used to process the string.
     * @param args Optional fields passed to the langPackage.
     */
    fun process(pkg: LangPack, lang: Language, vararg args: LangArg): TextComponent

    /**
     * TODO: Document.
     */
    fun get(): TextComponent
}
