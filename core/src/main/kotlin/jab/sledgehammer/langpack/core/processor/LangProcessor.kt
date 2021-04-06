package jab.sledgehammer.langpack.core.processor

import jab.sledgehammer.langpack.core.LangPack
import jab.sledgehammer.langpack.core.Language
import jab.sledgehammer.langpack.core.objects.LangArg
import jab.sledgehammer.langpack.core.objects.LangGroup
import java.util.ArrayList

/**
 * **LangProcessor** implements syntax formats for use in [LangPack].
 *
 * @author Jab
 */
interface LangProcessor {

    /**
     * Processes a string, inserting arguments and fields set in the lang-pack.
     *
     * The scope will be at the package level.
     *
     * @param string The string to process.
     * @param pack The lang-pack instance.
     * @param lang The language context.
     * @param args (Optional) The arguments to process into the string.
     *
     * @return Returns the processed string.
     */
    fun process(string: String, pack: LangPack, lang: Language, vararg args: LangArg): String {
        return process(string, pack, lang, null, *args)
    }

    /**
     * Processes a string, inserting arguments and fields set in the lang-pack.
     *
     * @param string The string to process.
     * @param pack The lang-pack instance.
     * @param lang The language context.
     * @param context (Optional) The scope of the string.
     * @param args (Optional) The arguments to process into the string.
     *
     * @return Returns the processed string.
     */
    fun process(
        string: String,
        pack: LangPack,
        lang: Language,
        context: LangGroup? = null,
        vararg args: LangArg,
    ): String

    /**
     * Processes a string, inserting provided arguments.
     *
     * @param string The string to process.
     * @param args (Optional) The arguments to process into the string.
     *
     * @return Returns the processed string.
     */
    fun process(string: String, vararg args: LangArg): String

    /**
     * Colors a list of strings to the Minecraft color-code specifications using an alternative color-code.
     *
     * @param strings The strings to color.
     * @param colorCode (Default: '&') The alternative color-code to process.
     *
     * @return Returns the colored string.
     */
    fun color(strings: List<String>, colorCode: Char = '&'): List<String> {
        val coloredList = ArrayList<String>()
        for (string in strings) {
            coloredList.add(color(string, colorCode))
        }
        return coloredList
    }

    /**
     * TODO: Implement.
     *
     * @param string The string to color.
     * @param colorCode  The alternative color-code to process.
     *
     * @return Returns the colored string.
     */
    fun color(string: String, colorCode: Char = '&'): String
}
