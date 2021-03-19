package jab.langpack.spigot.test

import jab.langpack.core.objects.complex.ActionText
import jab.langpack.spigot.SpigotLangPack
import jab.langpack.test.LangTest
import jab.langpack.test.TestResult
import org.bukkit.entity.Player

/**
 * The **TestAction** class Tests the basic operations for [ActionText].
 *
 * @author Jab
 */
class TestAction(description: List<String>) : LangTest<SpigotLangPack, Player>("action", description) {

    override fun run(pack: SpigotLangPack, player: Player): TestResult {
        pack.message(player, "test.action.message")
        return TestResult(true)
    }
}
