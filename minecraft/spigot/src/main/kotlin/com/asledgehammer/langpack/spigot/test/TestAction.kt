package com.asledgehammer.langpack.spigot.test

import com.asledgehammer.langpack.core.test.LangTest
import com.asledgehammer.langpack.core.test.TestResult
import com.asledgehammer.langpack.spigot.SpigotLangPack
import com.asledgehammer.langpack.textcomponent.objects.complex.ActionText
import org.bukkit.entity.Player

/**
 * **TestAction** tests a basic [ActionText].
 *
 * @author Jab
 */
class TestAction(description: List<String>) : LangTest<SpigotLangPack, Player>("action", description) {
    override fun run(pack: SpigotLangPack, player: Player): TestResult {
        pack.message(player, "test.action.message")
        return TestResult(true)
    }
}
