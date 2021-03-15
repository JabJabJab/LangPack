package jab.langpack.test

import jab.langpack.core.LangPack

/**
 * The **LangTest** class serves as a in-game testing implementation for the lang-pack plugin in the Spigot environment.
 *
 * @author Jab
 *
 * @property name The name of the test.
 * @property description TODO: Document.
 */
abstract class LangTest<Commander>(val name: String, val description: List<String>) {

    /**
     * Executes the test procedure.
     *
     * @param pack The lang-pack instance to test.
     * @param player The player running the test.
     *
     * @return Returns true if the test succeeds. Returns false if the test fails.
     */
    fun test(pack: LangPack, player: Commander): TestResult {

        try {

            val time = System.currentTimeMillis()
            val result = run(pack, player)
            result.time = System.currentTimeMillis() - time
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
    protected abstract fun run(pack: LangPack, player: Commander): TestResult
}
