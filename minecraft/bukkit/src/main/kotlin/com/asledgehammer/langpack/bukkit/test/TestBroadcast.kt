package com.asledgehammer.langpack.bukkit.test

import com.asledgehammer.langpack.bukkit.BukkitLangPack
import com.asledgehammer.langpack.core.test.LangTest
import com.asledgehammer.langpack.core.test.TestResult
import org.bukkit.entity.Player

/**
 * **TestBroadcast** tests a basic broadcast call to [BukkitLangPack].
 *
 * @author Jab
 */
class TestBroadcast(description: List<String>) : LangTest<BukkitLangPack, Player>("broadcast", description) {
    override fun run(pack: BukkitLangPack, player: Player): TestResult {
        pack.broadcast("test.broadcast.message")
        return TestResult(true)
    }
}
