@file:Suppress("unused")

package com.asledgehammer.langpack.bukkit.objects.complex

import com.asledgehammer.cfg.CFGSection
import com.asledgehammer.langpack.bukkit.BukkitLangPack
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.complex.Complex
import com.asledgehammer.langpack.core.objects.complex.StringPool
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

/**
 * **BukkitStringPool** wraps the StringPool class to provide additional support for the Bukkit API.
 *
 * @author Jab
 */
class BukkitStringPool : StringPool {

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
    constructor(cfg: CFGSection) : super(cfg)

    /**
     * Sends the ActionText to a given player.
     *
     * @param player The player to send.
     */
    fun message(player: Player) {
        if (!player.isOnline) return
        player.sendMessage(get())
    }

    /**
     * Sends the ActionText as a message to a player.
     *
     * @param player The player to receive the message.
     * @param pack (Optional) The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun send(player: Player, pack: BukkitLangPack? = null, vararg args: LangArg) {
        val text = if (pack != null) {
            process(pack, pack.getLanguage(player), definition?.parent, *args)
        } else {
            get()
        }
        player.sendMessage(text)
    }

    /**
     * Broadcasts the ActionText to all online players on the server.
     */
    fun broadcast() {
        val message = get()
        for (player in Bukkit.getOnlinePlayers()) player.sendMessage(message)
    }

    /**
     * Broadcasts the ActionText to all players in a given world.
     *
     * @param world The world to broadcast.
     */
    fun broadcast(world: World) {
        val message = get()
        for (player in world.players) player.sendMessage(message)
    }

    /**
     * Broadcasts the ActionText to all online players on the server.
     *
     * @param pack The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun broadcast(pack: BukkitLangPack, vararg args: LangArg) {
        val cache = HashMap<Language, String>()
        for (player in Bukkit.getOnlinePlayers()) {
            val text: String
            val lang = pack.getLanguage(player)
            if (cache[lang] != null) {
                text = cache[lang]!!
            } else {
                text = process(pack, pack.getLanguage(player), definition?.parent, *args)
                cache[lang] = text
            }
            player.sendMessage(text)
        }
    }

    /**
     * Broadcasts the ActionText to all players in a given world.
     *
     * @param pack The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun broadcast(world: World, pack: BukkitLangPack, vararg args: LangArg) {
        val cache = HashMap<Language, String>()
        for (player in world.players) {
            val text: String
            val lang = pack.getLanguage(player)
            if (cache[lang] != null) {
                text = cache[lang]!!
            } else {
                text = process(pack, lang, definition?.parent, *args)
                cache[lang] = text
            }
            player.sendMessage(text)
        }
    }

    /**
     * The **SpongeStringPool.Loader** overrides [StringPool] with [BukkitStringPool].
     *
     * @author Jab
     */
    class Loader : Complex.Loader<BukkitStringPool> {
        override fun load(cfg: CFGSection): BukkitStringPool = BukkitStringPool(cfg)
    }
}
