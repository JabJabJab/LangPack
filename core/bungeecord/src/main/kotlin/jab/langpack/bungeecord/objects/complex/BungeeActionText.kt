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
 * TODO: Document.
 *
 * @author Jab
 */
class BungeeActionText : ActionText {

    /**
     * @see ActionText
     */
    constructor(text: String) : super(text)

    /**
     * @see ActionText
     */
    constructor(text: String, hoverText: HoverText) : super(text, hoverText)

    /**
     * @see ActionText
     */
    constructor(text: String, command: String) : super(text, command)

    /**
     * @see ActionText
     */
    constructor(text: String, command: String, hover: List<String>) : super(text, command, hover)

    /**
     * @see ActionText
     */
    constructor(text: String, commandText: CommandText, hoverText: HoverText) : super(text, commandText, hoverText)

    /**
     * @see ActionText
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
     * TODO: Document.
     *
     * @author Jab
     */
    class Loader : Complex.Loader<BungeeActionText> {
        override fun load(cfg: ConfigurationSection): BungeeActionText = BungeeActionText(cfg)
    }
}
