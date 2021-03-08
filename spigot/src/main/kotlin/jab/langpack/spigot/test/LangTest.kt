package jab.langpack.spigot.test

import jab.langpack.spigot.SpigotLangPack
import org.bukkit.entity.Player

/**
 * The **LangTest** class serves as a in-game testing implementation for the lang-pack plugin in the Spigot environment.
 *
 * @author Jab
 *
 * @property name The name of the test.
 */
abstract class LangTest(val name: String) {

    /**
     * Executes the test procedure.
     *
     * @param pack The lang-pack instance to test.
     * @param player The player running the test.
     *
     * @return Returns true if the test succeeds. Returns false if the test fails.
     */
    fun test(pack: SpigotLangPack, player: Player): TestResult {

        try {

            val result = run(pack, player)
            if (!result.success) {
                return result
            }

            return result
        } catch (e: Exception) {

            val result = TestResult(false, e.message)
            e.printStackTrace(System.err)

            return result
        }
    }

    /**
     * @param pack The lang-pack instance to test.
     * @param player The player running the test.
     *
     * @return Returns the result of the test.
     */
    protected abstract fun run(pack: SpigotLangPack, player: Player): TestResult
}
