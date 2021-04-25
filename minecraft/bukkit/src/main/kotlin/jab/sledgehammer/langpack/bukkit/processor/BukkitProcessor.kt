package jab.sledgehammer.langpack.bukkit.processor

import jab.sledgehammer.langpack.core.objects.formatter.FieldFormatter
import jab.sledgehammer.langpack.core.processor.DefaultProcessor
import org.bukkit.ChatColor

class BukkitProcessor(formatter: FieldFormatter) : DefaultProcessor(formatter) {

    /**
     * TODO: Implement.
     *
     * @param string The string to color.
     * @param colorCode The alternative color-code to process.
     *
     * @return Returns the colored string.
     */
    override fun color(string: String, colorCode: Char): String =
        ChatColor.translateAlternateColorCodes(colorCode, string)
}
