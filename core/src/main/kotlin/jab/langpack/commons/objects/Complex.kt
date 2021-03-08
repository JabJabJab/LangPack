package jab.langpack.commons.objects

import jab.langpack.commons.LangArg
import jab.langpack.commons.LangPack
import jab.langpack.commons.Language
import jab.langpack.commons.processor.LangProcessor

/**
 * The ***Complex*** interface allows resolving for complex results for lang-pack.
 *
 * @author Jab
 */
interface Complex<E> {

    /**
     * Process the complex object using the lang-pack's [LangProcessor].
     *
     * @param pack The lang-pack instance.
     * @param lang The language to query.
     * @param args (Optional) Arguments to pass to the processor.
     *
     * @return Returns the processed result.
     */
    fun process(pack: LangPack, lang: Language, vararg args: LangArg): E

    /**
     * Process the complex object.
     *
     * @return Returns the processed result.
     */
    fun get(): E
}