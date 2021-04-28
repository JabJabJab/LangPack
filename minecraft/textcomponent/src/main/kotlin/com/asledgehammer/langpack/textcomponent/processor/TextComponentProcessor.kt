package com.asledgehammer.langpack.textcomponent.processor

import com.asledgehammer.langpack.core.LangPack
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.LangGroup
import com.asledgehammer.langpack.core.objects.complex.Complex
import com.asledgehammer.langpack.core.objects.definition.LangDefinition
import com.asledgehammer.langpack.core.objects.definition.StringDefinition
import com.asledgehammer.langpack.core.objects.formatter.FieldFormatter
import com.asledgehammer.langpack.core.processor.DefaultProcessor
import com.asledgehammer.langpack.core.util.StringUtil
import com.asledgehammer.langpack.textcomponent.util.ChatUtil
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Content
import net.md_5.bungee.api.chat.hover.content.Text

/**
 * **DefaultProcessor** implements the default field syntax for lang-packs.
 *
 *  @author Jab
 */
class TextComponentProcessor(formatter: FieldFormatter) : DefaultProcessor(formatter) {

    override fun postProcess(string: String): String = ChatColor.translateAlternateColorCodes('&', string)

    /**
     * Processes a text component, inserting arguments and fields set in the lang-pack.
     *
     * @param component The text component to process.
     * @param pack The package instance.
     * @param lang The language context.
     * @param args (Optional) The arguments to process into the string.
     *
     * @return Returns the processed string.
     */
    fun process(
        component: TextComponent, pack: LangPack, lang: Language, context: LangGroup?, vararg args: LangArg,
    ): TextComponent {
        // There's no need to slice a component that is only a field.
        val composition = if (!formatter.isField(component.text)) {
            ChatUtil.slice(component, pack.formatter)
        } else {
            val comp = TextComponent(component.text)
            if (component.extra != null) comp.extra = component.extra
            comp
        }

        composition.hoverEvent = component.hoverEvent
        composition.clickEvent = component.clickEvent

        // Process fields as extras, removing the text after processing it.
        val eraseText = processText(composition, pack, lang, context, *args)
        processHoverEvent(composition, pack, lang, context, *args)
        processClickEvent(composition, pack, lang, context, *args)
        processExtras(composition, pack, lang, context, *args)
        if (eraseText) composition.text = ""

        ChatUtil.spreadColor(composition)
        return composition
    }

    /**
     * Processes a text component, inserting provided arguments.
     *
     * @param component The component to process.
     * @param args (Optional) The arguments to process into the string.
     *
     * @return Returns the processed component.
     */
    fun process(component: TextComponent, vararg args: LangArg): TextComponent {
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

    private fun processText(
        component: TextComponent,
        pack: LangPack,
        lang: Language,
        context: LangGroup?,
        vararg args: LangArg,
    ): Boolean {
        with(component) {
            var eraseText = false
            if (formatter.isField(text)) {
                val fieldStripped = formatter.strip(text)
                var found = false
                var field: LangDefinition<*>? = null
                for (arg in args) {
                    if (arg.key.equals(fieldStripped, true)) {
                        field = StringDefinition(pack, context, StringUtil.toAString(arg.value))
                        found = true
                        break
                    }
                }
                if (!found) {
                    field = pack.resolve(fieldStripped, lang, context)
                        ?: pack.resolve(fieldStripped, pack.defaultLang, context)
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

                            // If the reference is forced to global scope, do not provide context.
                            val parent = if (formatter.isPackageScope(text)) {
                                null
                            } else {
                                field.parent
                            }

                            addExtra(process(processedComponent, pack, lang, parent, *args))
                            eraseText = true
                        }
                        else -> {
                            text = process(field.value.toString(), pack, lang, field.parent, *args)
                        }
                    }
                } else {
                    text = postProcess(formatter.getPlaceholder(text))
                }
            } else {
                text = postProcess(text)
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
                text = postProcess(text)
            }
            return eraseText
        }
    }

    private fun processHoverEvent(
        component: BaseComponent,
        pack: LangPack,
        lang: Language,
        context: LangGroup?,
        vararg args: LangArg,
    ) {
        with(component) {
            if (hoverEvent == null) return
            if (hoverEvent.contents != null) {
                val action = hoverEvent.action
                val contents = hoverEvent.contents!!
                val newContents = ArrayList<Content>()
                for (content in contents) {
                    if (content is Text) {
                        newContents.add(Text(process(content.value as String, pack, lang, context, *args)))
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

    private fun processClickEvent(
        component: BaseComponent,
        pack: LangPack,
        lang: Language,
        context: LangGroup?,
        vararg args: LangArg,
    ) {
        with(component) {
            if (clickEvent == null) return
            if (clickEvent.value != null) {
                val action = clickEvent.action
                val value = process(clickEvent.value, pack, lang, context, *args)
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

    private fun processExtras(
        component: TextComponent,
        pack: LangPack,
        lang: Language,
        context: LangGroup?,
        vararg args: LangArg,
    ) {
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
                    newExtras.add(process(next, pack, lang, context, *args))
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
