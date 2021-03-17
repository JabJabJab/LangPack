package jab.langpack.core.processor

/**
 * TODO: Update documentation to reflect Definition API update.
 *
 * The **PercentFormatter** class. TODO: Document.
 *
 * @author Jab
 */
class PercentFormatter : FieldFormatter {

    override fun getFields(string: String): ArrayList<String> {
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
            } else {
                if (insideField) {
                    nextField.append(c)
                }
            }
        }
        return fields
    }

    override fun isField(string: String?): Boolean {
        if (string != null && string.length > 2 && string.startsWith('%') && string.endsWith('%')) {
            // Check to make sure the string doesn't start with one variable and end with another.
            return getFields(string).size == 1
        }
        return false
    }

    override fun getFallback(string: String): String? {
        return if (string.contains("=")) {
            string.substring(string.indexOf('=') + 1).replace("%", "")
        } else {
            null
        }
    }

    override fun isResolve(string: String): Boolean = string.indexOf('!') > -1

    override fun isPackageScope(string: String): Boolean = string.indexOf('~') > -1

    override fun format(field: String, properties: FieldProperties?): String {

        var built = field.toLowerCase()

        if (properties != null) {
            if (properties.fallBack != null && !properties.fallBack.equals(built, true)) {
                built += "=${properties.fallBack}"
            }
            if (properties.resolve) {
                built = "!$built"
            }
            if (properties.packageScope) {
                built = "~$built"
            }
        }

        return "%$built%"
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

        return stripped
    }
}
