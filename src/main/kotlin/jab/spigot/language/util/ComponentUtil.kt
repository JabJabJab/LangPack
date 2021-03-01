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
            for (index in 1..list.lastIndex) {
                val next = list[index]
                next.color = getLastColor(last)
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
            list.add(component)
            if (component.extra != null && component.extra.isNotEmpty()) {
                for (n in component.extra) {
                    toFlatList(n)
                }
            }
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
            var color = component.color

            if (component is TextComponent) {
                if (component.text != null && component.text.isNotEmpty()) {
                    val chars = component.text.toCharArray()
                    if (chars.size >= 2) {
                        for (index in chars.lastIndex - 1..0) {
                            val next = chars[index]
                            if (next == ChatColor.COLOR_CHAR) {
                                color = ChatColor.getByChar(chars[index + 1])
                                break
                            }
                        }
                    }
                }
            }
            return color
        }
    }
}