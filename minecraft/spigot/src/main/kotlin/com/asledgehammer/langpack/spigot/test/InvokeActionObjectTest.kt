package com.asledgehammer.langpack.spigot.test

import com.asledgehammer.langpack.core.objects.definition.ComplexDefinition
import com.asledgehammer.langpack.core.test.TestResult
import com.asledgehammer.langpack.spigot.SpigotLangPack
import com.asledgehammer.langpack.spigot.objects.complex.SpigotActionText
import org.bukkit.entity.Player

class InvokeActionObjectTest(pack: SpigotLangPack) : SpigotLangTest(pack, "invoke_action_object") {

    override fun run(pack: SpigotLangPack, player: Player): TestResult {
        val lang = pack.getLanguage(player)
        val def = pack.resolve("tests.invoke_action_object.message", lang)!! as ComplexDefinition
        val actionText = def.value as SpigotActionText
        actionText.send(player, pack)
        return TestResult.success()
    }
}