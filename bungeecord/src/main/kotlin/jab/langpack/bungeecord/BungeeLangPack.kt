package jab.langpack.bungeecord

import jab.langpack.commons.LangArg
import jab.langpack.commons.LangPack
import jab.langpack.commons.Language
import jab.langpack.commons.objects.LangComplex
import jab.langpack.commons.objects.LangComponent
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.io.File
import java.util.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
class BungeeLangPack(name: String, dir: File = File("lang")) : LangPack(name, dir) {

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

            var value = resolve(query, lang)
            if (value == null) {
                lang = defaultLang
                value = resolve(query, lang)
            }

            println("query: $query value: $value")

            val component: TextComponent
            if (value != null) {
                component = when (value) {
                    is LangComponent -> {
                        value.get()
                    }
                    is LangComplex -> {
                        TextComponent(value.get())
                    }
                    is TextComponent -> {
                        value
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

        var value = resolve(query, lang)
        if (value == null) {
            lang = defaultLang
            value = resolve(query, lang)
        }

        val component: TextComponent
        if (value != null) {
            component = when (value) {
                is LangComponent -> {
                    value.get()
                }
                is LangComplex -> {
                    TextComponent(value.get())
                }
                is TextComponent -> {
                    value
                }
                else -> {
                    TextComponent(value.toString())
                }
            }
        } else {
            component = TextComponent(query)
        }

        player.sendMessage(
            processor.processComponent(component, this, langPlayer, *args)
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

            // println("${player.name} locale: $locale")

            for (lang in Language.values()) {
                if (lang.abbreviation.equals(locale, true)) {
                    return lang
                }
            }
        }

        return defaultLang
    }

}