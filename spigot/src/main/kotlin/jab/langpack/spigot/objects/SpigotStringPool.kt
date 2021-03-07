package jab.langpack.spigot.objects

import jab.langpack.commons.LangArg
import jab.langpack.commons.Language
import jab.langpack.commons.objects.StringPool
import jab.langpack.spigot.SpigotLangPack
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import java.util.*

class SpigotStringPool: StringPool {

    constructor(mode: Mode, random: Random) : super(mode, random)
    constructor(cfg: ConfigurationSection) : super(cfg)

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

        player.sendMessage(get())
    }

    /**
     * Sends the ActionText as a message to a player.
     *
     * @param player The player to receive the message.
     * @param pack (Optional) The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun send(player: Player, pack: SpigotLangPack? = null, vararg args: LangArg) {

        val message = if (pack != null) {
            process(pack, pack.getLanguage(player), *args)
        } else {
            get()
        }

        player.sendMessage(message)
    }

    /**
     * Broadcasts the ActionText to all online players on the server.
     */
    fun broadcast() {

        val message = get()
        for (player in Bukkit.getOnlinePlayers()) {
            player.sendMessage(message)
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
            player.sendMessage(message)
        }
    }

    /**
     * Broadcasts the ActionText to all online players on the server.
     *
     * @param pack The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun broadcast(pack: SpigotLangPack, vararg args: LangArg) {

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
     * Broadcasts the ActionText to all players in a given world.
     *
     * @param pack The package to process the text.
     * @param args (Optional) Additional arguments to provide to process the text.
     */
    fun broadcast(world: World, pack: SpigotLangPack, vararg args: LangArg) {

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
}