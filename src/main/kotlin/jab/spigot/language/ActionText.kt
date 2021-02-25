package jab.spigot.language

import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player

@Suppress("MemberVisibilityCanBePrivate")
class ActionText {

    var text: String
    var hoverText: HoverText? = null
    var hoverItem: HoverItem? = null
    var hoverEntity: HoverEntity? = null


    constructor(text: String, hoverText: HoverText) {
        this.text = text
        this.hoverText = hoverText
    }

    constructor(cfg: ConfigurationSection) {
        text = cfg.getString("text")!!
        //        if (cfg.contains("hover") && cfg.isConfigurationSection("hover")) {
        //            val cfgHover = cfg.getConfigurationSection("hover")!!
        //            if (cfgHover.contains("type")) {
        //                val hoverActionString = cfgHover.getString("type")
        //                if (hoverActionString.equals("text", true) || hoverActionString.equals("show_text", true)) {
        //                    val hoverAction = HoverEvent.Action.SHOW_TEXT
        //                    if (cfgHover.isList("value")) {
        //                        val lines: List<String> = cfgHover.getStringList("value")
        //                        var list: Array<TextComponent> = emptyArray()
        //                        for (arg in lines) {
        //                            list = list.plus(TextComponent(arg))
        //                        }
        //                        component.hoverEvent = HoverEvent(hoverAction, list)
        //                    } else {
        //                        val line = TextComponent(LangPackage.toAString(cfgHover.get("value")!!))
        //                        component.hoverEvent = HoverEvent(hoverAction, arrayOf(line))
        //                    }
        //
        //
        //                } else if (hoverActionString.equals("item", true) || hoverActionString.equals("show_item", true)) {
        //                    hoverAction = HoverEvent.Action.SHOW_ITEM
        //                }
        //            } else if (cfgHover.contains(""))
        //                if (cfg.isList("hover")) {
        //                    component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT)
        //                }
        //        }
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

        val component: TextComponent = TextComponent()

        // If there's assigned hover text, use it.
        when {
            hoverText != null -> {
                val hoverText = hoverText!!
                val processedHoverTextLines = hoverText.process(pkg, lang, *args).split(LangPackage.NEW_LINE)

                var array = emptyArray<TextComponent>()
                for (line in processedHoverTextLines) {
                    array = array.plus(TextComponent(line))
                }
                component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, array)
            }
            hoverItem != null -> {
                TODO("Not implemented.")
            }
            hoverEntity != null -> {
                TODO("Not implemented.")
            }
        }
    }

}