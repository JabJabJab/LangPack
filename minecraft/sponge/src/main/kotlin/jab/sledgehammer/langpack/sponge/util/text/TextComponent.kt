@file:Suppress("unused")

package jab.sledgehammer.langpack.sponge.util.text

import jab.sledgehammer.langpack.sponge.util.ColorUtil
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyle
import org.spongepowered.api.text.format.TextStyles
import jab.sledgehammer.langpack.sponge.objects.complex.ActionText

/**
 * **TextComponent** is a "dummy-wrapper" solution for maintaining a consistency with solutions for
 * cross-server-platform support for [ActionText] objects.
 *
 * @author Jab
 */
class TextComponent(var text: String? = "") {

    var extra: ArrayList<TextComponent>? = null
    val styles = mutableListOf<TextStyle>(TextStyles.RESET)
    var color: TextColor = TextColors.WHITE
    var clickEvent: ClickEvent? = null
    var hoverEvent: HoverEvent? = null

    /**
     * Converts the dummy MD5 TextComponent format to Sponge's Text format.
     */
    fun toText(): Text {
        val builder = Text.builder(text ?: "")
        builder.color(color)
        if (clickEvent != null) builder.onClick(clickEvent!!.toAction())
        if (hoverEvent != null) builder.onHover(hoverEvent!!.toAction())
        for (style in styles) builder.style(style)
        if (extra != null && extra!!.isNotEmpty()) {
            for (child in extra!!) {
                builder.append(child.toText())
            }
        }
        return builder.build()
    }

    fun addExtra(component: TextComponent) {
        if (extra == null) extra = ArrayList()
        extra!!.add(component)
    }

    /**
     * Debug utility function for building texts for Sponge.
     */
    @JvmOverloads
    internal fun toPretty(prefix: String = "", tab: String = "  "): String {
        var prefixActual = prefix
        var text = ""

        fun line(line: String) {
            text += "$prefixActual$line\n"
        }

        fun tab() {
            prefixActual += tab
        }

        fun unTab() {
            prefixActual = prefixActual.substring(0, prefixActual.length - tab.length)
        }

        val resetColor = ColorUtil.toString(TextColors.WHITE)

        fun recurse(component: TextComponent) {
            line("TextComponent {")
            tab()

            val formattedColor = ColorUtil.toString(component.color)
            line("text: \"$formattedColor${component.text}$resetColor\",")
            line("color: $formattedColor${component.color},")

            if (component.styles.isNotEmpty()) {
                line("styles: [")
                tab()
                for (style in component.styles) {
                    line("$style,")
                }
                unTab()
                line("],")
            }
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

        recurse(this)

        return text
    }
}
