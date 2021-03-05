package jab.spigot.language.`object`

import jab.spigot.language.LangArg
import jab.spigot.language.LangPackage
import jab.spigot.language.Language
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList

/**
 * TODO: Document.
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class ActionText : LangComponent {

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

    override fun process(pkg: LangPackage, lang: Language, vararg args: LangArg): TextComponent {

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

    /**
     * Sends the ActionText to a given player.
     *
     * @param player The player to send.
     */
    fun message(player: Player) {

        // Make sure that only online players are processed.
        if (!player.isOnline) {
            return
        }

        player.spigot().sendMessage(get())
    }

    /**
     * Sends the ActionText as a message to a player.
     *
     * @param player The player to receive the message.
     * @param pkg (Optional) The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun send(player: Player, pkg: LangPackage? = null, vararg args: LangArg) {

        val textComponent = if (pkg != null) {
            process(pkg, Language.getLanguage(player, pkg.defaultLang), *args)
        } else {
            get()
        }

        player.spigot().sendMessage(textComponent)
    }

    /**
     * Broadcasts the ActionText to all online players on the server.
     */
    fun broadcast() {

        val message = get()
        for (player in Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(message)
        }
    }

    /**
     * Broadcasts the ActionText to all players in a given world.
     *
     * @param world The world to broadcast.
     */
    fun broadcast(world: World) {

        val message = get()
        for (player in world.players) {
            player.spigot().sendMessage(message)
        }
    }

    /**
     * Broadcasts the ActionText to all online players on the server.
     *
     * @param pkg The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun broadcast(pkg: LangPackage, vararg args: LangArg) {

        val cache = EnumMap<Language, TextComponent>(Language::class.java)

        for (player in Bukkit.getOnlinePlayers()) {

            val textComponent: TextComponent
            val lang = Language.getLanguage(player, pkg.defaultLang)

            if (cache[lang] != null) {
                textComponent = cache[lang]!!
            } else {
                textComponent = process(pkg, Language.getLanguage(player, pkg.defaultLang), *args)
                cache[lang] = textComponent
            }

            player.spigot().sendMessage(textComponent)
        }
    }

    /**
     * Broadcasts the ActionText to all players in a given world.
     *
     * @param pkg The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun broadcast(world: World, pkg: LangPackage, vararg args: LangArg) {

        val cache = EnumMap<Language, TextComponent>(Language::class.java)

        for (player in world.players) {

            val textComponent: TextComponent
            val lang = Language.getLanguage(player, pkg.defaultLang)

            if (cache[lang] != null) {
                textComponent = cache[lang]!!
            } else {
                textComponent = process(pkg, Language.getLanguage(player, pkg.defaultLang), *args)
                cache[lang] = textComponent
            }

            player.spigot().sendMessage(textComponent)
        }
    }
}
