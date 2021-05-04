package com.asledgehammer.langpack.bukkit.test

import com.asledgehammer.langpack.bukkit.BukkitLangPack
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.test.TestResult
import org.bukkit.entity.Player

/**
 * **SimpleTest** is a implementation to test a field in a lang file in a simpler way.
 *
 * @author Jab
 *
 * @param pack The pack instance.
 * @param id The id of the test.
 */
class SimpleTest(pack: BukkitLangPack, id: String) : BukkitLangTest(pack, id) {

    override fun run(pack: BukkitLangPack, player: Player, vararg args: LangArg): TestResult {
        pack.message(player, "tests.$id.message", *args)
        return TestResult.success()
    }
}
