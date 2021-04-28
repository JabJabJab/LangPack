package com.asledgehammer.langpack.sponge.test

import com.asledgehammer.langpack.core.test.LangTest
import com.asledgehammer.langpack.core.test.TestResult
import com.asledgehammer.langpack.sponge.SpongeLangPack
import org.spongepowered.api.entity.living.player.Player

/**
 * **TestBroadcast** tests a basic broadcast call to [SpongeLangPack].
 *
 * @author Jab
 */
class TestBroadcast(description: List<String>) : LangTest<SpongeLangPack, Player>("broadcast", description) {
    override fun run(pack: SpongeLangPack, player: Player): TestResult {
        pack.broadcast("test.broadcast.message")
        return TestResult.success()
    }
}
