package jab.langpack.commons.objects

import jab.langpack.commons.LangArg
import jab.langpack.commons.LangPack
import jab.langpack.commons.Language
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.configuration.ConfigurationSection
import kotlin.collections.ArrayList

/**
 * TODO: Document.
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
open class ActionText : LangComponent {

    /**
     * TODO: Document.
     */
    var text: String

    /**
     * TODO: Document.
     */
    var hoverText: HoverText? = null

    /**
     * TODO: Document.
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
     * TODO: Document.
     *
     * @param cfg
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

    override fun process(pkg: LangPack, lang: Language, vararg args: LangArg): TextComponent {

        val text = pkg.processor.processString(text, pkg, lang, *args)
        val component = TextComponent(text)

        if (hoverText != null) {
            component.hoverEvent = hoverText!!.process(pkg, lang, *args)
        }
        if (commandText != null) {
            component.clickEvent = commandText!!.process(pkg, lang, *args)
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
