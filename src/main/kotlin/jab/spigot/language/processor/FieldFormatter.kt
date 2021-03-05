package jab.spigot.language.processor

/**
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
     *
     * @return Returns a field in the syntax format.
     */
    fun formatField(field: String): String

    /**
     * @param string The string to test.
     *
     * @return Returns true if the string is a field.
     */
    fun isField(string: String?): Boolean
}
