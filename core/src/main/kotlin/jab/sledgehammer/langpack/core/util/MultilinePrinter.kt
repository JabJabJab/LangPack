package jab.sledgehammer.langpack.core.util

/**
 * **MultilinePrinter** TODO: Document.
 *
 * @param E The type of object to print.
 */
abstract class MultilinePrinter<E> {

    private var text = ""
    private var prefix = ""
    private var tab = "  "

    fun print(element: E, tab: String = "  "): String {
        this.tab = tab
        onPrint(element)
        val text = text
        reset()
        return text
    }

    protected fun raw(string: String) {
        text += string
    }

    protected fun line(string: String) {
        text += "$prefix$string\n"
    }

    protected fun tab() {
        prefix += tab
    }

    protected fun unTab() {
        val index = prefix.length - tab.length
        prefix = if (index < 1) "" else prefix.substring(0, index)
    }

    private fun reset() {
        text = ""
        prefix = ""
        tab = "  "
    }

    protected abstract fun onPrint(element: E)
}
