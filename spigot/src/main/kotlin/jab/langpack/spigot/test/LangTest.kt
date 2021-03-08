package jab.langpack.spigot.test

import jab.langpack.spigot.SpigotLangPack
import org.bukkit.entity.Player

/**
 * The **LangTest** class TODO: Document.
 *
 * @author Jab
 *
 * @property name The name of the test.
 */
abstract class LangTest(val name: String) {

    /**
     * Executes the test procedure.
     *
     * @param pkg The lang pack instance to test.
     * @param player The player running the test.
     *
     * @return Returns true if the test succeeds. Returns false if the test fails.
     */
    fun test(pkg: SpigotLangPack, player: Player): TestResult {

        try {

            val result = run(pkg, player)
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
     * @param pkg The lang pack instance to test.
     * @param player The player running the test.
     */
    protected abstract fun run(pkg: SpigotLangPack, player: Player): TestResult
}
