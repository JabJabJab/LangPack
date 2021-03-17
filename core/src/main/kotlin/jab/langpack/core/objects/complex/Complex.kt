package jab.langpack.core.objects.complex

import jab.langpack.core.objects.LangArg
import jab.langpack.core.LangPack
import jab.langpack.core.Language
import jab.langpack.core.objects.definition.LangDefinition
import jab.langpack.core.objects.formatter.FieldFormatter
import jab.langpack.core.processor.LangProcessor

/**
 * TODO: Update documentation to reflect Definition API update.
 *
 * The **Complex** interface allows resolving for complex results for lang-pack.
 *
 * @author Jab
 */
interface Complex<E> {

    /**
     * TODO: Document.
     *
     * @param definition
     */
    fun walk(definition: LangDefinition<*>): Complex<E>

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
     * TODO: Document.
     *
     * @param formatter
     *
     * @return
     */
    fun needsWalk(formatter: FieldFormatter): Boolean

    /**
     * Process the complex object.
     *
     * @return Returns the processed result.
     */
    fun get(): E
}
