package com.asledgehammer.langpack.spigot.test

import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.definition.ComplexDefinition
import com.asledgehammer.langpack.core.test.TestResult
import com.asledgehammer.langpack.spigot.SpigotLangPack
import com.asledgehammer.langpack.spigot.objects.complex.SpigotStringPool
import org.bukkit.entity.Player

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @param pack
 */
class InvokePoolTest(pack: SpigotLangPack) : SpigotLangTest(pack, "invoke_pool") {

    override fun run(pack: SpigotLangPack, player: Player, vararg args: LangArg): TestResult {
        val lang = pack.getLanguage(player)
        val def = pack.resolve("tests.$id.message", lang)!! as ComplexDefinition
        val stringPool = def.value as SpigotStringPool
        stringPool.send(player, pack, *args)
        stringPool.send(player, pack, *args)
        return TestResult.success()
    }
}
