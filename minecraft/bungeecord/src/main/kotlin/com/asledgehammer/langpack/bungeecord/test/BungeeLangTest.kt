package com.asledgehammer.langpack.bungeecord.test

import com.asledgehammer.langpack.bungeecord.BungeeLangPack
import com.asledgehammer.langpack.core.test.LangTest
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * TODO: Document.
 *
 * @author Jab
 */
abstract class BungeeLangTest private constructor(id: String, description: List<String>) :
    LangTest<BungeeLangPack, ProxiedPlayer>(id, description) {

    constructor(pack: BungeeLangPack, id: String) : this(id, getDescription(id, pack))

    companion object {
        private fun getDescription(id: String, pack: BungeeLangPack): List<String> {
            val query = "tests.$id.description"
            val description = pack.getList(query)
            require(description != null) {
                "The description does not exist for test \"$id\".  ($query)"
            }
            return description
        }
    }
}
