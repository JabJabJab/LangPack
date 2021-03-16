package jab.langpack.core.objects

import jab.langpack.core.LangPack

/**
 * The **LangString** class. TODO: Document.
 *
 * @author Jab
 */
class LangString : Definition<String> {

    /**
     * TODO: Document.
     *
     * @param pack
     * @param parent
     * @param value
     */
    constructor(pack: LangPack, parent: Group, value: String) : super(pack, parent, value)

    /**
     * TODO: Document.
     *
     * @param pack
     * @param value
     */
    constructor(pack: LangPack, value: String) : super(pack, null, value)

    override fun onWalk(): String {
        return walkString(raw)
    }

    override fun needsWalk(): Boolean {
        return stringNeedsWalk(raw)
    }

    override fun toString(): String = "LangString($value)"
}