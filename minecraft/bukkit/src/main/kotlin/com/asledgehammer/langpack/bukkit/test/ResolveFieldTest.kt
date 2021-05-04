package com.asledgehammer.langpack.bukkit.test

import com.asledgehammer.langpack.bukkit.BukkitLangPack
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.test.TestResult
import org.bukkit.entity.Player

/**
 * **ResolveFieldTest** tests the integrity of relative and absolute look-ups in lang files.
 *
 * @author Jab
 *
 * @param pack The pack instance.
 */
class ResolveFieldTest(pack: BukkitLangPack) : BukkitLangTest(pack, "resolve_field") {

    override fun run(pack: BukkitLangPack, player: Player, vararg args: LangArg): TestResult {
        pack.message(player, "tests.$id.message", LangArg("player", player.name))
        return TestResult.success()
    }
}
