@file:Suppress("unused")

package jab.sledgehammer.langpack.spigot.objects.complex

import jab.sledgehammer.langpack.core.Language
import jab.sledgehammer.langpack.core.objects.LangArg
import jab.sledgehammer.langpack.core.objects.complex.Complex
import jab.sledgehammer.langpack.core.objects.complex.StringPool
import jab.sledgehammer.langpack.spigot.SpigotLangPack
import jab.sledgehammer.config.ConfigSection
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

/**
 * **SpigotStringPool** wraps the StringPool class to provide additional support for the Spigot API.
 *
 * @author Jab
 */
class SpigotStringPool : StringPool {

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
        val cache = HashMap<Language, String>()
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
        val cache = HashMap<Language, String>()
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
     * The **SpigotStringPool.Loader** overrides [StringPool] with [SpigotStringPool].
     *
     * @author Jab
     */
    class Loader : Complex.Loader<SpigotStringPool> {
        override fun load(cfg: ConfigSection): SpigotStringPool = SpigotStringPool(cfg)
    }
}
