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

@Suppress("MemberVisibilityCanBePrivate")
class ActionText : LangComponent {

    var text: String
    var hoverText: HoverText? = null
    var hoverItem: HoverItem? = null
    var hoverEntity: HoverEntity? = null


    /**
     * @param text
     * @param hoverText
     */
    constructor(text: String, hoverText: HoverText) {
        this.text = text
        this.hoverText = hoverText
    }

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

    fun messageDynamic(
        player: Player,
        pkg: LangPackage,
        lang: Language = Language.getLanguage(player),
        vararg args: LangArg
    ) {

        // Make sure that only online players are processed.
        if (!player.isOnline) {
            return
        }

        val component = TextComponent()

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

        player.spigot().sendMessage(component)
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

}