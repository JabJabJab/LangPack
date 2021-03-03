package jab.spigot.language.test

import jab.spigot.language.LangPackage
import org.bukkit.entity.Player

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @property name The name of the test.
 */
abstract class LangTest(val name: String) {

    /**
     * Executes the test procedure.
     *
     * @param pkg The LangPackage instance to test.
     * @param player The player running the test.
     *
     * @return Returns true if the test succeeds. Returns false if the test fails.
     */
    fun test(pkg: LangPackage, player: Player): TestResult {

        try {
            println("""Running test: "$name".. """)
            val result = run(pkg, player)
            if(!result.success) {
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
     * @param pkg The langPackage instance to test.
     * @param player The player running the test.
     */
    protected abstract fun run(pkg: LangPackage, player: Player): TestResult
}