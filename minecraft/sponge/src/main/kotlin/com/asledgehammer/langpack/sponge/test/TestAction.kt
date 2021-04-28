package com.asledgehammer.langpack.sponge.test

import com.asledgehammer.langpack.core.test.LangTest
import com.asledgehammer.langpack.core.test.TestResult
import com.asledgehammer.langpack.sponge.SpongeLangPack
import com.asledgehammer.langpack.sponge.objects.complex.SpongeActionText
import org.spongepowered.api.entity.living.player.Player

/**
 * **TestAction** tests a basic [SpongeActionText].
 *
 * @author Jab
 */
class TestAction(description: List<String>) : LangTest<SpongeLangPack, Player>("action", description) {
    override fun run(pack: SpongeLangPack, player: Player): TestResult {
        pack.message(player, "test.action.message")
        return TestResult(true)
    }
}
