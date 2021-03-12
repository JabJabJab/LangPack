package jab.langpack.core.util

import jab.langpack.core.processor.FieldFormatter
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text

/**
 * The **ChatUtil** class houses all utilities for [TextComponent] for the lang-pack plugin.
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
object ChatUtil {

    /**
     * Slices a TextComponent into extras with each component being split fields and text.
     *
     * @param textComponent The component to split.
     * @param formatter The formatter to identify fields.
     *
     * @return Returns a component with all text & fields sequenced in [BaseComponent.extra].
     */
    fun slice(textComponent: TextComponent, formatter: FieldFormatter): TextComponent {

        val composition = TextComponent()
        val text = textComponent.text ?: return TextComponent(textComponent.text)

        // Make sure that we have fields to sort, otherwise return the color-formatted text.
        val stringFields = formatter.getFields(text)
        if (stringFields.isEmpty()) {
            return TextComponent(StringUtil.color(text))
        }

        var next = text
        for (stringField in stringFields) {
            val fField = "%$stringField%"
            val offset = next.indexOf(fField, 0, true)
            composition.addExtra(TextComponent(next.substring(0, offset)))
            composition.addExtra(TextComponent(fField))
            next = next.substring(offset + fField.length)
        }
        if (next.isNotEmpty()) {
            composition.addExtra(TextComponent(next))
        }

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
        component.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
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

        var array = emptyList<Text>()
        for (line in lines) {
            array = array.plus(Text(line))
        }

        component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, array)
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

        var array = emptyList<Text>()
        for (line in lines) {
            array = array.plus(Text(line))
        }

        component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, array)
        return component
    }

    /**
     * Spreads color to nested text components, much like how color codes work in legacy text.
     *
     * @param textComponent The component to color.
     */
    fun spreadColor(textComponent: TextComponent) {

        val list = flatten(textComponent)

        var last: BaseComponent = list[0]
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
    fun flatten(component: BaseComponent): ArrayList<BaseComponent> {

        val list = ArrayList<BaseComponent>()

        fun recurse(next: BaseComponent) {
            list.add(next)
            if (next.extra != null && next.extra.isNotEmpty()) {
                for (n in next.extra) {
                    recurse(n)
                }
            }
        }

        recurse(component)

        return list
    }

    /**
     * @param component The component to parse.
     *
     * @return Returns the last color-code in the base component. If one isn't in the text, the
     * [BaseComponent.color] is used.
     */
    fun getLastColor(component: BaseComponent): ChatColor {

        if (component is TextComponent) {
            if (component.text != null && component.text.isNotEmpty()) {
                val chars = component.text.toCharArray()

                var index = chars.lastIndex - 1
                while (index > -1) {

                    val next = chars[index]
                    if (next == ChatColor.COLOR_CHAR) {
                        return ChatColor.getByChar(chars[index + 1])
                    }

                    index--
                }
            }
        }

        return component.color
    }

    /**
     * Displays information on a base component using spacing and lines to read easily in a console.
     *
     * @param component The component to prettify.
     * @param startingPrefix The indention to use for all lines.
     *
     * @return Returns lines of text to display properties of the component.
     */
    fun pretty(component: BaseComponent, startingPrefix: String): ArrayList<String> {

        val lines = ArrayList<String>()
        var prefix = startingPrefix

        fun tabIn() {
            prefix += "  "
        }

        fun tabOut() {
            prefix = prefix.substring(0, prefix.length - 2)
        }

        fun line(string: String) {
            lines.add("$prefix$string")
        }

        fun recurse(component: BaseComponent) {
            line("${component.javaClass.simpleName} {")
            tabIn()
            if (component is TextComponent) {
                with(component) {
                    if (text != null && text.isNotEmpty()) {
                        line("""text: "$text${ChatColor.RESET}"""")
                    }
                    line("color: ${color.name}${ChatColor.RESET}")
                    if (clickEvent != null) line("clickEvent: $clickEvent")
                    if (hoverEvent != null) line("hoverEvent: $hoverEvent")
                    if (extra != null) {
                        line("extras: (size: ${extra.size})")
                        tabIn()
                        for (extra in extra) {
                            recurse(extra)
                        }
                        tabOut()
                    }
                }
            }

            tabOut()
            line("}")
        }

        recurse(component)
        return lines
    }
}
