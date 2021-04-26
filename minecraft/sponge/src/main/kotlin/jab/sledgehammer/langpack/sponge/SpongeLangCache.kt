@file:Suppress("unused")

package jab.sledgehammer.langpack.sponge

import jab.sledgehammer.langpack.core.LangCache
import jab.sledgehammer.langpack.core.Language
import jab.sledgehammer.langpack.core.objects.LangArg
import org.spongepowered.api.entity.living.player.Player

/**
 * **SpongeLangCache** wraps the [LangCache] class to provide additional support for the Sponge API.
 *
 * @author Jab
 *
 * @param pack The SpongeLangPack instance.
 */
class SpongeLangCache(pack: SpongeLangPack) : LangCache<SpongeLangPack>(pack) {

    /**
     * @see SpongeLangPack.broadcast
     */
    fun broadcast(field: String, vararg args: LangArg) {
        pack.broadcast(field, *args)
    }

    /**
     * @see SpongeLangPack.message
     */
    fun message(player: Player, field: String, vararg args: LangArg) {
        pack.message(player, field, *args)
    }

    /**
     * @see SpongeLangPack.getLanguage
     */
    fun getLanguage(player: Player): Language = pack.getLanguage(player)
}
