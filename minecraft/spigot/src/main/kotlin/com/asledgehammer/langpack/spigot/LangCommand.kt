package com.asledgehammer.langpack.spigot

import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.test.LangTest
import com.asledgehammer.langpack.spigot.test.InvokeActionObjectTest
import com.asledgehammer.langpack.spigot.test.SimpleTest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * **LangCommand** handles all command executions under the *"lang"* command.
 *
 * @author Jab
 *
 * @property plugin The plugin instance.
 */
internal class LangCommand(private val plugin: LangPlugin) : CommandExecutor, TabCompleter {

    private val tests = HashMap<String, LangTest<SpigotLangPack, Player>>()
    private val emptyList = ArrayList<String>()
    private val subCommands = ArrayList<String>()
    private val cache = SpigotLangCache(plugin.pack)
    private val pack = plugin.pack

    init {

        val command = plugin.getCommand("lang")
        require(command != null) { "The command 'lang' is not registered." }
        command.setExecutor(this)
        command.tabCompleter = this

        if (plugin.testsEnabled) {
            subCommands.add("test")
            subCommands.add("tests")

            addTest("basic")
            addTest("multiline")
            addTest("placeholder")
            addTest("visibility_scope")
            addTest("broadcast")
            addTest("action")

            addTest(InvokeActionObjectTest(pack))
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return true
        val player: Player = sender
        var found = false

        // Scan for sub-commands.
        if (args.isNotEmpty()) {
            when (args[0].toLowerCase()) {
                "test" -> {
                    onTestCommand(player, args)
                    found = true
                }
                "tests" -> {
                    onTestsCommand(player)
                    found = true
                }
            }
            // Let the player know the command is invalid before sending the help dialog.
            if (!found) pack.message(player, "command.not_found", LangArg("command", args[0]))
        }

        // Display help message if no command is found.
        if (!found) pack.message(player, "command.help")
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>,
    ): MutableList<String> {

        if (args.isEmpty() || sender !is Player) return subCommands

        val lang = cache.getLanguage(sender)
        when (args.size) {
            1 -> {
                val arg0 = args[0].toLowerCase()
                return if (arg0.isEmpty()) {
                    subCommands
                } else {
                    val list = ArrayList<String>()
                    for (subCommand in subCommands) {
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
                    return if (arg2.isEmpty()) {
                        ArrayList(tests.keys)
                    } else {
                        val list = ArrayList<String>()
                        for (key in tests.keys) {
                            if (key.contains(arg2, true)) {
                                list.add(key)
                            }
                        }
                        if (list.isEmpty()) list.add(cache.getString("test.tooltip_not_found", lang))
                        list
                    }
                }
            }
        }
        return emptyList
    }

    private fun onTestCommand(player: Player, args: Array<out String>) {
        if (!player.hasPermission("langpack.test")) {
            pack.message(player, "permission.deny")
            return
        }

        // Make sure that 'tests_enabled' is set to true in the global config.
        if (!plugin.testsEnabled) {
            pack.message(player, "test.disabled")
            return
        }

        // Make sure that a test name is provided.
        if (args.size < 2 || args.size > 3) {
            pack.message(player, "test.help")
            return
        }

        val testName = args[1].toLowerCase()
        val argTest = LangArg("test", testName)

        // Make sure the test exists.
        val test = tests[testName]
        if (test == null) {
            pack.message(player, "test.not_found", argTest)
            return
        }

        if (args.size == 3) {
            if (!args[2].equals("run", true)) {
                pack.message(player, "test.help")
                return
            }

            pack.message(player, "test.start", argTest)

            // Run the test.
            val result = test.test(pack, player)

            // Display the result.
            if (result.success) {
                pack.message(player, "test.success", argTest, LangArg("time", result.time))
            } else {
                val argReason = LangArg("reason", result.reason!!)
                pack.message(player, "test.failure", argTest, argReason)
            }
            pack.message(player, "test.end")
            return
        } else {
            pack.message(player, "test.description",
                LangArg("test", test.id),
                LangArg("description", test.description)
            )
            return
        }
    }

    private fun onTestsCommand(player: Player) {
        if (!player.hasPermission("langpack.test")) {
            pack.message(player, "permission.deny")
            return
        }

        pack.message(player, "tests.start", LangArg("test_count", tests.size))

        val names = ArrayList<String>(tests.keys)
        if (names.isNotEmpty()) {
            names.sortBy { it }
            for (test in names) {
                pack.message(player, "tests.line", LangArg("test", test))
            }
        }

        pack.message(player, "tests.end")
    }

    private fun addTest(test: LangTest<SpigotLangPack, Player>) {
        tests[test.id] = test
    }

    fun addTest(id: String) {
        addTest(SimpleTest(pack, id))
    }
}
