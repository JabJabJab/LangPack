@file:Suppress("unused")

package com.asledgehammer.langpack.bukkit

import com.asledgehammer.langpack.core.LangCache
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.objects.LangArg
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
