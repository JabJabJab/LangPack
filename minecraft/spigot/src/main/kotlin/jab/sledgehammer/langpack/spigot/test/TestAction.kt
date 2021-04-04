package jab.sledgehammer.langpack.spigot.test

import jab.sledgehammer.langpack.spigot.SpigotLangPack
import jab.langpack.test.LangTest
import jab.langpack.test.TestResult
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
