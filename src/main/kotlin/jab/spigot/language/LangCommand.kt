package jab.spigot.language

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * TODO: Document.
 *
 * @author Jab
 *
 * @property plugin
 */
class LangCommand(private val plugin: LangPlugin) : CommandExecutor, TabCompleter {

    init {
        val command = plugin.getCommand("lang")
        if (command != null) {
            command.setExecutor(this)
            command.tabCompleter = this
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player || !sender.isOp) {
            return true
        }

        val player: Player = sender

        val lang = plugin.lang!!

        when {
            command.name.equals("hover", true) -> {
                lang.messageField(player, "hover_command_msg", LangArg("player", player.displayName))
            }
            command.name.equals("subcommand", true) -> {

            }
            command.name.equals("subsubcommand", true) -> {

            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        return EMPTY_LIST
    }

    companion object {
        private val EMPTY_LIST = ArrayList<String>()
    }
}