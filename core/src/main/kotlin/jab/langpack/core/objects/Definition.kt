package jab.langpack.core.objects

import jab.langpack.core.LangPack

/**
 * The **Definition** class. TODO: Document.
 *
 * @property pack
 * @property parent
 * @property value
 */
abstract class Definition<E>(val pack: LangPack, val parent: Group?, val raw: E) {

    var value: E = raw
    var walked: Boolean = false

    /**
     * TODO: Document.
     */
    fun walk() {
        if (!walked && needsWalk()) {
            value = onWalk()
            walked = true
        }
    }

    /**
     * TODO: Document.
     */
    fun unWalk() {
        walked = false
        value = raw
    }

    /**
     * Walks the value for the definition. This allows for post-load transformations of the value.
     *
     * @return TODO: Document.
     */
    abstract fun onWalk(): E

    /**
     * TODO: Document.
     *
     * @return
     */
    abstract fun needsWalk(): Boolean

    /**
     * TODO: Document.
     *
     * @param string
     *
     * @return
     */
    protected fun walkString(string: String): String {
        if (pack.debug) {
            println("Walking ($string)..")
        }

        val formatter = pack.formatter
        var value = string

        val fields = formatter.getFields(value)
        val language = parent?.language ?: pack.defaultLang

        val fieldMap = HashMap<String, Definition<*>>()
        for (field in fields) {
            val fField = formatter.strip(field)
            val def = pack.resolve(language, fField) ?: continue
            if (!def.walked) def.walk()
            fieldMap[field] = def
        }

        for (field in fields) {
            if (pack.formatter.isResolve(field)) {
                val formattedField = formatter.format(field)
                val fieldDefinition = fieldMap[field]

                // If the field cannot resolve, set the placeholder.
                if (fieldDefinition == null) {
                    val placeholder = formatter.strip(field)
                    if (pack.debug) {
                        println("Failed to locate resolve field: $field. Using placeholder instead. ($placeholder)")
                    }
                    value = value.replace(formattedField, placeholder)
                    continue
                }

                var resolvedField = pack.getString(formatter.strip(field), language)
                if (resolvedField == null) {
                    resolvedField = formatter.strip(field)
                }
                if (pack.debug) {
                    println("Replacing resolve field $field with: $resolvedField")
                }
                value = value.replace(formattedField, resolvedField)
            }
        }

        if (pack.debug) {
            println("value: $value")
        }

        return value
    }

    /**
     * TODO: Document.
     *
     * @param string
     *
     * @return
     */
    protected fun stringNeedsWalk(string: String): Boolean {
        val formatter = pack.formatter
        val fields = formatter.getFields(string)
        for (field in fields) {
            if (formatter.isResolve(field)) return true
        }
        return false
    }
}
