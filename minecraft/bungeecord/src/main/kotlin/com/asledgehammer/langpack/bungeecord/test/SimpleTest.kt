package com.asledgehammer.langpack.bungeecord.test

import com.asledgehammer.langpack.bungeecord.BungeeLangPack
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.test.TestResult
import net.md_5.bungee.api.connection.ProxiedPlayer

/**
 * **SimpleTest** is a implementation to test a field in a lang file in a simpler way.
 *
 * @author Jab
 *
 * @param pack The pack instance.
 * @param id The id of the test.
 */
class SimpleTest(pack: BungeeLangPack, id: String): BungeeLangTest(pack,id) {

    override fun run(pack: BungeeLangPack, player: ProxiedPlayer, vararg args: LangArg): TestResult {
        pack.message(player, "tests.$id.message", *args)
        return TestResult.success()
    }
}
