package com.asledgehammer.langpack.spigot.test

import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.test.TestResult
import com.asledgehammer.langpack.spigot.SpigotLangPack
import com.asledgehammer.langpack.spigot.objects.complex.SpigotStringPool
import org.bukkit.entity.Player

/**
 * **ResolveFieldTest** tests the integrity of relative and absolute look-ups in lang files.
 *
 * @author Jab
 *
 * @param pack The pack instance.
 */
class ResolveFieldTest(pack: SpigotLangPack) : SpigotLangTest(pack, "resolve_field") {

    override fun run(pack: SpigotLangPack, player: Player, vararg args: LangArg): TestResult {
        pack.message(player, "tests.$id.message", LangArg("player", player.name))
        return TestResult.success()
    }
}
