package jab.langpack.spigot.test

import jab.langpack.spigot.SpigotLangPack
import jab.langpack.test.LangTest
import jab.langpack.test.TestResult
import org.bukkit.entity.Player

class TestBroadcast(description: List<String>) : LangTest<SpigotLangPack, Player>("broadcast", description) {

    override fun run(pack: SpigotLangPack, player: Player): TestResult {
        pack.broadcast("%test.broadcast.message%")
        return TestResult(true)
    }
}