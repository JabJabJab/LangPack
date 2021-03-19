@file:Suppress("unused")

package jab.langpack.bungeecord

import jab.langpack.core.LangCache
import jab.langpack.core.Language
import jab.langpack.core.objects.LangArg
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * TODO: Document.
 *
 * @author Jab
 */
class BungeeLangCache(pack: BungeeLangPack) : LangCache<BungeeLangPack>(pack) {

    /**
     * @see BungeeLangPack.broadcast
     */
    fun broadcast(field: String, vararg args: LangArg) = pack.broadcast(field, *args)

    /**
     * @see BungeeLangPack.message
     */
    fun message(player: ProxiedPlayer, field: String, vararg args: LangArg) =
        pack.message(player, field, *args)

    /**
     * @see BungeeLangPack.getLanguage
     */
    fun getLanguage(player: ProxiedPlayer): Language = pack.getLanguage(player)
}
