@file:Suppress("unused")

package com.asledgehammer.langpack.bungeecord.objects.complex

import com.asledgehammer.cfg.CFGSection
import com.asledgehammer.langpack.bungeecord.BungeeLangPack
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.complex.Complex
import com.asledgehammer.langpack.textcomponent.objects.complex.TextComponentActionText
import com.asledgehammer.langpack.textcomponent.objects.complex.TextComponentCommandText
import com.asledgehammer.langpack.textcomponent.objects.complex.TextComponentHoverText
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * **BungeeActionText** wraps the [TextComponentActionText] class to provide additional support for the BungeeCord API.
 *
 * @author Jab
 */
class BungeeActionText : TextComponentActionText {

    /**
     * None constructor.
     *
     * @param text The text to display.
     */
    constructor(text: String) : super(text)

    /**
     * Hover constructor.
     *
     * @param text The text to display.
     * @param hoverText The hover text to display.
     */
    constructor(text: String, hoverText: TextComponentHoverText) : super(text, hoverText)

    /**
     * Command constructor.
     *
     * @param text The text to display.
     * @param command The command to execute.
     */
    constructor(text: String, command: String) : super(text, command)

    /**
     * Full primitives constructor
     *
     * @param text The text to display.
     * @param command The command to execute.
     * @param hover The hover text to display.
     */
    constructor(text: String, command: String, hover: List<String>) : super(text, command, hover)

    /**
     * Full objects constructor
     *
     * @param text The text to display.
     * @param commandText The command to execute.
     * @param hoverText The hover text to display.
     */
    constructor(text: String, commandText: TextComponentCommandText, hoverText: TextComponentHoverText) : super(text,
        commandText,
        hoverText)

    /**
     * Import constructor.
     *
     * @param cfg The YAML to read.
     */
    constructor(cfg: CFGSection) : super(cfg)

    /**
     * Sends the ActionText to a given player.
     *
     * @param player The player to send.
     */
    fun message(player: ProxiedPlayer) {
        // Make sure that only online players are processed.
        if (!player.isConnected) return
        player.sendMessage(get())
    }

    /**
     * Sends the ActionText as a message to a player.
     *
     * @param player The player to receive the message.
     * @param pack (Optional) The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun send(player: ProxiedPlayer, pack: BungeeLangPack? = null, vararg args: LangArg) {
        val textComponent = if (pack != null) {
            process(pack, pack.getLanguage(player), definition?.parent, *args)
        } else {
            get()
        }
        player.sendMessage(textComponent)
    }

    /**
     * Broadcasts the ActionText to all online players on the server.
     */
    fun broadcast() {
        val message = get()
        val server = ProxyServer.getInstance()
        for (player in server.players) player.sendMessage(message)
    }

    /**
     * Broadcasts the ActionText to all online players on the server.
     *
     * @param pack The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun broadcast(pack: BungeeLangPack, vararg args: LangArg) {
        val cache = HashMap<Language, TextComponent>()
        val server = ProxyServer.getInstance()
        for (player in server.players) {
            val textComponent: TextComponent
            val lang = pack.getLanguage(player)
            if (cache[lang] != null) {
                textComponent = cache[lang]!!
            } else {
                textComponent = process(pack, pack.getLanguage(player), definition?.parent, *args)
                cache[lang] = textComponent
            }
            player.sendMessage(textComponent)
        }
    }

    /**
     * **BungeeActionText.Loader** overrides [TextComponentActionText] with [BungeeActionText].
     *
     * @author Jab
     */
    class Loader : Complex.Loader<BungeeActionText> {
        override fun load(cfg: CFGSection): BungeeActionText = BungeeActionText(cfg)
    }
}
