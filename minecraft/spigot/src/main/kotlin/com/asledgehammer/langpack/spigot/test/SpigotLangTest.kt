package com.asledgehammer.langpack.spigot.test

import com.asledgehammer.langpack.core.test.LangTest
import com.asledgehammer.langpack.spigot.SpigotLangPack
import org.bukkit.entity.Player

/**
 * **SpigotLangTest** is a Spigot-specific abstraction to test API through [SpigotLangPack].
 *
 * @author Jab
 *
 * @param id The id of the test.
 * @param description The description of the test.
 */
abstract class SpigotLangTest private constructor(id: String, description: List<String>) :
    LangTest<SpigotLangPack, Player>(id, description) {

    /**
     * @param pack The pack instance.
     * @param id The id of the test.
     */
    constructor(pack: SpigotLangPack, id: String) : this(id, getDescription(id, pack))

    companion object {
        private fun getDescription(id: String, pack: SpigotLangPack): List<String> {
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
