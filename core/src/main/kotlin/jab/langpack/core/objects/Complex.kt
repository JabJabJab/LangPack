package jab.langpack.core.objects

import jab.langpack.core.LangArg
import jab.langpack.core.LangPack
import jab.langpack.core.Language
import jab.langpack.core.processor.Processor

/**
 * The ***Complex*** interface allows resolving for complex results for lang-pack.
 *
 * @author Jab
 */
interface Complex<E> {

    /**
     * TODO: Document.
     *
     * @param definition
     */
    fun walk(definition: Definition<*>): Complex<E>
    
    /**
     * Process the complex object using the lang-pack's [Processor].
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

    fun needsWalk(): Boolean
}
