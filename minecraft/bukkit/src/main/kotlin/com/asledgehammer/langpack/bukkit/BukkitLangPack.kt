@file:Suppress("unused")

package com.asledgehammer.langpack.bukkit

import com.asledgehammer.langpack.bukkit.objects.complex.BukkitActionText
import com.asledgehammer.langpack.bukkit.objects.complex.BukkitStringPool
import com.asledgehammer.langpack.bukkit.processor.BukkitProcessor
import com.asledgehammer.langpack.core.LangPack
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.Languages
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.complex.Complex
import com.asledgehammer.langpack.core.processor.LangProcessor
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File

/**
 * **BukkitLangPack** wraps the [LangPack] class to provide additional support for the Bukkit API.
 *
 * @author Jab
 *
 * @param classLoader The classloader to load resources.
 * @param dir The directory to handle lang files.
 */
class BukkitLangPack(classLoader: ClassLoader = this::class.java.classLoader, dir: File = File("lang")) :
    LangPack(classLoader, dir) {

    override var processor: LangProcessor = BukkitProcessor(formatter)

    /**
     * Basic constructor. Uses the 'lang' directory in the server folder.
     *
     * @param classLoader The classloader to load resources.
     */
    constructor(classLoader: ClassLoader) : this(classLoader, File("lang"))

    /**
     * Broadcasts a message to all online players, checking their locales and sending the corresponding dialog.
     *
     * @param query The ID of the dialog to send.
     * @param args The variables to apply to the dialog sent.
     */
    fun broadcast(query: String, vararg args: LangArg) {

        val cache = HashMap<Language, String>()

        for (player in Bukkit.getOnlinePlayers()) {

            // Grab the players language, else fallback to default.
            val langPlayer = getLanguage(player)
            var lang = langPlayer

            val cacheValue = cache[lang]
            if (cacheValue != null) {
                player.sendMessage(cacheValue)
                continue
            }

            var resolved = resolve(query, lang)
            if (resolved == null) {
                lang = defaultLang
                resolved = resolve(query, lang)
            }

            val component = resolved?.value?.toString() ?: query

            val result = processor.process(component, this, langPlayer, null, *args)
            cache[lang] = result
            cache[langPlayer] = result

            player.sendMessage(result)
        }
    }

    /**
     * Messages a player with a given field and arguments. The language will be based on [Player.getLocale].
     *   If the language is not supported, [LangPack.defaultLang] will be used.
     *
     * @param player The player to send the message.
     * @param query The field to send.
     * @param args Additional arguments to apply.
     */
    fun message(player: Player, query: String, vararg args: LangArg) {

        val langPlayer = getLanguage(player)
        var lang = langPlayer

        var resolved = resolve(query, lang)
        if (resolved == null) {
            lang = defaultLang
            resolved = resolve(query, lang)
        }

        val component: String = if (resolved != null) {
            when (val value = resolved.value) {
                is Complex<*> -> {
                    value.get().toString()
                }
                else -> {
                    value.toString()
                }
            }
        } else {
            query
        }

        player.sendMessage(
            processor.process(component, this, langPlayer, null, *args)
        )
    }

    /**
     * @param player The player to read.
     *
     * @return Returns the language of the player's [Player.getLocale]. If the locale set is invalid, the fallBack
     *   is returned.
     */
    fun getLanguage(player: Player): Language = Languages.getClosest(player.locale, defaultLang)

    init {
        setBukkitLoaders(loaders)
    }

    companion object {

        private val actionTextLoader = BukkitActionText.Loader()
        private val stringPoolLoader = BukkitStringPool.Loader()

        /**
         * Adds the default loaders for the spigot module.
         */
        fun setBukkitLoaders(map: HashMap<String, Complex.Loader<*>>) {
            map["action"] = actionTextLoader
            map["pool"] = stringPoolLoader
        }

        /**
         * Message a player with multiple lines of text.
         *
         * @param sender The player to send the texts.
         * @param lines The lines of text to send.
         */
        fun message(sender: CommandSender, lines: Array<String>) {
            if (lines.isEmpty()) return
            sender.sendMessage(lines)
        }

        /**
         * Message a player with multiple lines of text.
         *
         * @param sender The player to send the texts.
         * @param lines The lines of text to send.
         */
        fun message(sender: CommandSender, lines: List<String>) {
            // Convert to an array to send all messages at once.
            var array = emptyArray<String>()
            for (line in lines) {
                array = array.plus(line)
            }
            sender.sendMessage(array)
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcast(lines: Array<String>) {
            for (line in lines) {
                Bukkit.broadcastMessage(line)
            }
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcast(lines: List<String>) {
            for (line in lines) {
                Bukkit.broadcastMessage(line)
            }
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * <br/><b>NOTE:</b> If any lines of text are null, it is ignored.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcastSafe(lines: Array<String?>) {
            for (line in lines) {
                if (line != null) Bukkit.broadcastMessage(line)
            }
        }

        /**
         * Broadcasts multiple lines of text to all players on the server.
         *
         * <br/><b>NOTE:</b> If any lines of text are null, it is ignored.
         *
         * @param lines The lines of text to broadcast.
         */
        fun broadcastSafe(lines: List<String?>) {
            for (line in lines) {
                if (line != null) Bukkit.broadcastMessage(line)
            }
        }
    }
}
