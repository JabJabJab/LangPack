package jab.langpack.core.objects.definition

import jab.langpack.core.LangPack
import jab.langpack.core.objects.LangGroup
import jab.langpack.core.objects.formatter.FieldFormatter

/**
 * The **StringDefinition** class. TODO: Document.
 *
 * @author Jab
 */
class StringDefinition : LangDefinition<String> {

    /**
     * TODO: Document.
     *
     * @param pack
     * @param parent
     * @param value
     */
    constructor(pack: LangPack, parent: LangGroup, value: String) : super(pack, parent, value)

    /**
     * TODO: Document.
     *
     * @param pack
     * @param value
     */
    constructor(pack: LangPack, value: String) : super(pack, null, value)

    override fun onWalk(): String {
        return walk(raw)
    }

    override fun needsWalk(formatter: FieldFormatter): Boolean {
        return formatter.needsWalk(raw)
    }

    override fun toString(): String = "LangString($value)"
}