package jab.langpack.spigot

import jab.langpack.commons.LangArg
import jab.langpack.commons.LangCache
import jab.langpack.commons.Language
import org.bukkit.entity.Player

/**
 * The **SpigotLangCache** class wraps [LangCache] to add methods specific for the Spigot environment.
 *
 * TODO: Implement TextComponent cache.
 *
 * @author Jab
 */
@Suppress("unused")
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
        return pack.message(player, field, *args)
    }

    /**
     * @see SpigotLangPack.getLanguage
     */
    fun getLanguage(player: Player): Language {
        return pack.getLanguage(player)
    }
}