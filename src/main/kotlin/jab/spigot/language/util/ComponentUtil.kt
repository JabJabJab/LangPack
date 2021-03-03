package jab.spigot.language.util

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent

/**
 * TODO: Document.
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate")
class ComponentUtil {
    companion object {

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

                        if (next == ChatColor.COLOR_CHAR.toChar()) {
                            return ChatColor.getByChar(chars[index + 1])
                        }

                        index--
                    }
                }
            }
            return component.color
        }

        fun toPretty(component: BaseComponent, startingPrefix: String): ArrayList<String> {

            val lines = ArrayList<String>()
            var prefix = startingPrefix

            fun tab() {
                prefix += "  "
            }

            fun untab() {
                prefix = prefix.substring(0, prefix.length - 2)
            }

            fun line(string: String) {
                lines.add("$prefix$string")
            }

            fun recurse(component: BaseComponent) {
                line("${component.javaClass.simpleName} {")
                tab()
                if (component is TextComponent) {
                    val text = if (component.text.isEmpty()) {
                        "(Empty)"
                    } else {
                        component.text
                    }
                    line("text: $text")
                    line("color: ${component.color}")
                    line("clickEvent: ${component.clickEvent}")
                    line("hoverEvent: ${component.hoverEvent}")
                    if (component.extra != null) {
                        line("extras: (size: ${component.extra.size})")
                        tab()
                        for (extra in component.extra) {
                            recurse(extra)
                        }
                        untab()
                    }
                }
                // ..
                untab()
                line("}")
            }

            recurse(component)
            return lines
        }

    }
}