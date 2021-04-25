package jab.sledgehammer.langpack.bukkit.test

import jab.sledgehammer.langpack.bukkit.BukkitLangPack
import jab.sledgehammer.langpack.core.test.LangTest
import jab.sledgehammer.langpack.core.test.TestResult
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
