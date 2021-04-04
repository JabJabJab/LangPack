@file:Suppress("unused")

package jab.sledgehammer.langpack.bungeecord

import jab.sledgehammer.langpack.bungeecord.objects.complex.BungeeActionText
import jab.sledgehammer.langpack.bungeecord.objects.complex.BungeeStringPool
import jab.sledgehammer.langpack.core.LangPack
import jab.sledgehammer.langpack.core.Language
import jab.sledgehammer.langpack.core.objects.LangArg
import jab.sledgehammer.langpack.core.objects.complex.Complex
import jab.sledgehammer.langpack.textcomponent.TextComponentLangPack
import jab.sledgehammer.langpack.textcomponent.processor.TextComponentProcessor
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.io.File
import java.util.*

/**
 * **BungeeLangPack** wraps the LangPack class to provide additional support for the BungeeCord API.
 *
 * @author Jab
 *
 * @param classLoader The classloader to load resources.
 * @param dir The directory to handle lang files.
 */
class BungeeLangPack(classLoader: ClassLoader = this::class.java.classLoader, dir: File = File("lang")) :
    TextComponentLangPack(classLoader, dir) {

    /**
     * Basic constructor. Uses the 'lang' directory in the server folder.
     *
     * @param classLoader The classloader to load resources.
     */
    constructor(classLoader: ClassLoader) : this(classLoader, File("lang"))

    init {
        setBungeeLoaders(loaders)
    }

    /**
     * Broadcasts a message to all online players, checking their locales and sending the corresponding dialog.
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

            val result = (processor as TextComponentProcessor).process(component, this, langPlayer, null, *args)
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
            (processor as TextComponentProcessor).process(component, this, langPlayer, null, *args)
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
         * Adds the default loaders for the bungeecord module.
         *
         * @param map The map to store the loaders.
         */
        fun setBungeeLoaders(map: HashMap<String, Complex.Loader<*>>) {
            map["action"] = actionTextLoader
            map["pool"] = stringPoolLoader
        }
    }
}
