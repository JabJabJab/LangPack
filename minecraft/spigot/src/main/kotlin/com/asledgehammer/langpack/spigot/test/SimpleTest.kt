package com.asledgehammer.langpack.spigot.test

import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.test.TestResult
import com.asledgehammer.langpack.spigot.SpigotLangPack
import org.bukkit.entity.Player

/**
 * **SimpleTest** is a implementation to test a field in a lang file in a simpler way.
 *
 * @author Jab
 *
 * @param pack The pack instance.
 * @param id The id of the test.
 */
class SimpleTest(pack: SpigotLangPack, id: String): SpigotLangTest(pack,id) {

    override fun run(pack: SpigotLangPack, player: Player, vararg args: LangArg): TestResult {
        pack.message(player, "tests.$id.message", *args)
        return TestResult.success()
    }
}
