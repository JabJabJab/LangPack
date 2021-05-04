package com.asledgehammer.langpack.bungeecord.test

import com.asledgehammer.langpack.bungeecord.BungeeLangPack
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.test.TestResult
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * **ResolveFieldTest** tests the integrity of relative and absolute look-ups in lang files.
 *
 * @author Jab
 *
 * @param pack The pack instance.
 */
class ResolveFieldTest(pack: BungeeLangPack) : BungeeLangTest(pack, "resolve_field") {

    override fun run(pack: BungeeLangPack, player: ProxiedPlayer, vararg args: LangArg): TestResult {
        pack.message(player, "tests.$id.message", LangArg("player", player.name))
        return TestResult.success()
    }
}
