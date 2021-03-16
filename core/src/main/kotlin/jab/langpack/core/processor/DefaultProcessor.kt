package jab.langpack.core.processor

import jab.langpack.core.objects.LangArg
import jab.langpack.core.LangPack
import jab.langpack.core.Language
import jab.langpack.core.objects.complex.Complex
import jab.langpack.core.objects.definition.Definition
import jab.langpack.core.objects.definition.StringDefinition
import jab.langpack.core.util.ChatUtil
import jab.langpack.core.util.StringUtil
import jab.langpack.core.util.StringUtil.color
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Content
import net.md_5.bungee.api.chat.hover.content.Text

/**
 * TODO: Update documentation to reflect Definition API update.
 *
 * The ***DefaultProcessor*** class implements the default field syntax for lang-packs.
 *
 *  @author Jab
 */
class DefaultProcessor(private val formatter: FieldFormatter) : Processor {

    override fun process(
        component: TextComponent, pack: LangPack, lang: Language, vararg args: LangArg,
    ): TextComponent {

        // There's no need to slice a component that is only a field.
        val composition = if (!formatter.isField(component.text)) {
            ChatUtil.slice(component, pack.formatter)
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
        val eraseText = processText(composition, pack, lang, *args)
        processHoverEvent(composition, pack, lang, *args)
        processClickEvent(composition, pack, lang, *args)
        processExtras(composition, pack, lang, *args)
        if (eraseText) composition.text = ""

        ChatUtil.spreadColor(composition)
        return composition
    }

    override fun process(component: TextComponent, vararg args: LangArg): TextComponent {

        // There's no need to slice a component that is only a field.
        val composition = if (!formatter.isField(component.text)) {
            ChatUtil.slice(component, formatter)
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
        val eraseText = processText(composition, *args)
        processHoverEvent(composition, *args)
        processClickEvent(composition, *args)
        processExtras(composition, *args)
        if (eraseText) composition.text = ""

        ChatUtil.spreadColor(composition)
        return composition
    }

    override fun process(string: String, pack: LangPack, lang: Language, vararg args: LangArg): String {

        val stringFields = formatter.getFields(string)
        if (stringFields.isEmpty()) return color(string)

        var processedString = string

        // Process all fields in the string.
        for (stringField in stringFields) {

            val fField = formatter.format(stringField)
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

            // Check lang-pack for the defined field.
            if (!found) {
                val field = pack.getString(stringField, lang, *args)
                if (field != null) {
                    processedString = processedString.replace(fField, field, true)
                }
            }
        }

        // Remove all field characters.
        for (stringField in stringFields) {
            processedString = processedString.replace(stringField, formatter.strip(stringField))
        }

        return color(processedString)
    }

    override fun process(string: String, vararg args: LangArg): String {

        val stringFields = formatter.getFields(string)
        if (stringFields.isEmpty()) {
            return color(string)
        }

        var processedString = string

        // Process all fields in the string.
        for (stringField in stringFields) {
            for (field in args) {
                if (field.key.equals(stringField, true)) {
                    val fField = formatter.format(stringField)
                    val value = field.value.toString()
                    processedString = processedString.replace(fField, value, true)
                }
                break
            }
        }

        // Remove all field characters.
        for (stringField in stringFields) {
            processedString = processedString.replace(stringField, formatter.strip(stringField))
        }

        return color(processedString)
    }

    private fun processText(component: TextComponent, pack: LangPack, lang: Language, vararg args: LangArg): Boolean {
        with(component) {
            var eraseText = false
            if (formatter.isField(text)) {
                val fField = formatter.strip(text)

                var found = false
                var field: Definition<*>? = null
                for (arg in args) {
                    if (arg.key.equals(fField, true)) {
                        field = StringDefinition(pack, StringUtil.toAString(arg.value))
                        found = true
                        break
                    }
                }
                if (!found) {
                    field = pack.resolve(lang, fField)
                    if (field == null) {
                        field = pack.resolve(pack.defaultLang, fField)
                    }
                }

                if (field != null) {
                    when (field.value) {
                        is Complex<*> -> {
                            val result = (field.value as Complex<*>).get()
                            val processedComponent: TextComponent = if (result is TextComponent) {
                                result
                            } else {
                                TextComponent(result.toString())
                            }
                            addExtra(process(processedComponent, *args))
                            eraseText = true
                        }
                        else -> {
                            text = process(field.value.toString(), pack, lang, *args)
                        }
                    }
                } else {
                    text = fField
                }
            } else {
                text = color(text)
            }

            return eraseText
        }
    }

    private fun processText(composition: TextComponent, vararg args: LangArg): Boolean {
        with(composition) {
            var eraseText = false
            if (formatter.isField(text)) {
                val fField = formatter.strip(text)

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
                            addExtra(process(field, *args))
                            eraseText = true
                        }
                        is Complex<*> -> {
                            val result = field.get()
                            val processedComponent: TextComponent = if (result is TextComponent) {
                                result
                            } else {
                                TextComponent(result.toString())
                            }
                            addExtra(process(processedComponent, *args))
                            eraseText = true
                        }
                        else -> {
                            text = process(field.toString(), *args)
                        }
                    }
                } else {
                    text = fField
                }
            } else {
                text = color(text)
            }
            return eraseText
        }
    }

    private fun processHoverEvent(component: BaseComponent, pack: LangPack, lang: Language, vararg args: LangArg) {
        with(component) {
            if (hoverEvent == null) return
            if (hoverEvent.contents != null) {
                val action = hoverEvent.action
                val contents = hoverEvent.contents!!
                val newContents = ArrayList<Content>()
                for (content in contents) {
                    if (content is Text) {
                        newContents.add(Text(process(content.value as String, pack, lang, *args)))
                    } else {
                        newContents.add(content)
                    }
                }
                hoverEvent = HoverEvent(action, newContents)
            }
        }
    }

    private fun processHoverEvent(component: BaseComponent, vararg args: LangArg) {
        with(component) {
            if (hoverEvent == null) return
            if (hoverEvent.contents != null) {
                val action = hoverEvent.action
                val contents = hoverEvent.contents!!
                val newContents = ArrayList<Content>()
                for (content in contents) {
                    if (content is Text) {
                        newContents.add(Text(process(content.value as String, *args)))
                    } else {
                        newContents.add(content)
                    }
                }
                hoverEvent = HoverEvent(action, newContents)
            }
        }
    }

    private fun processClickEvent(component: BaseComponent, pack: LangPack, lang: Language, vararg args: LangArg) {
        with(component) {
            if (clickEvent == null) return
            if (clickEvent.value != null) {
                val action = clickEvent.action
                val value = process(clickEvent.value, pack, lang, *args)
                clickEvent = ClickEvent(action, value)
            }
        }
    }

    private fun processClickEvent(composition: BaseComponent, vararg args: LangArg) {
        with(composition) {
            if (clickEvent == null) return
            if (clickEvent.value != null) {
                val action = clickEvent.action
                val value = process(clickEvent.value, *args)
                clickEvent = ClickEvent(action, value)
            }
        }
    }

    private fun processExtras(component: TextComponent, pack: LangPack, lang: Language, vararg args: LangArg) {
        with(component) {
            if (extra == null || formatter.isField(text)) return
            val newExtras = ArrayList<BaseComponent>()
            for (next in extra) {
                if (next == null) {
                    continue
                } else if (next !is TextComponent) {
                    newExtras.add(next)
                    continue
                } else {
                    newExtras.add(process(next, pack, lang, *args))
                }
            }
            extra.clear()
            extra.addAll(newExtras)
        }
    }

    private fun processExtras(component: TextComponent, vararg args: LangArg) {
        with(component) {
            if (extra == null || formatter.isField(text)) return

            val newExtras = ArrayList<BaseComponent>()
            for (next in extra) {
                if (next == null) {
                    continue
                } else if (next !is TextComponent) {
                    newExtras.add(next)
                    continue
                } else {
                    newExtras.add(process(next, *args))
                }
            }
            extra.clear()
            extra.addAll(newExtras)
        }
    }
}
