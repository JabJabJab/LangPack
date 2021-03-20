@file:Suppress("unused")

package jab.langpack.spigot

import jab.langpack.core.LangPack
import jab.langpack.core.Language
import jab.langpack.core.objects.LangArg
import jab.langpack.core.objects.complex.Complex
import jab.langpack.spigot.objects.complex.SpigotActionText
import jab.langpack.spigot.objects.complex.SpigotStringPool
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import java.util.*

/**
 * **SpigotLangPack** wraps the LangPack class to provide additional support for the Spigot API.
 *
 * @author Jab
 *
 * @param classLoader The classloader to load resources.
 * @param dir The directory to handle lang files.
 */
class SpigotLangPack(classLoader: ClassLoader = this::class.java.classLoader, dir: File = File("lang")) :
    LangPack(classLoader, dir) {

    /**
     * Basic constructor. Uses the 'lang' directory in the server folder.
     *
     * @param classLoader The classloader to load resources.
     */
    constructor(classLoader: ClassLoader) : this(classLoader, File("lang"))

    init {
        setSpigotLoaders(loaders)
    }

    /**
     * Broadcasts a message to all online players, checking their locales and sending the corresponding dialog.
     *
     * @param query The ID of the dialog to send.
     * @param args The variables to apply to the dialog sent.
     */
    fun broadcast(query: String, vararg args: LangArg) {

        val cache: EnumMap<Language, TextComponent> = EnumMap<Language, TextComponent>(Language::class.java)

        for (player in Bukkit.getOnlinePlayers()) {

            // Grab the players language, else fallback to default.
            val langPlayer = getLanguage(player)
            var lang = langPlayer

            if (cache[lang] != null) {
                player.spigot().sendMessage(cache[lang])
                continue
            }

            var resolved = resolve(query, lang)
            if (resolved == null) {
                lang = defaultLang
                resolved = resolve(query, lang)
            }

            val component: TextComponent
            if (resolved != null) {

                val value = resolved.value

                component = when (value) {

                    is Complex<*> -> {
                        val result = value.get()
                        val processedComponent: TextComponent = if (result is TextComponent) {
                            result
                        } else {
                            TextComponent(result.toString())
                        }
                        processedComponent
                    }
                    else -> {
                        TextComponent(value.toString())
                    }
                }
            } else {
                component = TextComponent(query)
            }

            val result = processor.process(component, this, langPlayer, null, *args)
            cache[lang] = result
            cache[langPlayer] = result

            player.spigot().sendMessage(result)
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

        val component: TextComponent
        if (resolved != null) {
            val value = resolved.value
            component = when (value) {
                is Complex<*> -> {
                    val result = value.get()
                    val processedComponent: TextComponent = if (result is TextComponent) {
                        result
                    } else {
                        TextComponent(result.toString())
                    }
                    processedComponent
                }
                else -> {
                    TextComponent(value.toString())
                }
            }
        } else {
            component = TextComponent(query)
        }

        player.spigot().sendMessage(
            processor.process(component, this, langPlayer, null, *args)
        )
    }

    /**
     * @param player The player to read.
     *
     * @return Returns the language of the player's [Player.getLocale]. If the locale set is invalid, the fallBack
     *   is returned.
     */
    fun getLanguage(player: Player): Language {
        val locale = player.locale
        for (lang in Language.values()) {
            if (lang.abbreviation.equals(locale, true)) return lang
        }
        return defaultLang
    }

    companion object {

        private val actionTextLoader = SpigotActionText.Loader()
        private val stringPoolLoader = SpigotStringPool.Loader()

        /**
         * Adds the default loaders for the spigot module.
         */
        fun setSpigotLoaders(map: HashMap<String, Complex.Loader<*>>) {
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
