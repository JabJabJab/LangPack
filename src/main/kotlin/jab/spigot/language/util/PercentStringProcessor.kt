package jab.spigot.language.util

import jab.spigot.language.LangArg
import jab.spigot.language.LangPackage
import jab.spigot.language.LangPackage.Companion.color
import jab.spigot.language.Language

/**
 * The <i>PercentStringProcessor</i> class implements the default field syntax for [LangPackage].
 *
 *  Field syntax: '%field%'
 *
 *  @author Jab
 */
class PercentStringProcessor : StringProcessor {

    override fun processString(string: String, pkg: LangPackage, lang: Language, vararg args: LangArg): String {

        val stringFields = getFields(string)
        if (stringFields.isEmpty()) {
            return color(string)
        }

        var processedString = string

        // Process all fields in the string.
        for (stringField in stringFields) {

            val fField = formatField(stringField)
            var found = false

            // Check the passed fields for the defined field.
            for (field in args) {
                if (field.key.equals(stringField, true)) {
                    found = true
                    val value = field.value.toString()
                    processedString = processedString.replace(fField, value, true)
                }
                break
            }

            // Check LanguagePackage for the defined field.
            if (!found) {
                val field = pkg.getString(stringField)
                if (field != null) {
                    processedString = processedString.replace(fField, field, true)
                }
            }
        }

        // Remove all field characters.
        for (stringField in stringFields) {
            processedString = processedString.replace(stringField, stringField.replace("%", ""))
        }

        return color(processedString)
    }

    override fun processString(string: String, vararg args: LangArg): String {

        val stringFields = getFields(string)
        if (stringFields.isEmpty()) {
            return color(string)
        }

        var processedString = string

        // Process all fields in the string.
        for (stringField in stringFields) {
            for (field in args) {
                if (field.key.equals(stringField, true)) {
                    val fField = formatField(stringField)
                    val value = field.value.toString()
                    processedString = processedString.replace(fField, value, true)
                }
                break
            }
        }

        // Remove all field characters.
        for (stringField in stringFields) {
            processedString = processedString.replace(stringField, stringField.replace("%", ""))
        }

        return color(processedString)
    }

    override fun getFields(string: String): Array<String> {

        val nextField = StringBuilder()
        var fields: Array<String> = emptyArray()
        var insideField = false

        for (next in string.chars()) {
            val c = next.toChar()
            if (c == '%') {
                if (insideField) {
                    if (nextField.isNotEmpty()) {
                        fields = fields.plus(nextField.toString())
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

    companion object {

        /**
         * @param field the Field to process.
         *
         * @return Returns a field in the syntax format.
         */
        private fun formatField(field: String): String {
            return "%${field.toLowerCase()}%"
        }
    }
}