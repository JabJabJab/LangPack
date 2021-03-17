@file:Suppress("MemberVisibilityCanBePrivate")

package jab.langpack.core.objects.definition

import jab.langpack.core.LangPack
import jab.langpack.core.objects.LangGroup
import jab.langpack.core.processor.FieldFormatter
import jab.langpack.core.util.StringUtil

/**
 * The **Definition** class. TODO: Document.
 *
 * @property pack
 * @property parent
 * @property value
 */
abstract class LangDefinition<E>(val pack: LangPack, val parent: LangGroup?, val raw: E) {

    var value: E = raw
    var walked: Boolean = false

    /**
     * TODO: Document.
     */
    fun walk() {
        if (!walked && needsWalk(pack.formatter)) {
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
    abstract fun needsWalk(formatter: FieldFormatter): Boolean

    /**
     * TODO: Document.
     *
     * @param list
     *
     * @return
     */
    fun walk(list: List<String>): ArrayList<String> {
        val walkedList = ArrayList<String>()
        for (string in list) walkedList.add(walk(string))
        return walkedList
    }

    /**
     * TODO: Document.
     *
     * @param string
     *
     * @return
     */
    fun walk(string: String): String {
        if (pack.debug) {
            println("Walking ($string)..")
        }

        val formatter = pack.formatter
        var value = string

        val fields = formatter.getFields(value)
        val language = parent?.language ?: pack.defaultLang

        val fieldMap = HashMap<String, LangDefinition<*>>()
        for (field in fields) {
            val context = if (formatter.isPackageScope(field)) {
                null
            } else {
                parent
            }
            val fField = formatter.strip(field)
            val def = pack.resolve(fField, language, context) ?: continue
            if (!def.walked) def.walk()
            fieldMap[field] = def
        }

        for (field in fields) {
            if (pack.formatter.isResolve(field)) {
                val strippedField = formatter.strip(field)
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

                val context = if (formatter.isPackageScope(field)) {
                    null
                } else {
                    parent
                }

                val resolved = pack.resolve(strippedField, language, context)
                val resolvedField = if (resolved != null) {
                    StringUtil.toAString(resolved.value!!)
                } else {
                    strippedField
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
}
