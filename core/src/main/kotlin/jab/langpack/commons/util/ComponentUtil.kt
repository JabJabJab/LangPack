package jab.langpack.commons.util

import jab.langpack.commons.processor.FieldFormatter
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent

/**
 * TODO: Document.
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class ComponentUtil {
    companion object {

        /**
         * TODO: Document.
         *
         * @param textComponent
         * @param formatter
         *
         * @return
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
         * @return TODO: Document.
         */
        fun createHoverComponent(text: String, lines: Array<String>): TextComponent {
            val component = TextComponent(text)

            var list: Array<TextComponent> = emptyArray()
            for (arg in lines) {
                list = list.plus(TextComponent(arg))
            }

            @Suppress("DEPRECATION")
            component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, list)
            return component
        }

        /**
         * Creates a component with a [HoverEvent] for displaying lines of text.
         *
         * @param text The text to display.
         * @param lines The lines of text to display when the text is hovered by a mouse.
         *
         * @return
         */
        @Suppress("DEPRECATION")
        fun createHoverComponent(text: String, lines: List<String>): TextComponent {
            val component = TextComponent(text)

            var list: Array<TextComponent> = emptyArray()
            for (arg in lines) {
                list = list.plus(TextComponent(arg))
            }

            component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, list)
            return component
        }

        /**
         * TODO: Document.
         *
         * @param textComponent
         */
        fun spreadColor(textComponent: TextComponent) {

            val list = toFlatList(textComponent)

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
         * TODO: Document.
         *
         * @param component
         */
        fun toFlatList(component: BaseComponent): ArrayList<BaseComponent> {

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
         * TODO: Document.
         *
         * @param component
         *
         * @return
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
         * TODO: Document.
         *
         * @param component
         * @param startingPrefix
         *
         * @return
         */
        fun toPretty(component: BaseComponent, startingPrefix: String): ArrayList<String> {

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
                    if (component.text != null && component.text.isNotEmpty()) {
                        line("""text: "${component.text}${ChatColor.RESET}"""")
                    }
                    line("color: ${component.color.name}${ChatColor.RESET}")
                    if (component.clickEvent != null) {
                        line("clickEvent: ${component.clickEvent}")
                    }
                    if (component.hoverEvent != null) {
                        line("hoverEvent: ${component.hoverEvent}")
                    }
                    if (component.extra != null) {
                        line("extras: (size: ${component.extra.size})")
                        tabIn()
                        for (extra in component.extra) {
                            recurse(extra)
                        }
                        tabOut()
                    }
                }
                // ..
                tabOut()
                line("}")
            }

            recurse(component)
            return lines
        }
    }
}
