package jab.sledgehammer.langpack.sponge.test

import jab.sledgehammer.langpack.core.test.LangTest
import jab.sledgehammer.langpack.core.test.TestResult
import jab.sledgehammer.langpack.sponge.SpongeLangPack
import jab.sledgehammer.langpack.sponge.objects.complex.SpongeActionText
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
