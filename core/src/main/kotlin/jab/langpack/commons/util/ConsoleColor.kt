package jab.langpack.commons.util

import net.md_5.bungee.api.ChatColor

/**
 * The **ConsoleColor** class contains static utility methods for processing Minecraft color-codes to ANSI
 *   color-codes when printing to Java's consoles.
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class ConsoleColor {
    companion object {

        const val ANSI_RESET = "\u001B[0m"
        const val ANSI_BLACK = "\u001B[30m"
        const val ANSI_RED = "\u001B[31m"
        const val ANSI_GREEN = "\u001B[32m"
        const val ANSI_YELLOW = "\u001B[33m"
        const val ANSI_BLUE = "\u001B[34m"
        const val ANSI_PURPLE = "\u001B[35m"
        const val ANSI_CYAN = "\u001B[36m"
        const val ANSI_WHITE = "\u001B[37m"

        const val ANSI_BLACK_BACKGROUND = "\u001B[40m"
        const val ANSI_RED_BACKGROUND = "\u001B[41m"
        const val ANSI_GREEN_BACKGROUND = "\u001B[42m"
        const val ANSI_YELLOW_BACKGROUND = "\u001B[43m"
        const val ANSI_BLUE_BACKGROUND = "\u001B[44m"
        const val ANSI_PURPLE_BACKGROUND = "\u001B[45m"
        const val ANSI_CYAN_BACKGROUND = "\u001B[46m"
        const val ANSI_WHITE_BACKGROUND = "\u001B[47m"

        /**
         * Formats a line of text with Minecraft color-codes to ANSI color codes.
         *
         * @param string The string to format.
         *
         * @return Returns the formatted string.
         */
        fun toANSI(string: String): String {
            return string
                .replace(ChatColor.RESET.toString(), ANSI_RESET)
                // Red
                .replace(ChatColor.DARK_RED.toString(), ANSI_RED)
                .replace(ChatColor.RED.toString(), ANSI_RED)
                // Green
                .replace(ChatColor.DARK_GREEN.toString(), ANSI_GREEN)
                .replace(ChatColor.GREEN.toString(), ANSI_GREEN)
                // Yellow
                .replace(ChatColor.YELLOW.toString(), ANSI_YELLOW)
                .replace(ChatColor.GOLD.toString(), ANSI_YELLOW)
                // Blue
                .replace(ChatColor.DARK_BLUE.toString(), ANSI_BLUE)
                .replace(ChatColor.BLUE.toString(), ANSI_BLUE)
                // Purple
                .replace(ChatColor.DARK_PURPLE.toString(), ANSI_PURPLE)
                .replace(ChatColor.LIGHT_PURPLE.toString(), ANSI_PURPLE)
                // Cyan
                .replace(ChatColor.DARK_AQUA.toString(), ANSI_CYAN)
                .replace(ChatColor.AQUA.toString(), ANSI_CYAN)
                // White
                .replace(ChatColor.GRAY.toString(), ANSI_WHITE)
                .replace(ChatColor.DARK_GRAY.toString(), ANSI_WHITE)
                .replace(ChatColor.WHITE.toString(), ANSI_WHITE)
                // Black
                .replace(ChatColor.BLACK.toString(), ANSI_BLACK)
        }

        /**
         * Prints lines to Java's consoles, converting Minecraft color-codes to ANSI color-codes on each line.
         *
         * @param lines The lines of text to print.
         */
        fun println(lines: List<String>) {
            for (line in lines) {
                kotlin.io.println(toANSI(line))
            }
        }

        /**
         * Prints lines to Java's consoles, converting Minecraft color-codes to ANSI color-codes on each line.
         *
         * @param lines The lines of text to print.
         */
        fun println(vararg lines: String) {
            for (line in lines) {
                kotlin.io.println(toANSI(line))
            }
        }
    }
}
