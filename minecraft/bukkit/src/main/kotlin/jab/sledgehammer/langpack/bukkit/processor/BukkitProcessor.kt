package jab.sledgehammer.langpack.bukkit.processor

import jab.sledgehammer.langpack.core.objects.formatter.FieldFormatter
import jab.sledgehammer.langpack.core.processor.DefaultProcessor
import net.md_5.bungee.api.ChatColor

class BukkitProcessor(formatter: FieldFormatter) : DefaultProcessor(formatter) {

    /**
     * TODO: Implement.
     *
     * @param string The string to color.
     * @param colorCode The alternative color-code to process.
     *
     * @return Returns the colored string.
     */
    override fun color(string: String, colorCode: Char): String {
        return ChatColor.translateAlternateColorCodes(colorCode, string)
    }
}