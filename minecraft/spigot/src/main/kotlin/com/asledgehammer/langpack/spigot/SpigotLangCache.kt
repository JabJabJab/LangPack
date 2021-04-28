@file:Suppress("unused")

package com.asledgehammer.langpack.spigot

import com.asledgehammer.langpack.core.LangCache
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.objects.LangArg
import org.bukkit.entity.Player

/**
 * **SpigotLangCache** wraps the LangCache class to provide additional support for the Spigot API.
 *
 * @author Jab
 *
 * @param pack The SpigotLangPack instance.
 */
class SpigotLangCache(pack: SpigotLangPack) : LangCache<SpigotLangPack>(pack) {

    /**
     * @see SpigotLangPack.broadcast
     */
    fun broadcast(field: String, vararg args: LangArg) {
        pack.broadcast(field, *args)
    }

    /**
     * @see SpigotLangPack.message
     */
    fun message(player: Player, field: String, vararg args: LangArg) {
        pack.message(player, field, *args)
    }

    /**
     * @see SpigotLangPack.getLanguage
     */
    fun getLanguage(player: Player): Language = pack.getLanguage(player)
}
