package com.asledgehammer.langpack.bukkit.test

import com.asledgehammer.langpack.bukkit.BukkitLangPack
import com.asledgehammer.langpack.core.test.LangTest
import org.bukkit.entity.Player

/**
 * **BukkitLangTest** is a Bukkit-specific abstraction to test API through [BukkitLangPack].
 *
 * @author Jab
 *
 * @param id The id of the test.
 * @param description The description of the test.
 */
abstract class BukkitLangTest private constructor(id: String, description: List<String>) :
    LangTest<BukkitLangPack, Player>(id, description) {

    /**
     * @param pack The pack instance.
     * @param id The id of the test.
     */
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
