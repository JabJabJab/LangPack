package jab.langpack.core.processor

import jab.langpack.core.util.StringUtil

/**
 * TODO: Update documentation to reflect Definition API update.
 *
 * The ***FieldFormatter*** interface implements methods for the following:
 *  <br/>
 * - **Field detection**
 * - **Field parsing**
 * - **Field formatting**
 *
 * @author Jab
 */
interface FieldFormatter {

    /**
     * Parses a string into fields.
     *
     * @param string The unprocessed string to parse.
     *
     * @return Returns the fields in the unprocessed string.
     */
    fun getFields(string: String): ArrayList<String>

    /**
     * @param field the Field to process.
     * @param properties (Optional) TODO: Document.
     *
     * @return Returns a field in the syntax format.
     */
    fun format(field: String, properties: FieldProperties? = null): String

    /**
     * TODO: Document.
     *
     * @param string
     *
     * @return
     */
    fun strip(string: String): String

    /**
     * @param string The string to test.
     *
     * @return Returns true if the string is a field.
     */
    fun isField(string: String?): Boolean

    /**
     * TODO: Document.
     *
     * @param string
     *
     * @return
     */
    fun getFallback(string: String): String?

    /**
     * TODO: Document.
     *
     * @param string
     *
     * @return
     */
    fun isResolve(string: String): Boolean

    /**
     * TODO: Document.
     *
     * @param string
     *
     * @return
     */
    fun isPackageScope(string: String): Boolean

    /**
     * TODO: Document.
     *
     * @param list
     *
     * @return
     */
    fun needsWalk(list: List<*>): Boolean {
        for (string in list) {
            if (string != null && needsWalk(string)) return true
        }
        return false
    }

    /**
     * TODO: Document.
     *
     * @param value
     *
     * @return
     */
    fun needsWalk(value: Any): Boolean {
        val valueActual = StringUtil.toAString(value)
        val fields = getFields(valueActual)
        for (field in fields) {
            if (isResolve(field)) return true
        }
        return false
    }

    /**
     * TODO: Document.
     *
     * @param field
     *
     * @return
     */
    fun getProperties(field: String): FieldProperties =
        FieldProperties(strip(field), getFallback(field), isResolve(field), isPackageScope(field))
}
