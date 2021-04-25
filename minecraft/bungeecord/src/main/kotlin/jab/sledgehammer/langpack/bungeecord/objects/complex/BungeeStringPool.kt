@file:Suppress("unused")

package jab.sledgehammer.langpack.bungeecord.objects.complex

import jab.sledgehammer.config.ConfigSection
import jab.sledgehammer.langpack.bungeecord.BungeeLangPack
import jab.sledgehammer.langpack.core.Language
import jab.sledgehammer.langpack.core.objects.LangArg
import jab.sledgehammer.langpack.core.objects.complex.Complex
import jab.sledgehammer.langpack.core.objects.complex.StringPool
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

/**
 * **BungeeStringPool** wraps the StringPool class to provide additional support for the BungeeCord API.
 *
 * @author Jab
 */
class BungeeStringPool : StringPool {

    /**
     * Empty constructor.
     *
     * Uses default mode of [StringPool.Mode.RANDOM].
     * Uses default random instance from LangPack.
     */
    constructor() : super()

    /**
     * Lite constructor.
     *
     * Uses default random instance from LangPack.
     *
     * @param mode The mode of the StringPool. (DEFAULT: [StringPool.Mode.RANDOM])
     */
    constructor(mode: Mode) : super(mode)

    /**
     * Basic constructor.
     *
     * @param mode The mode of the StringPool. (DEFAULT: [StringPool.Mode.RANDOM])
     * @param random The random instance to use.
     */
    constructor(mode: Mode, random: Random) : super(mode, random)

    /**
     * Full constructor.
     *
     * @param mode (Optional) The mode of the StringPool. (DEFAULT: [StringPool.Mode.RANDOM])
     * @param random (Optional) The random instance to use.
     * @param strings The pool of strings to use.
     */
    constructor(mode: Mode, random: Random, strings: Collection<String>) : super(mode, random, strings)

    /**
     * Import constructor.
     *
     * Uses default random instance from LangPack.
     *
     * @param cfg The ConfigurationSection to load.
     */
    constructor(cfg: ConfigSection) : super(cfg)

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
        val cache = HashMap<Language, String>()
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

    /**
     * The **BungeeStringPool.Loader** class overrides [StringPool] with [BungeeStringPool].
     *
     * @author Jab
     */
    class Loader : Complex.Loader<BungeeStringPool> {
        override fun load(cfg: ConfigSection): BungeeStringPool = BungeeStringPool(cfg)
    }
}
