package com.asledgehammer.langpack.sponge.util

import com.asledgehammer.langpack.core.LangPack
import com.asledgehammer.langpack.core.objects.formatter.FieldFormatter
import com.asledgehammer.langpack.core.util.MultilinePrinter
import com.asledgehammer.langpack.minecraft.commons.util.text.ClickEvent
import com.asledgehammer.langpack.minecraft.commons.util.text.HoverEvent
import com.asledgehammer.langpack.sponge.util.text.TextComponent
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles

/**
 * **ChatUtil** houses all utilities for [TextComponent] for the [LangPack].
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
object ChatUtil {

    private val printer = TextComponentPrinter()

    /**
     * Slices a TextComponent into extras with each component being split fields and text.
     *
     * @param textComponent The component to split.
     * @param formatter The formatter to identify fields.
     *
     * @return Returns a component with all text & fields sequenced in [TextComponent.extra].
     */
    fun slice(textComponent: TextComponent, formatter: FieldFormatter): TextComponent {
        val composition = TextComponent()
        val text = textComponent.text ?: return TextComponent(textComponent.text)
        if (textComponent.text!!.isEmpty()) return TextComponent("")

        // Make sure that we have fields to sort, otherwise return the color-formatted text.
        val fields = formatter.getFields(text)
        if (fields.isEmpty()) return TextComponent(color(text))

        var next = text
        for (field in fields) {
            val offset = next.indexOf(field.raw, 0, true)
            composition.addExtra(TextComponent(next.substring(0, offset)))
            composition.addExtra(TextComponent(field.raw))
            next = next.substring(offset + field.raw.length)
        }
        if (next.isNotEmpty()) composition.addExtra(TextComponent(next))

        composition.hoverEvent = textComponent.hoverEvent
        composition.clickEvent = textComponent.clickEvent

        return composition
    }

    /**
     * Creates a component with a [ClickEvent] for firing a command.
     *
     * @param text The text to display.
     * @param command The command to execute when clicked.
     *
     * @return Returns a text component with a click event for executing the command.
     */
    fun createCommandComponent(text: String, command: String): TextComponent {
        val component = TextComponent(text)
        component.clickEvent = ClickEvent(command)
        return component
    }

    /**
     * Creates a component with a [HoverEvent] for displaying lines of text.
     *
     * @param text The text to display.
     * @param lines The lines of text to display when the text is hovered by a mouse.
     *
     * @return Returns a text component with a hover event.
     */
    fun createHoverComponent(text: String, lines: Array<String>): TextComponent {
        val component = TextComponent(text)
        component.hoverEvent = HoverEvent(lines)
        return component
    }

    /**
     * Creates a component with a [HoverEvent] for displaying lines of text.
     *
     * @param text The text to display.
     * @param lines The lines of text to display when the text is hovered by a mouse.
     *
     * @return Returns a text component with a hover event.
     */
    fun createHoverComponent(text: String, lines: List<String>): TextComponent {
        val component = TextComponent(text)
        component.hoverEvent = HoverEvent(lines)
        return component
    }

    /**
     * Spreads color to nested text components, much like how color codes work in legacy text.
     *
     * @param textComponent The component to color.
     */
    fun spreadColor(textComponent: TextComponent) {
        val list = flatten(textComponent)
        var last: TextComponent = list[0]
        last.color = getLastColor(last)
        for (index in 1..list.lastIndex) {
            val next = list[index]
            val result = getLastColor(last)
            next.color = result
            last = next
        }
    }

    /**
     * Flattens a base component to a list of components.
     *
     * @param component The component to flatten.
     *
     * @return Returns a list of all sequenced base component extras as it would be displayed in chat.
     */
    fun flatten(component: TextComponent): ArrayList<TextComponent> {
        val list = ArrayList<TextComponent>()

        fun recurse(next: TextComponent) {
            list.add(next)
            if (next.extra != null && next.extra!!.isNotEmpty()) {
                for (n in next.extra!!) recurse(n)
            }
        }

        recurse(component)
        return list
    }

    /**
     * @param component The component to parse.
     *
     * @return Returns the last color-code in the base component. If one isn't in the text, the
     * [TextComponent.color] is used.
     */
    fun getLastColor(component: TextComponent): TextColor {
        if (component.text != null && component.text!!.isNotEmpty()) {
            val chars = component.text!!.toCharArray()
            var index = chars.lastIndex - 1
            while (index > -1) {
                val next = chars[index]
                if (next == ColorUtil.COLOR_CHAR) {
                    val color = ColorUtil.getByChar(chars[index + 1])
                    if (color != TextColors.NONE) return color
                }
                index--
            }
        }
        return component.color
    }

    /**
     * TODO: Document.
     *
     * @param string
     * @param char
     */
    fun color(string: String, char: Char = '&'): String = string.replace(char, '\u00a7')

    /**
     * Displays information on a base component using spacing and lines to read easily in a console.
     *
     * @param component The component to prettify.
     *
     * @return Returns lines of text to display properties of the component.
     */
    fun pretty(component: TextComponent): String = printer.print(component)

    private class TextComponentPrinter : MultilinePrinter<TextComponent>() {
        override fun onPrint(element: TextComponent) {
            fun recurse(component: TextComponent) {
                line("${component.javaClass.simpleName} {")
                tab()
                with(component) {
                    if (text != null && text!!.isNotEmpty()) {
                        line("""text: "$text${TextStyles.RESET}"""")
                    }
                    line("color: ${color.name}${TextStyles.RESET}")
                    if (clickEvent != null) line("clickEvent: $clickEvent")
                    if (hoverEvent != null) line("hoverEvent: $hoverEvent")
                    if (extra != null) {
                        line("extras: (size: ${extra!!.size})")
                        tab()
                        for (extra in extra!!) recurse(extra)
                        unTab()
                    }
                }
                unTab()
                line("}")
            }
            recurse(element)
        }
    }
}
