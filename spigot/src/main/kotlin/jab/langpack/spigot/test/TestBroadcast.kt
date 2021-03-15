package jab.langpack.spigot.test

import jab.langpack.core.LangPack
import jab.langpack.spigot.broadcast
import jab.langpack.test.LangTest
import jab.langpack.test.TestResult
import org.bukkit.entity.Player

class TestBroadcast(description: List<String>): LangTest<Player>("broadcast", description) {

    override fun run(pack: LangPack, player: Player): TestResult {
        pack.broadcast("%test.broadcast.message%")
        return TestResult(true)
    }
}