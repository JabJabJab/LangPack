@file:Suppress("unused")

package jab.langpack.bungeecord

import jab.langpack.core.LangArg
import jab.langpack.core.LangCache
import jab.langpack.core.LangPack
import jab.langpack.core.Language
import jab.langpack.core.objects.ActionText
import jab.langpack.core.objects.Complex
import jab.langpack.core.objects.StringPool
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

// #############################
// ########  LangPack   ########
// #############################

/**
 * Broadcasts a message to all online players, checking their locales and sending the corresponding dialog.
 *
 *
 * @param query The ID of the dialog to send.
 * @param args The variables to apply to the dialog sent.
 */
fun LangPack.broadcast(query: String, vararg args: LangArg) {

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
fun LangPack.message(player: ProxiedPlayer, query: String, vararg args: LangArg) {

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
fun LangPack.getLanguage(player: ProxiedPlayer): Language {

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

// #############################
// ########  LangCache  ########
// #############################

/**
 * @see BungeeLangPack.broadcast
 */
fun LangCache<*>.broadcast(field: String, vararg args: LangArg) = pack.broadcast(field, *args)

/**
 * @see BungeeLangPack.message
 */
fun LangCache<*>.message(player: ProxiedPlayer, field: String, vararg args: LangArg) =
    pack.message(player, field, *args)

/**
 * @see BungeeLangPack.getLanguage
 */
fun LangCache<*>.getLanguage(player: ProxiedPlayer): Language = pack.getLanguage(player)

// #############################
// ######## ACTION TEXT ########
// #############################

/**
 * Sends the ActionText to a given player.
 *
 * @param player The player to send.
 */
fun ActionText.message(player: ProxiedPlayer) {

    // Make sure that only online players are processed.
    if (!player.isConnected) {
        return
    }

    player.sendMessage(get())
}

/**
 * Sends the ActionText as a message to a player.
 *
 * @param player The player to receive the message.
 * @param pack (Optional) The package to process the text.
 * @param args (Optional) Additional arguments to provide to process the text.
 */
fun ActionText.send(player: ProxiedPlayer, pack: LangPack? = null, vararg args: LangArg) {

    val textComponent = if (pack != null) {
        process(pack, pack.getLanguage(player), *args)
    } else {
        get()
    }

    player.sendMessage(textComponent)
}

/**
 * Broadcasts the ActionText to all online players on the server.
 */
fun ActionText.broadcast() {
    val message = get()
    val server = ProxyServer.getInstance()
    for (player in server.players) {
        player.sendMessage(message)
    }
}

/**
 * Broadcasts the ActionText to all online players on the server.
 *
 * @param pack The package to process the text.
 * @param args (Optional) Additional arguments to provide to process the text.
 */
fun ActionText.broadcast(pack: LangPack, vararg args: LangArg) {

    val cache = EnumMap<Language, TextComponent>(Language::class.java)

    val server = ProxyServer.getInstance()
    for (player in server.players) {

        val textComponent: TextComponent
        val lang = pack.getLanguage(player)

        if (cache[lang] != null) {
            textComponent = cache[lang]!!
        } else {
            textComponent = process(pack, pack.getLanguage(player), *args)
            cache[lang] = textComponent
        }

        player.sendMessage(textComponent)
    }
}

// #############################
// ######## STRING POOL ########
// #############################

/**
 * Sends the StringPool to a given player.
 *
 * @param player The player to send.
 */
fun StringPool.message(player: ProxiedPlayer) {
    // Make sure that only online players are processed.
    if (!player.isConnected) {
        return
    }
    player.sendMessage(TextComponent(get()))
}

/**
 * Sends the StringPool as a message to a player.
 *
 * @param player The player to receive the message.
 * @param pack (Optional) The package to process the text.
 * @param args (Optional) Additional arguments to provide to process the text.
 */
fun StringPool.send(player: ProxiedPlayer, pack: LangPack? = null, vararg args: LangArg) {
    val message = if (pack != null) {
        process(pack, pack.getLanguage(player), *args)
    } else {
        get()
    }
    player.sendMessage(TextComponent(message))
}

/**
 * Broadcasts the StringPool to all online players on the server.
 */
fun StringPool.broadcast() {
    val message = get()
    val server = ProxyServer.getInstance()
    for (player in server.players) {
        player.sendMessage(TextComponent(message))
    }
}

/**
 * Broadcasts the StringPool to all online players on the server.
 *
 * @param pack The package to process the text.
 * @param args (Optional) Additional arguments to provide to process the text.
 */
fun StringPool.broadcast(pack: LangPack, vararg args: LangArg) {

    val cache = EnumMap<Language, String>(Language::class.java)

    val server = ProxyServer.getInstance()
    for (player in server.players) {

        val message: String
        val lang = pack.getLanguage(player)

        if (cache[lang] != null) {
            message = cache[lang]!!
        } else {
            message = process(pack, lang, *args)
            cache[lang] = message
        }

        player.sendMessage(TextComponent(message))
    }
}
