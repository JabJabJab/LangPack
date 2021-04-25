@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package jab.sledgehammer.langpack.core.util

/**
 * TODO: Document.
 *
 * @author Jab
 */
class PrettyPrintingMap<K, V>(private val map: Map<K, V>) {

    var text = ""
    var prefix = ""

    fun reset() {
        text = ""
        prefix = ""
    }

    fun raw(string: String) {
        text += string
    }

    fun line(string: String) {
        text += "$prefix$string\n"
    }


    fun recurse(key: String?, value: Any?) {

        fun tab() {
            prefix += "  "
        }

        fun untab() {
            val index = prefix.lastIndex - 1
            prefix = if (index < 1) {
                ""
            } else {
                prefix.substring(0, index)
            }
        }

        if (value is Map<*, *>) {
            if (key != null) line("$key: {")
            else line("{")
            tab()
            recurseMap(value)
            untab()
            line("},")
        } else if (value is List<*>) {
            if (key != null) line("$key: [")
            else line("[")
            tab()
            recurseList(value)
            untab()
            line("],")
        } else {
            if (key != null) line("$key: $value")
            else line("$value,")
        }
    }

    fun recurseList(list: List<*>) {
        for ((index, item) in list.withIndex()) {
            recurse("$index", item)
        }
    }

    fun recurseMap(map: Map<*, *>) {
        for ((key, value) in map) {
            if (value != null) recurse(key.toString(), value)
        }
    }

    override fun toString(): String {
        reset()
        recurse(null, map)
        return text
    }
}
