package jab.sledgehammer.langpack.core.test

/**
 * **TestResult** stores the results for [LangTest]
 *
 * @author Jab
 *
 * @property success Set to true if the test succeeded.
 * @property reason (Optional) The reason the test failed.
 */
data class TestResult(val success: Boolean, val reason: String? = null) {

    /**
     * A time variable for displaying test times.
     */
    var time: Long = 0L
}
