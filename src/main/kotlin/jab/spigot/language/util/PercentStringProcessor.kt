package jab.spigot.language.util

import jab.spigot.language.LangArg
import jab.spigot.language.LangPackage
import jab.spigot.language.LangPackage.Companion.color
import jab.spigot.language.Language
import jab.spigot.language.`object`.LangComplex
import jab.spigot.language.`object`.LangComponent
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Content
import net.md_5.bungee.api.chat.hover.content.Text

/**
 * The <i>PercentStringProcessor</i> class implements the default field syntax for [LangPackage].
 *
 *  Field syntax: '%field%'
 *
 *  @author Jab
 */
class PercentStringProcessor : StringProcessor {

    private fun slice(textComponent: TextComponent): TextComponent {
        val composition = TextComponent()

        val text = textComponent.text ?: return TextComponent(textComponent.text)

        // Make sure that we have fields to sort, otherwise return the color-formatted text.
        val stringFields = getFields(text)
        if (stringFields.isEmpty()) {
            return TextComponent(color(text))
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

    override fun processComponent(
        textComponent: TextComponent,
        pkg: LangPackage,
        lang: Language,
        vararg args: LangArg
    ): TextComponent {

        val composition = if (!isField(textComponent.text)) {
            slice(textComponent)
        } else {
            val comp = TextComponent(textComponent.text)
            comp.hoverEvent = textComponent.hoverEvent
            comp.clickEvent = textComponent.clickEvent
            if (textComponent.extra != null) {
                comp.extra = textComponent.extra
            }
            comp
        }

        fun processHoverEvent() {
            if (composition.hoverEvent == null) {
                return
            }

            val event = composition.hoverEvent
            if (event.contents != null) {
                val action = event.action
                val contents = event.contents!!
                val newContents = ArrayList<Content>()
                for (content in contents) {
                    if (content is Text) {
                        newContents.add(Text(processString(content.value as String, pkg, lang, *args)))
                    } else {
                        newContents.add(content)
                    }
                }
                composition.hoverEvent = HoverEvent(action, newContents)
            }
        }

        fun processClickEvent() {
            if (composition.clickEvent == null) {
                return
            }

            val event = composition.clickEvent
            if (event.value != null) {
                val action = event.action
                val value = processString(event.value, pkg, lang, *args)
                composition.clickEvent = ClickEvent(action, value)
            }
        }

        fun processExtras() {
            if (composition.extra == null || isField(composition.text)) {
                return
            }

            val newExtras = ArrayList<BaseComponent>()
            for (next in composition.extra) {
                if (next == null) {
                    continue
                } else if (next !is TextComponent) {
                    newExtras.add(next)
                    continue
                } else {
                    newExtras.add(processComponent(next, pkg, lang, *args))
                }
            }

            composition.extra.clear()
            composition.extra.addAll(newExtras)
        }

        var eraseText = false

        if (isField(composition.text)) {
            val fField = composition.text.replace("%", "")

            var found = false
            var field: Any? = null
            for (arg in args) {
                if (arg.key.equals(fField, true)) {
                    field = arg.value
                    found = true
                    break
                }
            }
            if (!found) {
                field = pkg.getRaw(fField, lang)
            }

            if (field != null) {
                when (field) {
                    is TextComponent -> {
                        composition.addExtra(processComponent(field, pkg, lang, *args))
                        eraseText = true
                    }
                    is LangComponent -> {
                        composition.addExtra(processComponent(field.get(), pkg, lang, *args))
                        eraseText = true
                    }
                    is LangComplex -> {
                        composition.addExtra(processComponent(TextComponent(field.get()), pkg, lang, *args))
                        eraseText = true
                    }
                    else -> {
                        composition.text = processString(field.toString(), pkg, lang, *args)
                    }
                }
            } else {
                composition.text = fField
            }
        } else {
            composition.text = color(composition.text)
        }

        processHoverEvent()
        processClickEvent()
        processExtras()
        if (eraseText) {
            composition.text = ""
        }

        spreadColor(composition)
        return composition
    }

    override fun processString(string: String, pkg: LangPackage, lang: Language, vararg args: LangArg): String {

        val stringFields = getFields(string)
        if (stringFields.isEmpty()) {
            return color(string)
        }

        var processedString = string

        // Process all fields in the string.
        for (stringField in stringFields) {

            val fField = formatField(stringField)
            var found = false

            // Check the passed fields for the defined field.
            for (field in args) {
                if (field.key.equals(stringField, true)) {
                    found = true
                    val value = field.value.toString()
                    processedString = processedString.replace(fField, value, true)
                }
                break
            }

            // Check LanguagePackage for the defined field.
            if (!found) {
                val field = pkg.getString(stringField)
                if (field != null) {
                    processedString = processedString.replace(fField, field, true)
                }
            }
        }

        // Remove all field characters.
        for (stringField in stringFields) {
            processedString = processedString.replace(stringField, stringField.replace("%", ""))
        }

        return color(processedString)
    }

    override fun processString(string: String, vararg args: LangArg): String {

        val stringFields = getFields(string)
        if (stringFields.isEmpty()) {
            return color(string)
        }

        var processedString = string

        // Process all fields in the string.
        for (stringField in stringFields) {
            for (field in args) {
                if (field.key.equals(stringField, true)) {
                    val fField = formatField(stringField)
                    val value = field.value.toString()
                    processedString = processedString.replace(fField, value, true)
                }
                break
            }
        }

        // Remove all field characters.
        for (stringField in stringFields) {
            processedString = processedString.replace(stringField, stringField.replace("%", ""))
        }

        return color(processedString)
    }

    override fun getFields(string: String): Array<String> {

        val nextField = StringBuilder()
        var fields: Array<String> = emptyArray()
        var insideField = false

        for (next in string.chars()) {
            val c = next.toChar()
            if (c == '%') {
                if (insideField) {
                    if (nextField.isNotEmpty()) {
                        fields = fields.plus(nextField.toString())
                    }
                    insideField = false
                } else {
                    insideField = true
                    nextField.clear()
                }
            } else {
                if (insideField) {
                    nextField.append(c)
                }
            }
        }

        return fields
    }

    companion object {

        private fun spreadColor(textComponent: TextComponent) {
            val list = ArrayList<BaseComponent>()

            fun recurse(next: BaseComponent) {
                list.add(next)
                if (next.extra != null && next.extra.isNotEmpty()) {
                    for (n in next.extra) {
                        recurse(n)
                    }
                }
            }

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

            recurse(textComponent)

            var last: BaseComponent = list[0]
            for (index in 1..list.lastIndex) {
                val next = list[index]
                next.color = getLastColor(last)
                last = next
            }
        }

        /**
         * @param field the Field to process.
         *
         * @return Returns a field in the syntax format.
         */
        private fun formatField(field: String): String {
            return "%${field.toLowerCase()}%"
        }

        private fun isField(string: String?): Boolean {
            return string != null && string.length > 2 && string.startsWith('%') && string.endsWith('%')
        }
    }
}