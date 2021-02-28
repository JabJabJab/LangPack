package jab.spigot.language.util

import jab.spigot.language.LangArg
import jab.spigot.language.LangPackage
import jab.spigot.language.Language
import net.md_5.bungee.api.chat.TextComponent

/**
 * The <i>StringProcessor</i> interface is for implementing syntax formats for use in [LangPackage].
 *
 * @author Jab
 */
interface StringProcessor {

    /**
     * Processes a text component, inserting arguments and fields set in the LangPackage.
     *
     * @param textComponent The text component to process.
     * @param pkg The package instance.
     * @param lang The language context.
     * @param args (Optional) The arguments to process into the string.
     *
     * @return Returns the processed string.
     */
    fun processComponent(
        textComponent: TextComponent,
        pkg: LangPackage,
        lang: Language = Language.ENGLISH,
        vararg args: LangArg
    ): TextComponent

    /**
     * Processes a string, inserting arguments and fields set in the LangPackage.
     *
     * @param string The string to process.
     * @param pkg The package instance.
     * @param lang The language context.
     * @param args (Optional) The arguments to process into the string.
     *
     * @return Returns the processed string.
     */
    fun processString(
        string: String,
        pkg: LangPackage,
        lang: Language = Language.ENGLISH,
        vararg args: LangArg
    ): String

    /**
     * Processes a string, inserting provided arguments.
     *
     * @param string The string to process.
     * @param args (Optional) The arguments to process into the string.
     *
     * @return Returns the processed string.
     */
    fun processString(string: String, vararg args: LangArg): String

    /**
     * Parses a string into fields.
     *
     * @param string The unprocessed string to parse.
     *
     * @return Returns the fields in the unprocessed string.
     */
    fun getFields(string: String): Array<String>
}