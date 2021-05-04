package com.asledgehammer.langpack.bukkit.test

import com.asledgehammer.langpack.bukkit.BukkitLangPack
import com.asledgehammer.langpack.bukkit.objects.complex.BukkitActionText
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.definition.ComplexDefinition
import com.asledgehammer.langpack.core.test.TestResult
import org.bukkit.entity.Player

/**
 * **InvokeActionTest** tests the integrity of [BukkitActionText] through the API calls through the Bukkit wrapper.
 *
 * @author Jab
 *
 * @param pack The pack instance.
 */
class InvokeActionTest(pack: BukkitLangPack) : BukkitLangTest(pack, "invoke_action") {

    override fun run(pack: BukkitLangPack, player: Player, vararg args: LangArg): TestResult {
        val lang = pack.getLanguage(player)
        val def = pack.resolve("tests.$id.message", lang)!! as ComplexDefinition
        val actionText = def.value as BukkitActionText
        actionText.send(player, pack, *args)
        return TestResult.success()
    }
}
