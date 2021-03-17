@file:Suppress("MemberVisibilityCanBePrivate")

package jab.langpack.core.objects.complex

import jab.langpack.core.LangPack
import jab.langpack.core.Language
import jab.langpack.core.objects.LangArg
import jab.langpack.core.objects.LangGroup
import jab.langpack.core.objects.definition.LangDefinition
import jab.langpack.core.objects.formatter.FieldFormatter
import jab.langpack.core.processor.LangProcessor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.configuration.ConfigurationSection

/**
 * TODO: Update documentation to reflect Definition API update.
 *
 * The **ActionText** class packages defined [HoverEvent] and [ClickEvent] as [HoverText] and [CommandText] wrappers for
 * dynamic [TextComponent] usage for lang-packs.
 *
 * The object is complex and resolvable for [LangProcessor].
 *
 * @author Jab
 */
open class ActionText : Complex<TextComponent> {

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
    constructor(cfg: ConfigurationSection) {

        val readHoverText = fun(cfg: ConfigurationSection) {

            if (cfg.contains("hover")) {
                val lines = ArrayList<Text>()
                if (cfg.isList("hover")) {
                    for (arg in cfg.getStringList("hover")) {
                        if (lines.isEmpty()) {
                            lines.add(Text(arg))
                        } else {
                            lines.add(Text("\n$arg"))
                        }
                    }
                } else {
                    lines.add(Text(cfg.getString("hover")))
                }
                hoverText = HoverText(lines)
            }
        }

        text = cfg.getString("text")!!
        if (cfg.contains("hover")) {
            readHoverText(cfg)
        }

        if (cfg.contains("command")) {
            val line = cfg.getString("command")!!
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

    override fun walk(definition: LangDefinition<*>): ActionText {
        val walked = ActionText(definition.walk(text))
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

        if (hoverText != null) {
            component.hoverEvent = hoverText!!.get()
        }
        if (commandText != null) {
            component.clickEvent = commandText!!.get()
        }

        return component
    }
}
