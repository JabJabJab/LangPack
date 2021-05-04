package com.asledgehammer.langpack.sponge.test

import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.test.TestResult
import com.asledgehammer.langpack.sponge.SpongeLangPack
import org.spongepowered.api.entity.living.player.Player

/**
 * **SimpleTest** is a implementation to test a field in a lang file in a simpler way.
 *
 * @author Jab
 *
 * @param pack The pack instance.
 * @param id The id of the test.
 */
class SimpleTest(pack: SpongeLangPack, id: String) : SpongeLangTest(pack, id) {

    override fun run(pack: SpongeLangPack, player: Player, vararg args: LangArg): TestResult {
        pack.message(player, "tests.$id.message", *args)
        return TestResult.success()
    }
}
