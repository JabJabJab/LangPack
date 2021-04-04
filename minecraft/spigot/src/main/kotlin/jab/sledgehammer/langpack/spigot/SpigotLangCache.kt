@file:Suppress("unused")

package jab.sledgehammer.langpack.spigot

import jab.sledgehammer.langpack.core.LangCache
import jab.langpack.core.Language
import jab.langpack.core.objects.LangArg
import org.bukkit.entity.Player

/**
 * **SpigotLangCache** wraps the LangCache class to provide additional support for the Spigot API.
 *
 * @author Jab
 *
 * @param pack The SpigotLangPack instance.
 */
class SpigotLangCache(pack: SpigotLangPack) : jab.sledgehammer.langpack.core.LangCache<SpigotLangPack>(pack) {

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
