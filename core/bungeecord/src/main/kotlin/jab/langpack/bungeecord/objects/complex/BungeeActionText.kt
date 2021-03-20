@file:Suppress("unused")

package jab.langpack.bungeecord.objects.complex

import jab.langpack.bungeecord.BungeeLangPack
import jab.langpack.core.Language
import jab.langpack.core.objects.LangArg
import jab.langpack.core.objects.complex.ActionText
import jab.langpack.core.objects.complex.CommandText
import jab.langpack.core.objects.complex.Complex
import jab.langpack.core.objects.complex.HoverText
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.bukkit.configuration.ConfigurationSection
import java.util.*

/**
 * **BungeeActionText** wraps the ActionText class to provide additional support for the BungeeCord API.
 *
 * @author Jab
 */
class BungeeActionText : ActionText {

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
    constructor(text: String, hoverText: HoverText) : super(text, hoverText)

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
    constructor(text: String, commandText: CommandText, hoverText: HoverText) : super(text, commandText, hoverText)

    /**
     * Import constructor.
     *
     * @param cfg The YAML to read.
     */
    constructor(cfg: ConfigurationSection) : super(cfg)

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
            process(pack, pack.getLanguage(player), null, *args)
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
    fun broadcast(pack: BungeeLangPack, vararg args: LangArg) {
        val cache = EnumMap<Language, TextComponent>(Language::class.java)
        val server = ProxyServer.getInstance()
        for (player in server.players) {
            val textComponent: TextComponent
            val lang = pack.getLanguage(player)
            if (cache[lang] != null) {
                textComponent = cache[lang]!!
            } else {
                textComponent = process(pack, pack.getLanguage(player), null, *args)
                cache[lang] = textComponent
            }
            player.sendMessage(textComponent)
        }
    }

    /**
     * **BungeeActionText.Loader** overrides [ActionText] with [BungeeActionText].
     *
     * @author Jab
     */
    class Loader : Complex.Loader<BungeeActionText> {
        override fun load(cfg: ConfigurationSection): BungeeActionText = BungeeActionText(cfg)
    }
}
