@file:Suppress("unused")

package jab.langpack.spigot

import jab.langpack.core.LangArg
import jab.langpack.core.LangCache
import jab.langpack.core.LangPack
import jab.langpack.core.Language
import jab.langpack.core.objects.ActionText
import jab.langpack.core.objects.Complex
import jab.langpack.core.objects.StringPool
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

// #############################
// ########  LangPack   ########
// #############################

/**
 * Broadcasts a message to all online players, checking their locales and sending the corresponding dialog.
 *
 *
 * @param query The ID of the dialog to send.
 * @param args The variables to apply to the dialog sent.
 */
fun LangPack.broadcast(query: String, vararg args: LangArg) {

    val cache: EnumMap<Language, TextComponent> = EnumMap<Language, TextComponent>(Language::class.java)

    for (player in Bukkit.getOnlinePlayers()) {

        // Grab the players language, else fallback to default.
        val langPlayer = getLanguage(player)
        var lang = langPlayer

        if (cache[lang] != null) {
            player.spigot().sendMessage(cache[lang])
            continue
        }

        var resolved = resolve(lang, query)
        if (resolved == null) {
            lang = defaultLang
            resolved = resolve(lang, query)
        }

        val component: TextComponent
        if (resolved != null) {

            val value = resolved.value

            component = when (value) {

                is Complex<*> -> {
                    val result = value.get()
                    val processedComponent: TextComponent = if (result is TextComponent) {
                        result
                    } else {
                        TextComponent(result.toString())
                    }
                    processedComponent
                }
                else -> {
                    TextComponent(value.toString())
                }
            }
        } else {
            component = TextComponent(query)
        }

        val result = processor.process(component, this, langPlayer, *args)
        cache[lang] = result
        cache[langPlayer] = result

        player.spigot().sendMessage(result)
    }
}

/**
 * Messages a player with a given field and arguments. The language will be based on [Player.getLocale].
 *   If the language is not supported, [LangPack.defaultLang] will be used.
 *
 * @param player The player to send the message.
 * @param query The field to send.
 * @param args Additional arguments to apply.
 */
fun LangPack.message(player: Player, query: String, vararg args: LangArg) {

    val langPlayer = getLanguage(player)
    var lang = langPlayer

    var resolved = resolve(lang, query)
    if (resolved == null) {
        lang = defaultLang
        resolved = resolve(lang, query)
    }

    val component: TextComponent
    if (resolved != null) {
        val value = resolved.value
        component = when (value) {
            is Complex<*> -> {
                val result = value.get()
                val processedComponent: TextComponent = if (result is TextComponent) {
                    result
                } else {
                    TextComponent(result.toString())
                }
                processedComponent
            }
            else -> {
                TextComponent(value.toString())
            }
        }
    } else {
        component = TextComponent(query)
    }

    player.spigot().sendMessage(
        processor.process(component, this, langPlayer, *args)
    )
}

/**
 * @param player The player to read.
 *
 * @return Returns the language of the player's [Player.getLocale]. If the locale set is invalid, the fallBack
 *   is returned.
 */
fun LangPack.getLanguage(player: Player): Language {
    val locale = player.locale
    for (lang in Language.values()) {
        if (lang.abbreviation.equals(locale, true)) {
            return lang
        }
    }
    return defaultLang
}

/**
 * Message a player with multiple lines of text.
 *
 * @param sender The player to send the texts.
 * @param lines The lines of text to send.
 */
fun LangPack.Companion.message(sender: CommandSender, lines: Array<String>) {
    if (lines.isEmpty()) return
    sender.sendMessage(lines)
}

/**
 * Message a player with multiple lines of text.
 *
 * @param sender The player to send the texts.
 * @param lines The lines of text to send.
 */
fun LangPack.Companion.message(sender: CommandSender, lines: List<String>) {
    // Convert to an array to send all messages at once.
    var array = emptyArray<String>()
    for (line in lines) {
        array = array.plus(line)
    }
    sender.sendMessage(array)
}

/**
 * Broadcasts multiple lines of text to all players on the server.
 *
 * @param lines The lines of text to broadcast.
 */
fun LangPack.Companion.broadcast(lines: Array<String>) {
    for (line in lines) {
        Bukkit.broadcastMessage(line)
    }
}

/**
 * Broadcasts multiple lines of text to all players on the server.
 *
 * @param lines The lines of text to broadcast.
 */
fun LangPack.Companion.broadcast(lines: List<String>) {
    for (line in lines) {
        Bukkit.broadcastMessage(line)
    }
}

/**
 * Broadcasts multiple lines of text to all players on the server.
 *
 * <br/><b>NOTE:</b> If any lines of text are null, it is ignored.
 *
 * @param lines The lines of text to broadcast.
 */
fun LangPack.Companion.broadcastSafe(lines: Array<String?>) {
    for (line in lines) {
        if (line != null) {
            Bukkit.broadcastMessage(line)
        }
    }
}

/**
 * Broadcasts multiple lines of text to all players on the server.
 *
 * <br/><b>NOTE:</b> If any lines of text are null, it is ignored.
 *
 * @param lines The lines of text to broadcast.
 */
fun LangPack.Companion.broadcastSafe(lines: List<String?>) {
    for (line in lines) {
        if (line != null) {
            Bukkit.broadcastMessage(line)
        }
    }
}

// #############################
// ########  LangCache  ########
// #############################

/**
 * @see LangPack.broadcast
 */
fun LangCache<*>.broadcast(field: String, vararg args: LangArg) {
    pack.broadcast(field, *args)
}

/**
 * @see LangPack.message
 */
fun LangCache<*>.message(player: Player, field: String, vararg args: LangArg) {
    pack.message(player, field, *args)
}

/**
 * @see LangPack.getLanguage
 */
fun LangCache<*>.getLanguage(player: Player): Language = pack.getLanguage(player)

/**
 * Sends the ActionText to a given player.
 *
 * @param player The player to send.
 */
fun ActionText.message(player: Player) {
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
fun ActionText.send(player: Player, pack: LangPack? = null, vararg args: LangArg) {
    val textComponent = if (pack != null) {
        process(pack, pack.getLanguage(player), *args)
    } else {
        get()
    }
    player.spigot().sendMessage(textComponent)
}

/**
 * Broadcasts the ActionText to all online players on the server.
 */
fun ActionText.broadcast() {
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
fun ActionText.broadcast(world: World) {
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
fun ActionText.broadcast(pack: LangPack, vararg args: LangArg) {

    val cache = EnumMap<Language, TextComponent>(Language::class.java)

    for (player in Bukkit.getOnlinePlayers()) {

        val textComponent: TextComponent
        val lang = pack.getLanguage(player)

        if (cache[lang] != null) {
            textComponent = cache[lang]!!
        } else {
            textComponent = process(pack, pack.getLanguage(player), *args)
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
fun ActionText.broadcast(world: World, pack: LangPack, vararg args: LangArg) {

    val cache = EnumMap<Language, TextComponent>(Language::class.java)

    for (player in world.players) {

        val textComponent: TextComponent
        val lang = pack.getLanguage(player)

        if (cache[lang] != null) {
            textComponent = cache[lang]!!
        } else {
            textComponent = process(pack, lang, *args)
            cache[lang] = textComponent
        }

        player.spigot().sendMessage(textComponent)
    }
}

// #############################
// ######## STRING POOL ########
// #############################

/**
 * Sends the StringPool to a given player.
 *
 * @param player The player to send.
 */
fun StringPool.message(player: Player) {
    if (!player.isOnline) return
    player.sendMessage(get())
}

/**
 * Sends the StringPool as a message to a player.
 *
 * @param player The player to receive the message.
 * @param pack (Optional) The package to process the text.
 * @param args (Optional) Additional arguments to provide to process the text.
 */
fun StringPool.send(player: Player, pack: LangPack? = null, vararg args: LangArg) {
    val message = if (pack != null) {
        process(pack, pack.getLanguage(player), *args)
    } else {
        get()
    }
    player.sendMessage(message)
}

/**
 * Broadcasts the StringPool to all online players on the server.
 */
fun StringPool.broadcast() {
    val message = get()
    for (player in Bukkit.getOnlinePlayers()) {
        player.sendMessage(message)
    }
}

/**
 * Broadcasts the StringPool to all players in a given world.
 *
 * @param world The world to broadcast.
 */
fun StringPool.broadcast(world: World) {
    val message = get()
    for (player in world.players) {
        player.sendMessage(message)
    }
}

/**
 * Broadcasts the StringPool to all online players on the server.
 *
 * @param pack The package to process the text.
 * @param args (Optional) Additional arguments to provide to process the text.
 */
fun StringPool.broadcast(pack: LangPack, vararg args: LangArg) {

    val cache = EnumMap<Language, String>(Language::class.java)

    for (player in Bukkit.getOnlinePlayers()) {

        val message: String
        val lang = pack.getLanguage(player)

        if (cache[lang] != null) {
            message = cache[lang]!!
        } else {
            message = process(pack, pack.getLanguage(player), *args)
            cache[lang] = message
        }

        player.sendMessage(message)
    }
}

/**
 * Broadcasts the StringPool to all players in a given world.
 *
 * @param pack The package to process the text.
 * @param args (Optional) Additional arguments to provide to process the text.
 */
fun StringPool.broadcast(world: World, pack: LangPack, vararg args: LangArg) {

    val cache = EnumMap<Language, String>(Language::class.java)

    for (player in world.players) {

        val message: String
        val lang = pack.getLanguage(player)

        if (cache[lang] != null) {
            message = cache[lang]!!
        } else {
            message = process(pack, lang, *args)
            cache[lang] = message
        }

        player.sendMessage(message)
    }
}
