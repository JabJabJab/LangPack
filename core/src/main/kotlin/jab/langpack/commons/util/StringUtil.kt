package jab.langpack.commons.util

import jab.langpack.commons.LangPack.Companion.NEW_LINE
import net.md_5.bungee.api.ChatColor
import java.util.*

/**
 * The **StringUtil** class contains static utility methods for management of objects and strings.
 *
 * @author Jab
 */
@Suppress("unused")
class StringUtil {
    companion object {

        /**
         * Converts any object given to a string. Lists are compacted into one String using [NEW_LINE] as a separator.
         *
         * @param value The value to process to a String.
         *
         * @return Returns the result String.
         */
        fun toAString(value: Any): String {

            return if (value is List<*>) {

                val builder: StringBuilder = StringBuilder()
                for (next in value) {
                    val line = value.toString()
                    if (builder.isEmpty()) {
                        builder.append(line)
                    } else {
                        builder.append(NEW_LINE).append(line)
                    }
                }

                builder.toString()
            } else {
                value.toString()
            }
        }

        /**
         * @param value The value to partition as a string with the [NEW_LINE] operator.
         *
         * @return Returns a List of Strings, partitioned by the [NEW_LINE] operator.
         */
        fun toAList(value: Any): List<String?> {

            val string = value.toString()

            return if (string.contains(NEW_LINE)) {
                string.split(NEW_LINE)
            } else {
                listOf(string)
            }
        }

        /**
         * Converts a List of Strings to a String Array.
         *
         * @param list The List to convert.
         *
         * @return Returns a String Array of the String Lines in the List provided.
         */
        fun toAStringArray(list: List<String>): Array<String> {

            var array: Array<String> = emptyArray()
            for (next in list) {
                array = array.plus(next)
            }

            return array
        }

        /**
         * Colors a list of strings to the Minecraft color-code specifications using an alternative color-code.
         *
         * @param strings The strings to color.
         * @param colorCode (Default: '&') The alternative color-code to process.
         *
         * @return Returns the colored string.
         */
        fun color(strings: List<String>, colorCode: Char = '&'): List<String> {
            val coloredList = ArrayList<String>()
            for (string in strings) {
                coloredList.add(color(string, colorCode))
            }
            return coloredList
        }

        /**
         * Colors a string to the Minecraft color-code specifications using an alternative color-code.
         *
         * @param string The string to color.
         * @param colorCode (Default: '&') The alternative color-code to process.
         *
         * @return Returns the colored string.
         */
        fun color(string: String, colorCode: Char = '&'): String =
            ChatColor.translateAlternateColorCodes(colorCode, string)
    }
}
