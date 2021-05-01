@file:Suppress("unused")

package com.asledgehammer.langpack.bukkit.util.text

import com.asledgehammer.langpack.core.util.MultilinePrinter
import org.bukkit.ChatColor

/**
 * TODO: Document.
 *
 * @author Jab
 */
class TextComponent(var text: String? = "") {

    var extra: ArrayList<TextComponent>? = null
    var color: ChatColor = ChatColor.WHITE
    var clickEvent: ClickEvent? = null
    var hoverEvent: HoverEvent? = null

    /**
     * TODO: Document.
     *
     * @return
     */
    fun toLegacyText(): String {
        var text = "$color$text"
        if (extra != null && extra!!.isNotEmpty()) {
            for (next in extra!!) {
                text += next.toLegacyText()
            }
        }
        return text
    }

    fun addExtra(component: TextComponent) {
        if (extra == null) extra = ArrayList()
        extra!!.add(component)
    }

    fun print(): String = printer.print(this)

    companion object {
        private val printer = TextComponentPrinter()
    }

    private class TextComponentPrinter : MultilinePrinter<TextComponent>() {
        override fun onPrint(element: TextComponent) {
            val resetColor = ChatColor.RESET

            fun recurse(component: TextComponent) {
                line("TextComponent {")
                tab()

                val formattedColor = component.color.toString()
                line("text: \"$formattedColor${component.text}$resetColor\",")
                line("color: $formattedColor${component.color},")

                if (component.clickEvent != null) {
                    line("clickEvent: {")
                    tab()
                    line("value: ${component.clickEvent!!.value}")
                    unTab()
                    line("},")
                }
                if (component.hoverEvent != null) {
                    line("hoverEvent: {")
                    tab()
                    line("lines: [")
                    tab()
                    for (line in component.hoverEvent!!.contents) line("$line,")
                    unTab()
                    line("],")
                    unTab()
                    line("},")
                }
                if (component.extra != null) {
                    line("extra: {")
                    tab()
                    for (child in component.extra!!) recurse(child)
                    unTab()
                    line("}")
                }
                unTab()
                line("}")
            }

            recurse(element)
        }
    }
}
