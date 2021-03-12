package jab.langpack.core.objects

import jab.langpack.core.LangArg
import jab.langpack.core.LangPack
import jab.langpack.core.Language
import jab.langpack.core.processor.LangProcessor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.configuration.ConfigurationSection

/**
 * The **ActionText** class packages defined [HoverEvent] and [ClickEvent] as [HoverText] and [CommandText] wrappers for
 * dynamic [TextComponent] usage for lang-packs.
 *
 * The object is complex and resolvable for [LangProcessor].
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
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
     * Import constructor.
     *
     * @param cfg The YAML to read.
     */
    constructor(cfg: ConfigurationSection) {

        val readHoverText = fun(cfg: ConfigurationSection) {

            if (cfg.contains("hover_text")) {
                val lines = ArrayList<Text>()
                if (cfg.isList("hover_text")) {
                    for (arg in cfg.getStringList("hover_text")) {
                        if (lines.isEmpty()) {
                            lines.add(Text(arg))
                        } else {
                            lines.add(Text("\n$arg"))
                        }
                    }
                } else {
                    lines.add(Text(cfg.getString("hover_text")))
                }
                hoverText = HoverText(lines)
            }

            if (cfg.contains("command")) {
                val line = cfg.getString("command")!!
                this.commandText = CommandText(line)
            }
        }

        text = cfg.getString("text")!!
        if (cfg.contains("hover_text")) {
            readHoverText(cfg)
        }
    }

    override fun process(pack: LangPack, lang: Language, vararg args: LangArg): TextComponent {

        val text = pack.processor.processString(text, pack, lang, *args)
        val component = TextComponent(text)

        if (hoverText != null) {
            component.hoverEvent = hoverText!!.process(pack, lang, *args)
        }
        if (commandText != null) {
            component.clickEvent = commandText!!.process(pack, lang, *args)
        }

        return component
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
