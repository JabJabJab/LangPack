package jab.sledgehammer.langpack.spigot.test

import jab.sledgehammer.langpack.spigot.SpigotLangPack
import jab.langpack.test.LangTest
import jab.langpack.test.TestResult
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
