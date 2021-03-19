@file:Suppress("unused")

package jab.langpack.bungeecord

import jab.langpack.bungeecord.objects.complex.BungeeActionText
import jab.langpack.bungeecord.objects.complex.BungeeStringPool
import jab.langpack.core.LangPack
import jab.langpack.core.Language
import jab.langpack.core.objects.LangArg
import jab.langpack.core.objects.complex.Complex
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.io.File
import java.util.*

class BungeeLangPack(classLoader: ClassLoader = this::class.java.classLoader, dir: File = File("lang")) :
    LangPack(classLoader, dir) {

    /**
     * @see LangPack
     */
    constructor(classLoader: ClassLoader) : this(classLoader, File("lang"))

    init {
        setBungeeLoaders(loaders)
    }

    /**
     * Broadcasts a message to all online players, checking their locales and sending the corresponding dialog.
     *
     *
     * @param query The ID of the dialog to send.
     * @param args The variables to apply to the dialog sent.
     */
    fun broadcast(query: String, vararg args: LangArg) {

        val cache: EnumMap<Language, TextComponent> = EnumMap<Language, TextComponent>(Language::class.java)

        val server = ProxyServer.getInstance()

        for (player in server.players) {

            // Grab the players language, else fallback to default.
            val langPlayer = getLanguage(player)
            var lang = langPlayer

            if (cache[lang] != null) {
                player.sendMessage(cache[lang])
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

            player.sendMessage(result)
        }
    }

    /**
     * Messages a player with a given field and arguments. The language will be based on [ProxiedPlayer.getLocale].
     *   If the language is not supported, [LangPack.defaultLang] will be used.
     *
     * @param player The player to send the message.
     * @param query The field to send.
     * @param args Additional arguments to apply.
     */
    fun message(player: ProxiedPlayer, query: String, vararg args: LangArg) {

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

        player.sendMessage(
            processor.process(component, this, langPlayer, null, *args)
        )
    }

    /**
     * @param player The player to read.
     *
     * @return Returns the language of the player's [ProxiedPlayer.getLocale]. If the locale set is invalid, the fallBack
     *   is returned.
     */
    fun getLanguage(player: ProxiedPlayer): Language {

        if (player.locale != null) {
            var locale = player.locale.language
            if (player.locale.country != null) {
                locale += "_${player.locale.country}"
            }
            locale = locale.toLowerCase()

            for (lang in Language.values()) {
                if (lang.abbreviation.equals(locale, true)) {
                    return lang
                }
            }
        }

        return defaultLang
    }

    companion object {

        private val actionTextLoader = BungeeActionText.Loader()
        private val stringPoolLoader = BungeeStringPool.Loader()

        /**
         * Adds the default loaders for the spigot module.
         */
        fun setBungeeLoaders(map: HashMap<String, Complex.Loader<*>>) {
            map["action"] = actionTextLoader
            map["pool"] = stringPoolLoader
        }
    }
}