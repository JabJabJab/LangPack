package com.asledgehammer.langpack.sponge.test

import com.asledgehammer.langpack.core.test.LangTest
import com.asledgehammer.langpack.sponge.SpongeLangPack
import org.spongepowered.api.entity.living.player.Player

/**
 * **SpongeLangTest** is a Sponge-specific abstraction to test API through [SpongeLangTest].
 *
 * @author Jab
 *
 * @param id The id of the test.
 * @param description The description of the test.
 */
abstract class SpongeLangTest private constructor(id: String, description: List<String>) :
    LangTest<SpongeLangPack, Player>(id, description) {

    /**
     * @param pack The pack instance.
     * @param id The id of the test.
     */
    constructor(pack: SpongeLangPack, id: String) : this(id, getDescription(id, pack))

    companion object {
        private fun getDescription(id: String, pack: SpongeLangPack): List<String> {
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
