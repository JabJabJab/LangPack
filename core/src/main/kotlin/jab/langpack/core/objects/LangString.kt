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
     */
    private val raw: String

    /**
     * TODO: Document.
     *
     * @param pack
     * @param parent
     * @param value
     */
    constructor(pack: LangPack, parent: Group, value: String) : super(pack, parent, value) {
        raw = value
    }

    /**
     * TODO: Document.
     *
     * @param pack
     * @param value
     */
    constructor(pack: LangPack, value: String) : super(pack, null, value) {
        raw = value
    }


    override fun walk() {

        val processor = pack.processor
        val formatter = pack.formatter

        value = raw

        val fields = formatter.getFields(value)

        for (field in fields) {
            if (formatter.needsToResolve(field)) {
                print("Resolving $field..")
                val language = parent?.language ?: pack.defaultLang
                val resolvedField = processor.process(value, pack, language)
                println("Result: $resolvedField")
                value = value.replace(field, resolvedField)
            }
        }
    }

    override fun toString(): String {
        return "LangString(value='$value')"
    }
}