@file:Suppress("unused")

package jab.langpack.spigot.objects.complex

import jab.langpack.core.Language
import jab.langpack.core.objects.LangArg
import jab.langpack.core.objects.complex.Complex
import jab.langpack.core.objects.complex.StringPool
import jab.langpack.spigot.SpigotLangPack
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*

/**
 * TODO: Document.
 *
 * @author Jab
 */
class SpigotStringPool : StringPool {

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
    fun message(player: Player) {
        if (!player.isOnline) return
        player.sendMessage(get())
    }

    /**
     * Sends the StringPool as a message to a player.
     *
     * @param player The player to receive the message.
     * @param pack (Optional) The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun send(player: Player, pack: SpigotLangPack? = null, vararg args: LangArg) {
        val message = if (pack != null) {
            process(pack, pack.getLanguage(player), null, *args)
        } else {
            get()
        }
        player.sendMessage(message)
    }

    /**
     * Broadcasts the StringPool to all online players on the server.
     */
    fun broadcast() {
        val message = get()
        for (player in Bukkit.getOnlinePlayers()) {
            player.sendMessage(message)
        }
    }

    /**
     * Broadcasts the StringPool to all players in a given world.
     *
     * @param world The world to broadcast.
     */
    fun broadcast(world: World) {
        val message = get()
        for (player in world.players) {
            player.sendMessage(message)
        }
    }

    /**
     * Broadcasts the StringPool to all online players on the server.
     *
     * @param pack The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun broadcast(pack: SpigotLangPack, vararg args: LangArg) {

        val cache = EnumMap<Language, String>(Language::class.java)

        for (player in Bukkit.getOnlinePlayers()) {

            val message: String
            val lang = pack.getLanguage(player)

            if (cache[lang] != null) {
                message = cache[lang]!!
            } else {
                message = process(pack, pack.getLanguage(player), null, *args)
                cache[lang] = message
            }

            player.sendMessage(message)
        }
    }

    /**
     * Broadcasts the StringPool to all players in a given world.
     *
     * @param pack The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun broadcast(world: World, pack: SpigotLangPack, vararg args: LangArg) {

        val cache = EnumMap<Language, String>(Language::class.java)

        for (player in world.players) {

            val message: String
            val lang = pack.getLanguage(player)

            if (cache[lang] != null) {
                message = cache[lang]!!
            } else {
                message = process(pack, lang, null, *args)
                cache[lang] = message
            }

            player.sendMessage(message)
        }
    }

    /**
     * TODO: Document.
     *
     * @author Jab
     */
    class Loader : Complex.Loader<SpigotStringPool> {
        override fun load(cfg: ConfigurationSection): SpigotStringPool = SpigotStringPool(cfg)
    }
}
