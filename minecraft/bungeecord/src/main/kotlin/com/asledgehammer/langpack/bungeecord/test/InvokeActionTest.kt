package com.asledgehammer.langpack.bungeecord.test

import com.asledgehammer.langpack.bungeecord.BungeeLangPack
import com.asledgehammer.langpack.bungeecord.objects.complex.BungeeActionText
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.definition.ComplexDefinition
import com.asledgehammer.langpack.core.test.TestResult
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @param pack
 */
class InvokeActionTest(pack: BungeeLangPack) : BungeeLangTest(pack, "invoke_action") {

    override fun run(pack: BungeeLangPack, player: ProxiedPlayer, vararg args: LangArg): TestResult {
        val lang = pack.getLanguage(player)
        val def = pack.resolve("tests.$id.message", lang)!! as ComplexDefinition
        val actionText = def.value as BungeeActionText
        actionText.send(player, pack, *args)
        return TestResult.success()
    }
}
