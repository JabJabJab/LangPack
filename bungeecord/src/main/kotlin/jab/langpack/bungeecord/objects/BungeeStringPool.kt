package jab.langpack.bungeecord.objects

import jab.langpack.bungeecord.BungeeLangPack
import jab.langpack.commons.LangArg
import jab.langpack.commons.Language
import jab.langpack.commons.objects.StringPool
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import org.bukkit.configuration.ConfigurationSection
import java.util.*

/**
 * The **BungeeStringPool** class wraps [StringPool] for the Bungeecord environment of lang-pack.
 *
 * @author Jab
 */
@Suppress("unused")
class BungeeStringPool: StringPool {

    /**
     * Basic constructor.
     *
     * @param mode (Optional) The mode of the StringPool. (DEFAULT: [StringPool.Mode.RANDOM])
     * @param random (Optional) The random instance to use.
     */
    constructor(mode: Mode, random: Random) : super(mode, random)

    /**
     * Import constructor.
     *
     * @param cfg The ConfigurationSection to load.
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

        player.sendMessage(TextComponent(get()))
    }

    /**
     * Sends the ActionText as a message to a player.
     *
     * @param player The player to receive the message.
     * @param pack (Optional) The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun send(player: ProxiedPlayer, pack: BungeeLangPack? = null, vararg args: LangArg) {

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
    fun broadcast() {
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
    fun broadcast(pack: BungeeLangPack, vararg args: LangArg) {

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
}