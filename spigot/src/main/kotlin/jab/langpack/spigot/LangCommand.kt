package jab.langpack.spigot

import jab.langpack.core.LangArg
import jab.langpack.core.LangCache
import jab.langpack.test.LangTest
import jab.langpack.spigot.test.LangTestActionText
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * The **LangCommand** class handles all command executions under the "__/lang__" command.
 *
 * @author Jab
 *
 * @property plugin The plugin instance.
 */
internal class LangCommand(private val plugin: LangPlugin) : CommandExecutor, TabCompleter {

    private val tests = HashMap<String, LangTest<Player>>()
    private val cache = LangCache(plugin.pack!!)

    init {
        val command = plugin.getCommand("lang")
        if (command == null) {
            System.err.println("The command 'lang' is not registered.")
        } else {
            command.setExecutor(this)
            command.tabCompleter = this

            if (LangPlugin.CFG.testsEnabled) {
                addTest(LangTestActionText())
            }
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player || !sender.isOp) {
            return true
        }

        val player: Player = sender
        val lang = plugin.pack!!

        fun test() {

            // Make sure that 'tests_enabled' is set to true in the global config.
            if (!LangPlugin.CFG.testsEnabled) {
                lang.message(player, "lang_command_test.disabled")
                return
            }

            // Make sure that a test name is provided.
            if (args.size < 2) {
                lang.message(player, "lang_command_test.help")
                return
            }

            val testName = args[1].toLowerCase()
            val argTest = LangArg("test", testName)

            // Make sure the test exists.
            val test = tests[testName]
            if (test == null) {
                lang.message(player, "lang_command_test.not_found", argTest)
                return
            }

            lang.message(player, "lang_command_test.run", argTest)

            // Run the test.
            val result = test.test(lang, player)

            // Display the result.
            if (result.success) {
                lang.message(player, "lang_command_test.success", argTest)
            } else {
                val argReason = LangArg("reason", result.reason)
                lang.message(player, "lang_command_test.failure", argTest, argReason)
            }

            return
        }

        fun tests() {
            val builder = StringBuilder("${ChatColor.GRAY}[")
            val names = ArrayList<String>(tests.keys)
            if (names.isNotEmpty()) {
                names.sortBy { it }
                for (name in names) {
                    builder.append(ChatColor.GOLD).append(name).append(ChatColor.GRAY).append(",")
                }
                builder.setLength(builder.length - 3)
                builder.append(ChatColor.GRAY).append("]")
            } else {
                builder.append("]")
            }

            player.sendMessage("${ChatColor.GREEN}Tests: $builder")
        }

        var found = false

        // Scan for sub-commands.
        if (args.isNotEmpty()) {

            when (args[0].toLowerCase()) {
                "test" -> {
                    test()
                    found = true
                }

                "tests" -> {
                    tests()
                    found = true
                }
            }

            // Let the player know the command is invalid before sending the help dialog.
            if (!found) {
                lang.message(player, "lang_command.not_found", LangArg("command", args[0]))
            }
        }

        // Display help message if no command is found.
        if (!found) {
            lang.message(player, "lang_command.help")
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {

        if (args.isEmpty() || sender !is Player) {
            return SUB_COMMANDS
        }

        val lang = cache.getLanguage(sender)

        when (args.size) {

            1 -> {

                val arg0 = args[0].toLowerCase()

                return if (arg0.isEmpty()) {
                    SUB_COMMANDS
                } else {

                    val list = ArrayList<String>()

                    for (subCommand in SUB_COMMANDS) {
                        if (subCommand.contains(arg0, true)) {
                            list.add(subCommand)
                        }
                    }

                    list
                }

            }

            2 -> {

                if (args[1].isEmpty()) {
                    return ArrayList(tests.keys)
                } else {
                    val arg2 = args[1].toLowerCase()

                    if (arg2.isEmpty()) {
                        return ArrayList(tests.keys)
                    } else {

                        val list = ArrayList<String>()

                        for (key in tests.keys) {
                            if (key.contains(arg2, true)) {
                                list.add(key)
                            }
                        }

                        if (list.isEmpty()) {
                            list.add(cache.getString("lang_command_test.tooltip_not_found", lang))
                        }

                        return list
                    }
                }
            }
        }

        return EMPTY_LIST
    }


    private fun addTest(test: LangTest<Player>) {
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
