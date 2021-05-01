package com.asledgehammer.langpack.bukkit.test

import com.asledgehammer.langpack.bukkit.BukkitLangPack
import com.asledgehammer.langpack.core.test.LangTest
import org.bukkit.entity.Player

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @param id
 * @param description
 */
abstract class SpigotLangTest private constructor(id: String, description: List<String>) :
    LangTest<BukkitLangPack, Player>(id, description) {

    constructor(pack: BukkitLangPack, id: String) : this(id, getDescription(id, pack))

    companion object {
        private fun getDescription(id: String, pack: BukkitLangPack): List<String> {
            val query = "tests.$id.description"
            val description = pack.getList(query)
            require(description != null) {
                "The description does not exist for test \"$id\". " +
                        "($query)"
            }
            return description
        }
    }
}
