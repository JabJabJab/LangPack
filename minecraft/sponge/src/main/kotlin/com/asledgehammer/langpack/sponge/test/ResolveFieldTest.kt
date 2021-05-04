package com.asledgehammer.langpack.sponge.test

import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.test.TestResult
import com.asledgehammer.langpack.sponge.SpongeLangPack
import org.spongepowered.api.entity.living.player.Player

/**
 * **ResolveFieldTest** tests the integrity of relative and absolute look-ups in lang files.
 *
 * @author Jab
 *
 * @param pack The pack instance.
 */
class ResolveFieldTest(pack: SpongeLangPack) : SpongeLangTest(pack, "resolve_field") {

    override fun run(pack: SpongeLangPack, player: Player, vararg args: LangArg): TestResult {
        pack.message(player, "tests.$id.message", LangArg("player", player.name))
        return TestResult.success()
    }
}
