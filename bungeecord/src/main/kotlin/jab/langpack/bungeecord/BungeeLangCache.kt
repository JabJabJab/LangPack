package jab.langpack.bungeecord

import jab.langpack.commons.LangArg
import jab.langpack.commons.LangCache
import jab.langpack.commons.Language
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * TODO: Document.
 *
 * @author Jab
 */
@Suppress("unused")
class BungeeLangCache(pack: BungeeLangPack) : LangCache<BungeeLangPack>(pack) {

    /**
     * @see SpigotLangPack.broadcast
     */
    fun broadcast(field: String, vararg args: LangArg) {
        pack.broadcast(field, *args)
    }

    /**
     * @see SpigotLangPack.message
     */
    fun message(player: ProxiedPlayer, field: String, vararg args: LangArg) {
        return pack.message(player, field, *args)
    }

    /**
     * @see SpigotLangPack.getLanguage
     */
    fun getLanguage(player: ProxiedPlayer): Language {
        return pack.getLanguage(player)
    }
}