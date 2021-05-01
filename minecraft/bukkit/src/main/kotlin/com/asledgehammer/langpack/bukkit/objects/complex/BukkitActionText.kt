@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.asledgehammer.langpack.bukkit.objects.complex

import com.asledgehammer.config.ConfigSection
import com.asledgehammer.langpack.bukkit.BukkitLangPack
import com.asledgehammer.langpack.bukkit.util.text.TextComponent
import com.asledgehammer.langpack.core.LangPack
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.LangGroup
import com.asledgehammer.langpack.core.objects.complex.Complex
import com.asledgehammer.langpack.core.objects.definition.ComplexDefinition
import com.asledgehammer.langpack.core.objects.definition.LangDefinition
import com.asledgehammer.langpack.core.objects.formatter.FieldFormatter
import com.asledgehammer.langpack.core.processor.LangProcessor
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player

/**
 * **ActionText** packages [BukkitHoverText] and [BukkitCommandText] wrappers.
 * The object is complex and resolvable for [LangProcessor].
 *
 * @author Jab
 */
open class BukkitActionText : Complex<TextComponent> {

    override var definition: ComplexDefinition? = null

    /**
     * The text to display for the resolved [TextComponent].
     */
    var text: String

    /**
     * The text to display for the resolved [TextComponent] when hovered in chat.
     */
    var hoverText: BukkitHoverText? = null

    /**
     * The command to execute for resolved [TextComponent] when clicked in chat.
     */
    var commandText: BukkitCommandText? = null

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
    constructor(text: String, hoverText: BukkitHoverText) {
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
        this.commandText = BukkitCommandText(command)
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
        this.commandText = BukkitCommandText(command)
        this.hoverText = BukkitHoverText(hover)
    }

    /**
     * Full objects constructor
     *
     * @param text The text to display.
     * @param commandText The command to execute.
     * @param hoverText The hover text to display.
     */
    constructor(text: String, commandText: BukkitCommandText, hoverText: BukkitHoverText) {
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
                hoverText = BukkitHoverText(lines)
            }
        }

        text = cfg.getString("text")
        if (cfg.contains("hover")) {
            readHoverText(cfg)
        }

        if (cfg.contains("command")) {
            val line = cfg.getString("command")
            this.commandText = BukkitCommandText(line)
        }
    }

    override fun process(pack: LangPack, lang: Language, context: LangGroup?, vararg args: LangArg): TextComponent {
        val text = pack.processor.process(text, pack, lang, context, *args)
        val component = TextComponent(text)
        if (hoverText != null) component.hoverEvent = hoverText!!.process(pack, lang, context, *args)
        if (commandText != null) component.clickEvent = commandText!!.process(pack, lang, context, *args)
        return component
    }

    override fun walk(definition: LangDefinition<*>): BukkitActionText {
        val walked = BukkitActionText(definition.walk(text))
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
     * Sends the ActionText to a given player.
     *
     * @param player The player to send.
     */
    fun message(player: Player) {
        if (!player.isOnline) return
        player.sendMessage(get().toLegacyText())
    }

    /**
     * Sends the ActionText as a message to a player.
     *
     * @param player The player to receive the message.
     * @param pack (Optional) The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun send(player: Player, pack: BukkitLangPack? = null, vararg args: LangArg) {
        val text = if (pack != null) {
            process(pack, pack.getLanguage(player), definition?.parent, *args).toLegacyText()
        } else {
            get().toLegacyText()
        }
        player.sendMessage(text)
    }

    /**
     * Broadcasts the ActionText to all online players on the server.
     */
    fun broadcast() {
        val message = get().toLegacyText()
        for (player in Bukkit.getOnlinePlayers()) player.sendMessage(message)
    }

    /**
     * Broadcasts the ActionText to all players in a given world.
     *
     * @param world The world to broadcast.
     */
    fun broadcast(world: World) {
        val message = get().toLegacyText()
        for (player in world.players) player.sendMessage(message)
    }

    /**
     * Broadcasts the ActionText to all online players on the server.
     *
     * @param pack The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun broadcast(pack: BukkitLangPack, vararg args: LangArg) {
        val cache = HashMap<Language, String>()
        for (player in Bukkit.getOnlinePlayers()) {
            val text: String
            val lang = pack.getLanguage(player)
            if (cache[lang] != null) {
                text = cache[lang]!!
            } else {
                text = process(pack, pack.getLanguage(player), definition?.parent, *args).toLegacyText()
                cache[lang] = text
            }
            player.sendMessage(text)
        }
    }

    /**
     * Broadcasts the ActionText to all players in a given world.
     *
     * @param pack The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun broadcast(world: World, pack: BukkitLangPack, vararg args: LangArg) {
        val cache = HashMap<Language, String>()
        for (player in world.players) {
            val text: String
            val lang = pack.getLanguage(player)
            if (cache[lang] != null) {
                text = cache[lang]!!
            } else {
                text = process(pack, lang, definition?.parent, *args).toLegacyText()
                cache[lang] = text
            }
            player.sendMessage(text)
        }
    }

    /**
     * **SpongeActionText.Loader** loads [BukkitActionText] from YAML with the assigned type *action*.
     *
     * @author Jab
     */
    class Loader : Complex.Loader<BukkitActionText> {
        override fun load(cfg: ConfigSection): BukkitActionText = BukkitActionText(cfg)
    }
}
