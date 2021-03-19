@file:Suppress("unused")

package jab.langpack.bungeecord.objects.complex

import jab.langpack.bungeecord.BungeeLangPack
import jab.langpack.core.Language
import jab.langpack.core.objects.LangArg
import jab.langpack.core.objects.complex.Complex
import jab.langpack.core.objects.complex.StringPool
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.bukkit.configuration.ConfigurationSection
import java.util.*

class BungeeStringPool : StringPool {

    /**
     * @see StringPool
     */
    constructor(mode: Mode, random: Random) : super(mode, random)

    /**
     * @see StringPool
     */
    constructor(mode: Mode, random: Random, strings: ArrayList<String>) : super(mode, random, strings)

    /**
     * @see StringPool
     */
    constructor(cfg: ConfigurationSection) : super(cfg)

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
    fun StringPool.send(player: ProxiedPlayer, pack: BungeeLangPack? = null, vararg args: LangArg) {
        val message = if (pack != null) {
            process(pack, pack.getLanguage(player), null, *args)
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
    fun broadcast(pack: BungeeLangPack, vararg args: LangArg) {

        val cache = EnumMap<Language, String>(Language::class.java)

        val server = ProxyServer.getInstance()
        for (player in server.players) {

            val message: String
            val lang = pack.getLanguage(player)

            if (cache[lang] != null) {
                message = cache[lang]!!
            } else {
                message = process(pack, lang, null, *args)
                cache[lang] = message
            }

            player.sendMessage(TextComponent(message))
        }
    }

    class Loader : Complex.Loader<BungeeStringPool> {
        override fun load(cfg: ConfigurationSection): BungeeStringPool = BungeeStringPool(cfg)
    }
}