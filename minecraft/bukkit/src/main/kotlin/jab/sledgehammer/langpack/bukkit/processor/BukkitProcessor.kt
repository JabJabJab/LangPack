package jab.sledgehammer.langpack.bukkit.processor

import jab.sledgehammer.langpack.core.objects.formatter.FieldFormatter
import jab.sledgehammer.langpack.core.processor.DefaultProcessor
import org.bukkit.ChatColor

/**
 * TODO: Document.
 */
class BukkitProcessor(formatter: FieldFormatter) : DefaultProcessor(formatter) {
    override fun postProcess(string: String): String = ChatColor.translateAlternateColorCodes('&', string)
}
