package com.asledgehammer.langpack.sponge.test

import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.definition.ComplexDefinition
import com.asledgehammer.langpack.core.test.TestResult
import com.asledgehammer.langpack.sponge.SpongeLangPack
import com.asledgehammer.langpack.sponge.objects.complex.SpongeStringPool
import org.spongepowered.api.entity.living.player.Player

/**
 * **InvokePoolTest** tests the integrity of [SpongeStringPool] through the API calls through the Sponge wrapper.
 *
 * @author Jab
 *
 * @param pack The pack instance.
 */
class InvokePoolTest(pack: SpongeLangPack) : SpongeLangTest(pack, "invoke_pool") {

    override fun run(pack: SpongeLangPack, player: Player, vararg args: LangArg): TestResult {
        val lang = pack.getLanguage(player)
        val def = pack.resolve("tests.$id.message", lang)!! as ComplexDefinition
        val stringPool = def.value as SpongeStringPool
        stringPool.send(player, pack, *args)
        stringPool.send(player, pack, *args)
        return TestResult.success()
    }
}
