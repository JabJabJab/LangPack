package jab.langpack.spigot.test

/**
 * The **TestResult** struct stores the results for [LangTest]
 *
 * @author Jab
 *
 * @property success Set to true if the test succeeded.
 * @property reason (Optional) The reason the test failed.
 */
class TestResult(val success: Boolean, val reason: String? = null)
