package jab.langpack.bungeecord.objects

import jab.langpack.bungeecord.BungeeLangPack
import jab.langpack.commons.LangArg
import jab.langpack.commons.Language
import jab.langpack.commons.objects.ActionText
import jab.langpack.commons.objects.StringPool
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

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
fun ActionText.send(player: ProxiedPlayer, pack: BungeeLangPack? = null, vararg args: LangArg) {

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
fun ActionText.broadcast(pack: BungeeLangPack, vararg args: LangArg) {

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
 * Sends the ActionText to a given player.
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
 * Sends the ActionText as a message to a player.
 *
 * @param player The player to receive the message.
 * @param pack (Optional) The package to process the text.
 * @param args (Optional) Additional arguments to provide to process the text.
 */
fun StringPool.send(player: ProxiedPlayer, pack: BungeeLangPack? = null, vararg args: LangArg) {
    val message = if (pack != null) {
        process(pack, pack.getLanguage(player), *args)
    } else {
        get()
    }
    player.sendMessage(TextComponent(message))
}

/**
 * Broadcasts the ActionText to all online players on the server.
 */
fun StringPool.broadcast() {
    val message = get()
    val server = ProxyServer.getInstance()
    for (player in server.players) {
        player.sendMessage(TextComponent(message))
    }
}

/**
 * Broadcasts the ActionText to all online players on the server.
 *
 * @param pack The package to process the text.
 * @param args (Optional) Additional arguments to provide to process the text.
 */
fun StringPool.broadcast(pack: BungeeLangPack, vararg args: LangArg) {

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
