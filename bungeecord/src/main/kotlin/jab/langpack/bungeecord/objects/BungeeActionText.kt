package jab.langpack.bungeecord.objects

import jab.langpack.bungeecord.BungeeLangPack
import jab.langpack.commons.LangArg
import jab.langpack.commons.Language
import jab.langpack.commons.objects.ActionText
import jab.langpack.commons.objects.HoverText
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.bukkit.configuration.ConfigurationSection
import java.util.*

/**
 * The **BungeeActionText** class wraps [ActionText] for the Bungeecord environment of lang-pack.
 *
 * @author Jab
 */
@Suppress("unused")
class BungeeActionText : ActionText {

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
    fun send(player: ProxiedPlayer, pack: BungeeLangPack? = null, vararg args: LangArg) {

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
                textComponent = process(pack, pack.getLanguage(player), *args)
                cache[lang] = textComponent
            }

            player.sendMessage(textComponent)
        }
    }
}