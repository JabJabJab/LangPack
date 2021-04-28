@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.asledgehammer.langpack.sponge.objects.complex

import com.asledgehammer.config.ConfigSection
import com.asledgehammer.langpack.core.LangPack
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.LangGroup
import com.asledgehammer.langpack.core.objects.complex.Complex
import com.asledgehammer.langpack.core.objects.definition.LangDefinition
import com.asledgehammer.langpack.core.objects.formatter.FieldFormatter
import com.asledgehammer.langpack.core.processor.LangProcessor
import com.asledgehammer.langpack.sponge.util.text.TextComponent

/**
 * **ActionText** packages [HoverText] and [CommandText] wrappers.
 * The object is complex and resolvable for [LangProcessor].
 *
 * @author Jab
 */
open class SpongeActionText : Complex<TextComponent> {

    /**
     * The text to display for the resolved [TextComponent].
     */
    var text: String

    /**
     * The text to display for the resolved [TextComponent] when hovered in chat.
     */
    var hoverText: HoverText? = null

    /**
     * The command to execute for resolved [TextComponent] when clicked in chat.
     */
    var commandText: CommandText? = null

    /**
     * None constructor.
     *
     * @param text The text to display.
     */
    constructor(text: String) {
        this.text = text
    }

    /**
     * Hover constructor.
     *
     * @param text The text to display.
     * @param hoverText The hover text to display.
     */
    constructor(text: String, hoverText: HoverText) {
        this.text = text
        this.hoverText = hoverText
    }

    /**
     * Command constructor.
     *
     * @param text The text to display.
     * @param command The command to execute.
     */
    constructor(text: String, command: String) {
        this.text = text
        this.commandText = CommandText(command)
    }

    /**
     * Full primitives constructor
     *
     * @param text The text to display.
     * @param command The command to execute.
     * @param hover The hover text to display.
     */
    constructor(text: String, command: String, hover: List<String>) {
        this.text = text
        this.commandText = CommandText(command)
        this.hoverText = HoverText(hover)
    }

    /**
     * Full objects constructor
     *
     * @param text The text to display.
     * @param commandText The command to execute.
     * @param hoverText The hover text to display.
     */
    constructor(text: String, commandText: CommandText, hoverText: HoverText) {
        this.text = text
        this.commandText = commandText
        this.hoverText = hoverText
    }

    /**
     * Import constructor.
     *
     * @param cfg The YAML to read.
     */
    constructor(cfg: ConfigSection) {
        val readHoverText = fun(cfg: ConfigSection) {
            if (cfg.contains("hover")) {
                val lines = ArrayList<String>()
                if (cfg.isList("hover")) {
                    for (arg in cfg.getStringList("hover")) {
                        if (lines.isEmpty()) {
                            lines.add(arg)
                        } else {
                            lines.add("\n$arg")
                        }
                    }
                } else {
                    lines.add(cfg.getString("hover"))
                }
                hoverText = HoverText(lines)
            }
        }

        text = cfg.getString("text")
        if (cfg.contains("hover")) {
            readHoverText(cfg)
        }

        if (cfg.contains("command")) {
            val line = cfg.getString("command")
            this.commandText = CommandText(line)
        }
    }

    override fun process(pack: LangPack, lang: Language, context: LangGroup?, vararg args: LangArg): TextComponent {
        val text = pack.processor.process(text, pack, lang, context, *args)
        val component = TextComponent(text)
        if (hoverText != null) component.hoverEvent = hoverText!!.process(pack, lang, context, *args)
        if (commandText != null) component.clickEvent = commandText!!.process(pack, lang, context, *args)
        return component
    }

    override fun walk(definition: LangDefinition<*>): SpongeActionText {
        val walked = SpongeActionText(definition.walk(text))
        if (commandText != null) walked.commandText = commandText!!.walk(definition)
        if (hoverText != null) walked.hoverText = hoverText!!.walk(definition)
        return walked
    }

    override fun needsWalk(formatter: FieldFormatter): Boolean {
        if (formatter.needsWalk(text)) return true
        if (commandText != null && commandText!!.needsWalk(formatter)) return true
        else if (hoverText != null && hoverText!!.needsWalk(formatter)) return true
        return false
    }

    override fun get(): TextComponent {
        val component = TextComponent(text)
        if (hoverText != null) component.hoverEvent = hoverText!!.get()
        if (commandText != null) component.clickEvent = commandText!!.get()
        return component
    }

    /**
     * **SpongeActionText.Loader** loads [SpongeActionText] from YAML with the assigned type *action*.
     *
     * @author Jab
     */
    class Loader : Complex.Loader<SpongeActionText> {
        override fun load(cfg: ConfigSection): SpongeActionText = SpongeActionText(cfg)
    }
}
