package jab.spigot.language

import jab.spigot.language.test.LangTest
import jab.spigot.language.test.LangTestActionText
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

    private val tests: HashMap<String, LangTest>

    init {
        val command = plugin.getCommand("lang")
        if (command != null) {
            command.setExecutor(this)
            command.tabCompleter = this
        }

        tests = HashMap()
        if (LangCfg.testsEnabled) {
            addTest(LangTestActionText())
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player || !sender.isOp) {
            return true
        }

        val player: Player = sender
        val lang = plugin.lang!!

        fun test() {
            if(args.size < 2) {

            }
        }

        if (args.size >= 1) {
            val firstArg = args[0].toLowerCase()

            when (firstArg) {
                "test" -> {
                    test()
                }
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

    private fun addTest(test: LangTest) {
        tests[test.name] = test
    }

    companion object {
        private val EMPTY_LIST = ArrayList<String>()
    }
}