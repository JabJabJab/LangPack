package jab.spigot.language.test

import jab.spigot.language.LangPackage

/**
 * The <i>jab.spigot.language.test.LangTest</i> class handles runtime tests for the LangPackage plugin.
 *
 * @author Jab
 *
 * @property name The name of the test.
 */
abstract class LangTest(private val name: String) {

    /**
     * Executes the test procedure.
     *
     * @param pkg The LangPackage instance to test.
     *
     * @return Returns true if the test succeeds. Returns false if the test fails.
     */
    fun test(pkg: LangPackage): Boolean {

        fun fail(reason: String) {
            System.err.println("""Failed to run test: "$name". Reason: "$reason".""")
        }

        try {
            println("""Running test: "$name".. """)
            if (!runTest(pkg)) {
                fail("Test Failure.")
                return false
            }
            println("""Test "$name" successful.""")
            return true
        } catch (e: Exception) {
            fail("Exception occurred.")
            e.printStackTrace(System.err)
        }

        return false
    }

    /**
     * @param pkg The langPackage instance to test.
     */
    protected abstract fun run(pkg: LangPackage): Boolean
}