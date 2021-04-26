@file:Suppress("MemberVisibilityCanBePrivate")

package jab.sledgehammer.langpack.core.test

import jab.sledgehammer.langpack.core.LangPack

/**
 * **LangTest** is a runtime testing utility for [LangPack].
 *
 * @author Jab
 *
 * @property name The name of the test.
 * @property description A brief description of what the test does.
 *
 * @param Pack The pack type.
 * @param Commander The object of the person orchestrating the test.
 */
abstract class LangTest<Pack : LangPack, Commander>(val name: String, val description: List<String>) {

    /**
     * Executes the test procedure.
     *
     * @param pack The lang-pack instance to test.
     * @param commander The commander running the test.
     *
     * @return Returns the results of the test.
     */
    fun test(pack: Pack, commander: Commander): TestResult {
        val time = System.currentTimeMillis()
        return try {
            val result = run(pack, commander)
            result.time = System.currentTimeMillis() - time
            result
        } catch (e: Exception) {
            e.printStackTrace(System.err)
            val result = TestResult(false, e.message)
            result.time = System.currentTimeMillis() - time
            result
        }
    }

    /**
     * @param pack The lang-pack instance to test.
     * @param player The player running the test.
     *
     * @return Returns the result of the test.
     */
    protected abstract fun run(pack: Pack, player: Commander): TestResult
}
