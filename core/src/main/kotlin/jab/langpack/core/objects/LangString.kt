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

        val processor = pack.processor
        val formatter = pack.formatter

        var value = raw

        val fields = formatter.getFields(value)
        val language = parent?.language ?: pack.defaultLang

        for (field in fields) {
            if (formatter.isResolve(field)) {

                var fieldDefinition = pack.resolve(language, field)
                if (fieldDefinition == null) {
                    fieldDefinition = LangPack.global.resolve(language, field)
                }

                // If the field cannot resolve, set the placeholder.
                if (fieldDefinition == null) {
                    value = value.replace(field, formatter.strip(field))
                    continue
                }

                val resolvedField = processor.process(value, pack, language)
                value = value.replace(field, resolvedField)
            }
        }

        return value
    }

    override fun toString(): String = "LangString(value='$value')"
}