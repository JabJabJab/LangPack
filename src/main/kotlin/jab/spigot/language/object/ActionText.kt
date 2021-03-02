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

/**
 * TODO: Document.
 *
 * @author Jab
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class ActionText : LangComponent {

    /** TODO: Document. */
    var text: String

    /** TODO: Document. */
    var hoverText: HoverText? = null

    /** TODO: Document. */
    var hoverItem: HoverItem? = null

    /** TODO: Document. */
    var hoverEntity: HoverEntity? = null


    /**
     * TODO: Document.
     *
     * @param text
     * @param hoverText
     */
    constructor(text: String, hoverText: HoverText) {
        this.text = text
        this.hoverText = hoverText
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
                    lines.add(Text(LangPackage.toAString(cfg.get("hover_text")!!)))
                }
                hoverText = HoverText(lines)
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

        // If there's assigned hover text, use it.
        when {
            hoverText != null -> {
                component.hoverEvent = hoverText!!.process(pkg, lang, *args)
            }
            hoverItem != null -> {
                TODO("Not implemented.")
            }
            hoverEntity != null -> {
                TODO("Not implemented.")
            }
        }

        return component
    }

    override fun get(): TextComponent {
        val component = TextComponent(text)

        // If there's assigned hover text, use it.
        when {
            hoverText != null -> {
                component.hoverEvent = hoverText!!.get()
            }
            hoverItem != null -> {
                TODO("Not implemented.")
            }
            hoverEntity != null -> {
                TODO("Not implemented.")
            }
        }

        return component
    }

    /**
     * Broadcasts the ActionText to all online players on the server.
     */
    fun broadcast() {
        for (player in Bukkit.getOnlinePlayers()) {
            message(player)
        }
    }

    /**
     * Broadcasts the ActionText to all players in a given world.
     *
     * @param world The world to broadcast.
     */
    fun broadcast(world: World) {
        for (player in world.players) {
            message(player)
        }
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
}