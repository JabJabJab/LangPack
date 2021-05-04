package com.asledgehammer.langpack.bukkit.test

import com.asledgehammer.langpack.bukkit.BukkitLangPack
import com.asledgehammer.langpack.bukkit.objects.complex.BukkitStringPool
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.definition.ComplexDefinition
import com.asledgehammer.langpack.core.test.TestResult
import org.bukkit.entity.Player

/**
 * **InvokePoolTest** tests the integrity of [BukkitStringPool] through the API calls through the Bukkit wrapper.
 *
 * @author Jab
 *
 * @param pack The pack instance.
 */
class InvokePoolTest(pack: BukkitLangPack) : BukkitLangTest(pack, "invoke_pool") {

    override fun run(pack: BukkitLangPack, player: Player, vararg args: LangArg): TestResult {
        val lang = pack.getLanguage(player)
        val def = pack.resolve("tests.$id.message", lang)!! as ComplexDefinition
        val stringPool = def.value as BukkitStringPool
        stringPool.send(player, pack, *args)
        stringPool.send(player, pack, *args)
        return TestResult.success()
    }
}
