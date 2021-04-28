package com.asledgehammer.langpack.bukkit.processor

import com.asledgehammer.langpack.core.objects.formatter.FieldFormatter
import com.asledgehammer.langpack.core.processor.DefaultProcessor
import org.bukkit.ChatColor

/**
 * TODO: Document.
 */
class BukkitProcessor(formatter: FieldFormatter) : DefaultProcessor(formatter) {
    override fun postProcess(string: String): String = ChatColor.translateAlternateColorCodes('&', string)
}
