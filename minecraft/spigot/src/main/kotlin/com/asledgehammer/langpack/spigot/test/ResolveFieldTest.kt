package com.asledgehammer.langpack.spigot.test

import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.test.TestResult
import com.asledgehammer.langpack.spigot.SpigotLangPack
import org.bukkit.entity.Player

class ResolveFieldTest(pack: SpigotLangPack) : SpigotLangTest(pack, "resolve_field") {

    override fun run(pack: SpigotLangPack, player: Player, vararg args: LangArg): TestResult {
        pack.message(player, "tests.$id.message", LangArg("player", player.name))
        return TestResult.success()
    }
}