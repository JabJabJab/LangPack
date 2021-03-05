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
internal class LangCommand(private val plugin: LangPlugin) : CommandExecutor, TabCompleter {

    private val tests = HashMap<String, LangTest>()

    init {
        val command = plugin.getCommand("lang")
        if (command == null) {
            System.err.println("The command 'lang' is not registered.")
        } else {
            command.setExecutor(this)
            command.tabCompleter = this

            if (LangCfg.testsEnabled) {
                addTest(LangTestActionText())
            }
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player || !sender.isOp) {
            return true
        }

        val player: Player = sender
        val lang = plugin.lang!!

        fun test() {

            // Make sure that 'tests_enabled' is set to true in the global config.
            if (!LangCfg.testsEnabled) {
                lang.message(player, "lang_command_test_disabled")
                return
            }

            // Make sure that a test name is provided.
            if (args.size < 2) {
                lang.message(player, "lang_command_test_help")
                return
            }

            val testName = args[1].toLowerCase()
            val argTest = LangArg("test", testName)

            // Make sure the test exists.
            val test = tests[testName]
            if (test == null) {
                lang.message(player, "lang_command_test_not_found", argTest)
                return
            }

            lang.message(player, "lang_command_test_run", argTest)

            // Run the test.
            val result = test.test(lang, player)

            // Display the result.
            if (result.success) {
                lang.message(player, "lang_command_test_success", argTest)
            } else {
                val argReason = LangArg("reason", result.reason)
                lang.message(player, "lang_command_test_failure", argTest, argReason)
            }

            return
        }

        var found = false

        // Scan for sub-commands.
        if (args.size >= 1) {
            val firstArg = args[0].toLowerCase()

            when (firstArg) {
                "test" -> {
                    test()
                    found = true
                }
            }

            // Let the player know the command is invalid before sending the help dialog.
            if (!found) {
                lang.message(player, "lang_command_not_found", LangArg("command", args[0]))
            }
        }

        // Display help message if no command is found.
        if (!found) {
            lang.message(player, "lang_command_help")
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {

        if (args.isEmpty()) {
            return SUB_COMMANDS
        }

        if (args.size == 1) {
            val arg0 = args[0].toLowerCase()

            return if (arg0.isEmpty()) {
                SUB_COMMANDS
            } else {

                val list = ArrayList<String>()

                for (subCommand in SUB_COMMANDS) {
                    if (subCommand.contains(arg0)) {
                        list.add(subCommand)
                    }
                }

                list
            }
        }

        return EMPTY_LIST
    }

    private fun addTest(test: LangTest) {
        tests[test.name] = test
    }

    companion object {
        private val EMPTY_LIST = ArrayList<String>()
        private val SUB_COMMANDS = ArrayList<String>()

        init {
            SUB_COMMANDS.add("test")
        }
    }
}
