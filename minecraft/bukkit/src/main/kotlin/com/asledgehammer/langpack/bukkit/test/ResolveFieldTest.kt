package com.asledgehammer.langpack.bukkit.test

import com.asledgehammer.langpack.bukkit.BukkitLangPack
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.test.TestResult
import org.bukkit.entity.Player

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @param pack
 */
class ResolveFieldTest(pack: BukkitLangPack) : SpigotLangTest(pack, "resolve_field") {

    override fun run(pack: BukkitLangPack, player: Player, vararg args: LangArg): TestResult {
        pack.message(player, "tests.$id.message", LangArg("player", player.name))
        return TestResult.success()
    }
}
