@file:Suppress("unused")

package com.asledgehammer.langpack.bukkit.objects.complex

import com.asledgehammer.config.ConfigSection
import com.asledgehammer.langpack.bukkit.BukkitLangPack
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.complex.Complex
import com.asledgehammer.langpack.core.objects.complex.LegacyActionText
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player

/**
 * **BukkitActionText** wraps the [LegacyActionText] class to provide additional support for the Bukkit API.
 *
 * @author Jab
 */
class BukkitActionText : LegacyActionText {

    constructor(text: String) : super(text)
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
    fun send(player: Player, pack: BukkitLangPack? = null, vararg args: LangArg) {
        val message = if (pack != null) {
            process(pack, pack.getLanguage(player), definition?.parent, *args)
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
    fun broadcast(pack: BukkitLangPack, vararg args: LangArg) {
        val cache = HashMap<Language, String>()
        for (player in Bukkit.getOnlinePlayers()) {
            val message: String
            val lang = pack.getLanguage(player)
            if (cache[lang] != null) {
                message = cache[lang]!!
            } else {
                message = process(pack, pack.getLanguage(player), definition?.parent, *args)
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
    fun broadcast(world: World, pack: BukkitLangPack, vararg args: LangArg) {
        val cache = HashMap<Language, String>()
        for (player in world.players) {
            val message: String
            val lang = pack.getLanguage(player)
            if (cache[lang] != null) {
                message = cache[lang]!!
            } else {
                message = process(pack, lang, definition?.parent, *args)
                cache[lang] = message
            }
            player.sendMessage(message)
        }
    }

    /**
     * The **BukkitActionText.Loader** overrides [LegacyActionText] with [BukkitActionText].
     *
     * @author Jab
     */
    class Loader : Complex.Loader<BukkitActionText> {
        override fun load(cfg: ConfigSection): BukkitActionText = BukkitActionText(cfg)
    }
}
