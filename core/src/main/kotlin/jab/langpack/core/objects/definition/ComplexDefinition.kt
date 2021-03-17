package jab.langpack.core.objects.definition

import jab.langpack.core.LangPack
import jab.langpack.core.objects.LangGroup
import jab.langpack.core.objects.complex.Complex
import jab.langpack.core.processor.FieldFormatter

/**
 * The **ComplexDefinition** class. TODO: Document.
 *
 * @author Jab
 */
class ComplexDefinition : LangDefinition<Complex<*>> {

    /**
     * TODO: Document.
     *
     * @param pack
     * @param parent
     * @param value
     */
    constructor(pack: LangPack, parent: LangGroup, value: Complex<*>) : super(pack, parent, value)

    /**
     * TODO: Document.
     *
     * @param pack
     * @param value
     */
    constructor(pack: LangPack, value: Complex<*>) : super(pack, null, value)

    override fun onWalk(): Complex<*> = value.walk(this)

    override fun needsWalk(formatter: FieldFormatter): Boolean = raw.needsWalk(formatter)

    override fun toString(): String = "LangComplex(value=$value)"
}