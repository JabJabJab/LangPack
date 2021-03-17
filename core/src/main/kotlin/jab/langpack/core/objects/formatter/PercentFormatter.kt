package jab.langpack.core.objects.formatter

import jab.langpack.core.objects.FieldProperties

/**
 * TODO: Update documentation to reflect Definition API update.
 *
 * The **PercentFormatter** class. TODO: Document.
 *
 * @author Jab
 */
class PercentFormatter : FieldFormatter {

    override fun getFields(string: String): List<FieldProperties> {
        val nextField = StringBuilder()
        val fields = ArrayList<FieldProperties>()
        var insideField = false

        for (next in string.chars()) {
            val c = next.toChar()
            if (c == '%') {
                if (insideField) {
                    if (nextField.isNotEmpty()) {
                        fields.add(getProperties(nextField.toString()))
                    }
                    insideField = false
                } else {
                    insideField = true
                    nextField.clear()
                }
            } else if (insideField) nextField.append(c)
        }
        return fields
    }

    override fun getRawFields(string: String): List<String> {
        val nextField = StringBuilder()
        val fields = ArrayList<String>()
        var insideField = false

        for (next in string.chars()) {
            val c = next.toChar()
            if (c == '%') {
                if (insideField) {
                    if (nextField.isNotEmpty()) {
                        fields.add(nextField.toString())
                    }
                    insideField = false
                } else {
                    insideField = true
                    nextField.clear()
                }
            } else if (insideField) nextField.append(c)
        }
        return fields
    }

    override fun getProperties(field: String): FieldProperties {
        return FieldProperties(
            "%$field%",
            strip(field),
            getPlaceholder(field),
            isResolve(field),
            isPackageScope(field)
        )
    }

    override fun getFieldCount(string: String): Int {
        val nextField = StringBuilder("")
        var insideField = false
        var count = 0
        for (next in string.chars()) {
            val c = next.toChar()
            if (c == '%') {
                if (insideField) {
                    if (nextField.isNotEmpty()) {
                        count++
                    }
                    insideField = false
                } else {
                    insideField = true
                    nextField.clear()
                }
            } else {
                if (insideField) nextField.append(c)
            }
        }
        return count
    }

    override fun getPlaceholder(string: String): String {
        if (string.isEmpty()) return ""
        return if (string.contains("=")) {
            string.substring(string.indexOf('=') + 1).replace("%", "")
        } else {
            string.replace("!", "").replace("~", "")
        }
    }

    override fun strip(string: String): String {

        // Invalid placeholder. Don't cause a runtime exception for substring.
        if (string.startsWith("=")) return ""

        var stripped = string.replace("%", "")
            .replace("!", "")
            .replace("~", "")

        // Remove placeholder
        val index = string.indexOf("=")
        if (index > -1) {
            stripped = stripped.substring(0, stripped.indexOf("=") - 1)
        }

        return stripped.toLowerCase()
    }

    override fun format(field: String): String = "%${field.toLowerCase()}%"

    override fun isField(string: String?): Boolean {
        if (string == null || string.isEmpty()) return false
        if (string.length > 2 && string.startsWith('%') && string.endsWith('%')) {
            // Check to make sure the string doesn't start with one variable and end with another.
            return getFieldCount(string) == 1
        }
        return false
    }

    override fun isResolve(string: String): Boolean = string.indexOf('!') > -1

    override fun isPackageScope(string: String): Boolean = string.indexOf('~') > -1
}
