package com.asledgehammer.langpack.sponge.test

import com.asledgehammer.langpack.core.test.LangTest
import com.asledgehammer.langpack.sponge.SpongeLangPack
import org.spongepowered.api.entity.living.player.Player

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @param id
 * @param description
 */
abstract class SpongeLangTest private constructor(id: String, description: List<String>) :
    LangTest<SpongeLangPack, Player>(id, description) {

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
