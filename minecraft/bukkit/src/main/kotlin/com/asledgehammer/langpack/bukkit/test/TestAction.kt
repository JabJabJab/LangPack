package com.asledgehammer.langpack.bukkit.test

import com.asledgehammer.langpack.bukkit.BukkitLangPack
import com.asledgehammer.langpack.bukkit.objects.complex.BukkitActionText
import com.asledgehammer.langpack.core.test.LangTest
import com.asledgehammer.langpack.core.test.TestResult
import org.bukkit.entity.Player

/**
 * **TestAction** tests a basic [BukkitActionText].
 *
 * @author Jab
 */
class TestAction(description: List<String>) : LangTest<BukkitLangPack, Player>("action", description) {
    override fun run(pack: BukkitLangPack, player: Player): TestResult {
        pack.message(player, "test.action.message")
        return TestResult(true)
    }
}
