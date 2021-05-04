package com.asledgehammer.langpack.bungeecord.test

import com.asledgehammer.langpack.bungeecord.BungeeLangPack
import com.asledgehammer.langpack.bungeecord.objects.complex.BungeeStringPool
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.definition.ComplexDefinition
import com.asledgehammer.langpack.core.test.TestResult
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * **InvokePoolTest** tests the integrity of [BungeeStringPool] through the API calls through the Bungeecord wrapper.
 *
 * @author Jab
 *
 * @param pack The pack instance.
 */
class InvokePoolTest(pack: BungeeLangPack) : BungeeLangTest(pack, "invoke_pool") {

    override fun run(pack: BungeeLangPack, player: ProxiedPlayer, vararg args: LangArg): TestResult {
        val lang = pack.getLanguage(player)
        val def = pack.resolve("tests.$id.message", lang)!! as ComplexDefinition
        val stringPool = def.value as BungeeStringPool
        stringPool.send(player, pack, *args)
        stringPool.send(player, pack, *args)
        return TestResult.success()
    }
}
