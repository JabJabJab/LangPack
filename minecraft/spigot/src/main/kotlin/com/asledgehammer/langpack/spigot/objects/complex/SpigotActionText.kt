@file:Suppress("unused")

package com.asledgehammer.langpack.spigot.objects.complex

import com.asledgehammer.cfg.CFGSection
import com.asledgehammer.langpack.core.Language
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.objects.complex.Complex
import com.asledgehammer.langpack.spigot.SpigotLangPack
import com.asledgehammer.langpack.textcomponent.objects.complex.TextComponentActionText
import com.asledgehammer.langpack.textcomponent.objects.complex.TextComponentCommandText
import com.asledgehammer.langpack.textcomponent.objects.complex.TextComponentHoverText
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player

/**
 * **SpigotActionText** wraps the [TextComponentActionText] class to provide additional support for the Spigot API.
 *
 * @author Jab
 */
class SpigotActionText : TextComponentActionText {

    /**
     * None constructor.
     *
     * @param text The text to display.
     */
    constructor(text: String) : super(text)

    /**
     * Hover constructor.
     *
     * @param text The text to display.
     * @param hoverText The hover text to display.
     */
    constructor(text: String, hoverText: TextComponentHoverText) : super(text, hoverText)

    /**
     * Command constructor.
     *
     * @param text The text to display.
     * @param command The command to execute.
     */
    constructor(text: String, command: String) : super(text, command)

    /**
     * Full primitives constructor
     *
     * @param text The text to display.
     * @param command The command to execute.
     * @param hover The hover text to display.
     */
    constructor(text: String, command: String, hover: List<String>) : super(text, command, hover)

    /**
     * Full objects constructor
     *
     * @param text The text to display.
     * @param commandText The command to execute.
     * @param hoverText The hover text to display.
     */
    constructor(text: String, commandText: TextComponentCommandText, hoverText: TextComponentHoverText) : super(text, commandText, hoverText)

    /**
     * Import constructor.
     *
     * @param cfg The YAML to read.
     */
    constructor(cfg: CFGSection) : super(cfg)

    /**
     * Sends the ActionText to a given player.
     *
     * @param player The player to send.
     */
    fun message(player: Player) {
        if (!player.isOnline) return
        player.spigot().sendMessage(get())
    }

    /**
     * Sends the ActionText as a message to a player.
     *
     * @param player The player to receive the message.
     * @param pack (Optional) The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun send(player: Player, pack: SpigotLangPack? = null, vararg args: LangArg) {
        val textComponent = if (pack != null) {
            process(pack, pack.getLanguage(player), definition?.parent, *args)
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
     * @param pack The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun broadcast(pack: SpigotLangPack, vararg args: LangArg) {
        val cache = HashMap<Language, TextComponent>()
        for (player in Bukkit.getOnlinePlayers()) {
            val textComponent: TextComponent
            val lang = pack.getLanguage(player)
            if (cache[lang] != null) {
                textComponent = cache[lang]!!
            } else {
                textComponent = process(pack, pack.getLanguage(player), definition?.parent, *args)
                cache[lang] = textComponent
            }
            player.spigot().sendMessage(textComponent)
        }
    }

    /**
     * Broadcasts the ActionText to all players in a given world.
     *
     * @param pack The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun broadcast(world: World, pack: SpigotLangPack, vararg args: LangArg) {
        val cache = HashMap<Language, TextComponent>()
        for (player in world.players) {
            val textComponent: TextComponent
            val lang = pack.getLanguage(player)
            if (cache[lang] != null) {
                textComponent = cache[lang]!!
            } else {
                textComponent = process(pack, lang, definition?.parent, *args)
                cache[lang] = textComponent
            }
            player.spigot().sendMessage(textComponent)
        }
    }

    /**
     * **SpigotActionText.Loader** overrides [TextComponentActionText] with [SpigotActionText].
     *
     * @author Jab
     */
    class Loader : Complex.Loader<SpigotActionText> {
        override fun load(cfg: CFGSection): SpigotActionText = SpigotActionText(cfg)
    }
}
