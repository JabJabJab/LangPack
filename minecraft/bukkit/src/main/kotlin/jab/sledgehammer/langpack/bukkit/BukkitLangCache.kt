@file:Suppress("unused")

package jab.sledgehammer.langpack.bukkit

import jab.sledgehammer.langpack.core.LangCache
import jab.sledgehammer.langpack.core.Language
import jab.sledgehammer.langpack.core.objects.LangArg
import org.bukkit.entity.Player

/**
 * **BukkitLangCache** wraps the [LangCache] class to provide additional support for the Bukkit API.
 *
 * @author Jab
 *
 * @param pack The BukkitLangPack instance.
 */
class BukkitLangCache(pack: BukkitLangPack) : LangCache<BukkitLangPack>(pack) {

    /**
     * @see BukkitLangPack.broadcast
     */
    fun broadcast(field: String, vararg args: LangArg) {
        pack.broadcast(field, *args)
    }

    /**
     * @see BukkitLangPack.message
     */
    fun message(player: Player, field: String, vararg args: LangArg) {
        pack.message(player, field, *args)
    }

    /**
     * @see BukkitLangPack.getLanguage
     */
    fun getLanguage(player: Player): Language = pack.getLanguage(player)
}
