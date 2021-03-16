package jab.langpack.core.processor

import jab.langpack.core.objects.LangArg
import jab.langpack.core.LangPack
import jab.langpack.core.Language

import net.md_5.bungee.api.chat.TextComponent

/**
 * TODO: Update documentation to reflect Definition API update.
 *
 * The **LangProcessor** interface is for implementing syntax formats for use in [LangPack].
 *
 * @author Jab
 */
interface Processor {

    /**
     * Processes a text component, inserting arguments and fields set in the lang-pack.
     *
     * @param component The text component to process.
     * @param pack The package instance.
     * @param lang The language context.
     * @param args (Optional) The arguments to process into the string.
     *
     * @return Returns the processed string.
     */
    fun process(component: TextComponent, pack: LangPack, lang: Language, vararg args: LangArg): TextComponent

    /**
     * Processes a text component, inserting provided arguments.
     *
     * @param component The component to process.
     * @param args (Optional) The arguments to process into the string.
     *
     * @return Returns the processed component.
     */
    fun process(component: TextComponent, vararg args: LangArg): TextComponent

    /**
     * Processes a string, inserting arguments and fields set in the lang-pack.
     *
     * @param string The string to process.
     * @param pack The lang-pack instance.
     * @param lang The language context.
     * @param args (Optional) The arguments to process into the string.
     *
     * @return Returns the processed string.
     */
    fun process(string: String, pack: LangPack, lang: Language, vararg args: LangArg): String

    /**
     * Processes a string, inserting provided arguments.
     *
     * @param string The string to process.
     * @param args (Optional) The arguments to process into the string.
     *
     * @return Returns the processed string.
     */
    fun process(string: String, vararg args: LangArg): String
}
