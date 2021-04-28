package com.asledgehammer.langpack.spigot.test

import com.asledgehammer.langpack.core.test.LangTest
import com.asledgehammer.langpack.core.test.TestResult
import com.asledgehammer.langpack.spigot.SpigotLangPack
import org.bukkit.entity.Player

/**
 * **TestBroadcast** tests a basic broadcast call to [SpigotLangPack].
 *
 * @author Jab
 */
class TestBroadcast(description: List<String>) : LangTest<SpigotLangPack, Player>("broadcast", description) {
    override fun run(pack: SpigotLangPack, player: Player): TestResult {
        pack.broadcast("test.broadcast.message")
        return TestResult(true)
    }
}
