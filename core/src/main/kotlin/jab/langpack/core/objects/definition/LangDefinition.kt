@file:Suppress("MemberVisibilityCanBePrivate")

package jab.langpack.core.objects.definition

import jab.langpack.core.LangPack
import jab.langpack.core.objects.LangGroup
import jab.langpack.core.objects.formatter.FieldFormatter
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

        var value = string
        val fields = pack.formatter.getFields(value)
        val language = parent?.language ?: pack.defaultLang

        val walkedDefinitions = ArrayList<String>()

        for (field in fields) {

            val context = if (field.packageScope) {
                null
            } else {
                parent
            }

            if (!walkedDefinitions.contains(field.raw)) {
                val def = pack.resolve(field.name, language, context)
                if (def != null && !def.walked) def.walk()
                walkedDefinitions.add(field.raw)
            }

            if (field.resolve) {
                // If the field cannot resolve, set the placeholder.
                if (!walkedDefinitions.contains(field.raw)) {
                    if (pack.debug) {
                        println(
                            """Failed to locate resolve field: "$field". Using placeholder instead: "$field.placeholder"."""
                        )
                    }
                    value = value.replace(field.raw, field.placeholder)
                    continue
                }

                val resolved = pack.resolve(field.name, language, context)
                val result = if (resolved != null) {
                    StringUtil.toAString(resolved.value!!)
                } else {
                    field.placeholder
                }

                if (pack.debug) {
                    println("""Replacing resolve field "$field" with: "$result".""")
                }
                value = value.replace(field.raw, result)
            }
        }

        if (pack.debug) {
            println("value: $value")
        }

        return value
    }
}
