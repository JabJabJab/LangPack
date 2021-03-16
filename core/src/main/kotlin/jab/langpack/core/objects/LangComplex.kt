package jab.langpack.core.objects

import jab.langpack.core.LangPack

/**
 * The **LangComplex** class. TODO: Document.
 *
 * @author Jab
 */
class LangComplex : Definition<Complex<*>> {

    /**
     * TODO: Document.
     *
     * @param pack
     * @param parent
     * @param value
     */
    constructor(pack: LangPack, parent: Group, value: Complex<*>) : super(pack, parent, value)

    /**
     * TODO: Document.
     *
     * @param pack
     * @param value
     */
    constructor(pack: LangPack, value: Complex<*>) : super(pack, null, value)

    override fun onWalk(): Complex<*> = value.walk(this)

    override fun toString(): String = "LangComplex(value=$value)"

    override fun needsWalk(): Boolean {
        // TODO: Implement.
        return raw.needsWalk()
    }
}