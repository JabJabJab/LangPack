package jab.langpack.bungeecord

import jab.langpack.commons.LangArg
import jab.langpack.commons.LangCache
import jab.langpack.commons.Language
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * The **BungeeLangCache** class wraps [LangCache] to add methods specific for the Bungeecord environment.
 *
 * TODO: Implement TextComponent cache.
 *
 * @author Jab
 */
@Suppress("unused")
class BungeeLangCache(pack: BungeeLangPack) : LangCache<BungeeLangPack>(pack) {

    /**
     * @see BungeeLangPack.broadcast
     */
    fun broadcast(field: String, vararg args: LangArg) = pack.broadcast(field, *args)

    /**
     * @see BungeeLangPack.message
     */
    fun message(player: ProxiedPlayer, field: String, vararg args: LangArg) = pack.message(player, field, *args)

    /**
     * @see BungeeLangPack.getLanguage
     */
    fun getLanguage(player: ProxiedPlayer): Language = pack.getLanguage(player)
}
