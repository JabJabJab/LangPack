package jab.sledgehammer.langpack.bukkit.test

import jab.sledgehammer.langpack.bukkit.BukkitLangPack
import jab.sledgehammer.langpack.core.test.LangTest
import jab.sledgehammer.langpack.core.test.TestResult
import org.bukkit.entity.Player

/**
 * **TestAction** tests a basic [ActionText].
 *
 * @author Jab
 */
class TestAction(description: List<String>) : LangTest<BukkitLangPack, Player>("action", description) {
    override fun run(pack: BukkitLangPack, player: Player): TestResult {
        pack.message(player, "test.action.message")
        return TestResult(true)
    }
}
