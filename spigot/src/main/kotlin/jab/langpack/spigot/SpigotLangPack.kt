package jab.langpack.spigot

import jab.langpack.commons.LangArg
import jab.langpack.commons.LangPack
import jab.langpack.commons.Language
import jab.langpack.commons.objects.Complex
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import java.util.*

/**
 * The **SpigotLangPack** class wraps [LangPack] to add methods specific for the Spigot environment.
 *
 * @see LangPack
 *
 * @author Jab
 *
 * @property name The String name of the pack.
 * @property dir (Optional) The File Object for the directory where the LangFiles are stored. DEFAULT: 'lang/'
 * @throws IllegalArgumentException Thrown if the directory doesn't exist or isn't a valid directory. Thrown if
 *      the name given is empty.
 */
@Suppress("unused")
class SpigotLangPack(name: String, dir: File = File("lang")) : LangPack(name, dir) {

    /**
     * Broadcasts a message to all online players, checking their locales and sending the corresponding dialog.
     *
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

            var value = resolve(lang, query)
            if (value == null) {
                lang = defaultLang
                value = resolve(lang, query)
            }

            if (debug) {
                println("query: $query value: $value")
            }

            val component: TextComponent
            if (value != null) {
                component = when (value) {

                    is TextComponent -> {
                        value
                    }
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

            val result = processor.processComponent(component, this, langPlayer, *args)
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

        var value = resolve(lang, query)
        if (value == null) {
            lang = defaultLang
            value = resolve(lang, query)
        }

        val component: TextComponent
        if (value != null) {
            component = when (value) {
                is TextComponent -> {
                    value
                }
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
            processor.processComponent(component, this, langPlayer, *args)
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
            if (lang.abbreviation.equals(locale, true)) {
                return lang
            }
        }
        return defaultLang
    }

    companion object {

        /**
         * Message a player with multiple lines of text.
         *
         * @param sender The player to send the texts.
         * @param lines The lines of text to send.
         */
        fun message(sender: CommandSender, lines: Array<String>) {

            if (lines.isEmpty()) {
                return
            }

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
                if (line != null) {
                    Bukkit.broadcastMessage(line)
                }
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
                if (line != null) {
                    Bukkit.broadcastMessage(line)
                }
            }
        }
    }
}