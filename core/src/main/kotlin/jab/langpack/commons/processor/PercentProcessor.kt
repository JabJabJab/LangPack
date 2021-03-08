package jab.langpack.commons.processor

import jab.langpack.commons.LangArg
import jab.langpack.commons.LangPack
import jab.langpack.commons.Language
import jab.langpack.commons.objects.LangComplex
import jab.langpack.commons.objects.LangComponent
import jab.langpack.commons.util.ComponentUtil
import jab.langpack.commons.util.StringUtil.Companion.color
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Content
import net.md_5.bungee.api.chat.hover.content.Text

/**
 * The ***PercentStringProcessor*** class implements the default field syntax for [LangPack].
 *
 *  > ### Field syntax: **%**field**%**
 *
 *  @author Jab
 */
class PercentProcessor : LangProcessor, FieldFormatter {

    override fun processComponent(
        component: TextComponent,
        pkg: LangPack,
        lang: Language,
        vararg args: LangArg,
    ): TextComponent {

        // There's no need to slice a component that is only a field.
        val composition = if (!isField(component.text)) {
            ComponentUtil.slice(component, this)
        } else {
            val comp = TextComponent(component.text)
            if (component.extra != null) {
                comp.extra = component.extra
            }
            comp
        }

        composition.hoverEvent = component.hoverEvent
        composition.clickEvent = component.clickEvent

        // Process fields as extras, removing the text after processing it.
        val eraseText = processText(composition, pkg, lang, *args)
        processHoverEvent(composition, pkg, lang, *args)
        processClickEvent(composition, pkg, lang, *args)
        processExtras(composition, pkg, lang, *args)
        if (eraseText) {
            composition.text = ""
        }

        ComponentUtil.spreadColor(composition)
        return composition
    }

    override fun processComponent(component: TextComponent, vararg args: LangArg): TextComponent {

        // There's no need to slice a component that is only a field.
        val composition = if (!isField(component.text)) {
            ComponentUtil.slice(component, this)
        } else {
            val comp = TextComponent(component.text)
            comp.hoverEvent = component.hoverEvent
            comp.clickEvent = component.clickEvent
            if (component.extra != null) {
                comp.extra = component.extra
            }
            comp
        }

        // Process fields as extras, removing the text after processing it.
        val eraseText = processText(composition, *args)
        processHoverEvent(composition, *args)
        processClickEvent(composition, *args)
        processExtras(composition, *args)
        if (eraseText) {
            composition.text = ""
        }

        ComponentUtil.spreadColor(composition)
        return composition
    }

    override fun processString(
        string: String,
        pkg: LangPack,
        lang: Language,
        vararg args: LangArg,
    ): String {

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
                val field = pkg.getString(stringField, lang, *args)
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

    override fun getFields(string: String): ArrayList<String> {

        val nextField = StringBuilder()
        val fields = ArrayList<String>()
        var insideField = false

        for (next in string.chars()) {
            val c = next.toChar()
            if (c == '%') {
                if (insideField) {
                    if (nextField.isNotEmpty()) {
                        fields.add(nextField.toString())
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

    override fun formatField(field: String): String {
        return "%${field.toLowerCase()}%"
    }

    override fun isField(string: String?): Boolean {
        return string != null && string.length > 2 && string.startsWith('%') && string.endsWith('%')
    }

    private fun processText(
        composition: TextComponent,
        pkg: LangPack,
        lang: Language,
        vararg args: LangArg,
    ): Boolean {
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
                field = pkg.resolve(fField, lang)
                if (field == null) {
                    field = pkg.resolve(fField, pkg.defaultLang)
                }
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

        return eraseText
    }

    private fun processText(composition: TextComponent, vararg args: LangArg): Boolean {
        var eraseText = false
        if (isField(composition.text)) {
            val fField = composition.text.replace("%", "")

            var field: Any? = null
            for (arg in args) {
                if (arg.key.equals(fField, true)) {
                    field = arg.value
                    break
                }
            }

            if (field != null) {
                when (field) {
                    is TextComponent -> {
                        composition.addExtra(processComponent(field, *args))
                        eraseText = true
                    }
                    is LangComponent -> {
                        composition.addExtra(processComponent(field.get(), *args))
                        eraseText = true
                    }
                    is LangComplex -> {
                        composition.addExtra(processComponent(TextComponent(field.get()), *args))
                        eraseText = true
                    }
                    else -> {
                        composition.text = processString(field.toString(), *args)
                    }
                }
            } else {
                composition.text = fField
            }
        } else {
            composition.text = color(composition.text)
        }

        return eraseText
    }

    private fun processHoverEvent(
        composition: BaseComponent,
        pkg: LangPack,
        lang: Language,
        vararg args: LangArg,
    ) {
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

    private fun processHoverEvent(composition: BaseComponent, vararg args: LangArg) {
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
                    newContents.add(Text(processString(content.value as String, *args)))
                } else {
                    newContents.add(content)
                }
            }
            composition.hoverEvent = HoverEvent(action, newContents)
        }
    }

    private fun processClickEvent(
        composition: BaseComponent,
        pkg: LangPack,
        lang: Language,
        vararg args: LangArg,
    ) {
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

    private fun processClickEvent(composition: BaseComponent, vararg args: LangArg) {
        if (composition.clickEvent == null) {
            return
        }

        val event = composition.clickEvent
        if (event.value != null) {
            val action = event.action
            val value = processString(event.value, *args)
            composition.clickEvent = ClickEvent(action, value)
        }
    }

    private fun processExtras(
        composition: TextComponent,
        pkg: LangPack,
        lang: Language,
        vararg args: LangArg,
    ) {
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

    private fun processExtras(composition: TextComponent, vararg args: LangArg) {
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
                newExtras.add(processComponent(next, *args))
            }
        }

        composition.extra.clear()
        composition.extra.addAll(newExtras)
    }
}
