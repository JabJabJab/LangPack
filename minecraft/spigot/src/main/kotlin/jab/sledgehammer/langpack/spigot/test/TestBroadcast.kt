package jab.sledgehammer.langpack.spigot.test

import jab.sledgehammer.langpack.core.test.LangTest
import jab.sledgehammer.langpack.core.test.TestResult
import jab.sledgehammer.langpack.spigot.SpigotLangPack
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
