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

    override fun isResolve(field: String): Boolean = field.indexOf('!') > -1

    override fun isPackageScope(field: String): Boolean = field.indexOf('~') > -1

    override fun format(field: String): String = "%${field.toLowerCase()}%"

    override fun strip(string: String): String =
        string.replace("%", "")
            .replace("!", "")
            .replace("~", "")

}
