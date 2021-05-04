package com.asledgehammer.langpack.bungeecord

import com.asledgehammer.langpack.bungeecord.test.InvokeActionTest
import com.asledgehammer.langpack.bungeecord.test.InvokePoolTest
import com.asledgehammer.langpack.bungeecord.test.ResolveFieldTest
import com.asledgehammer.langpack.bungeecord.test.SimpleTest
import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.test.LangTest
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.TabExecutor

internal class LangCommand(private val plugin: LangPlugin) : Command("blang"), TabExecutor {

    private val tests = HashMap<String, LangTest<BungeeLangPack, ProxiedPlayer>>()
    private val emptyList = ArrayList<String>()
    private val subCommands = ArrayList<String>()
    private val cache = BungeeLangCache(plugin.pack)
    private val pack = plugin.pack

    override fun execute(player: CommandSender?, args: Array<out String>?) {
        if (player == null || player !is ProxiedPlayer) return

        if (args == null || args.isEmpty()) {
            pack.message(player, "command.help")
            return
        }

        // Scan for sub-commands.
        if (args.isNotEmpty()) {
            when (args[0].toLowerCase()) {
                "test" -> {
                    onTestCommand(player, args)
                    return
                }
                "tests" -> {
                    onTestsCommand(player)
                    return
                }
            }
            // Let the player know the command is invalid before sending the help dialog.
            pack.message(player, "command.not_found", LangArg("command", args[0]))
        }

        // Display help message if no command is found.
        pack.message(player, "command.help")
    }

    private fun onTestCommand(player: ProxiedPlayer, args: Array<out String>) {
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

        if (args.size == 3) {
            if (!args[2].equals("run", true)) {
                pack.message(player, "test.help")
                return
            }
            if (testName.equals("all", true)) {
                val names = ArrayList<String>(tests.keys)
                if (names.isNotEmpty()) {
                    names.sortBy { it }
                    for (test in names) runTest(player, test)
                }
            } else {
                runTest(player, testName)
            }
            return
        } else {
            val test = tests[testName]
            if (test == null) {
                pack.message(player, "test.not_found", LangArg("test", testName))
                return
            }
            pack.message(player, "test.description",
                LangArg("test", test.id),
                LangArg("description", test.description)
            )
            return
        }
    }

    private fun runTest(player: ProxiedPlayer, name: String) {
        val argTest = LangArg("test", name)

        // Make sure the test exists.
        val test = tests[name]
        if (test == null) {
            pack.message(player, "test.not_found", argTest)
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
    }

    private fun onTestsCommand(player: ProxiedPlayer) {
        if (!player.hasPermission("langpack.test")) {
            pack.message(player, "permission.deny")
            return
        }

        // Make sure that 'tests_enabled' is set to true in the global config.
        if (!plugin.testsEnabled) {
            pack.message(player, "test.disabled")
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

    private fun addTest(test: LangTest<BungeeLangPack, ProxiedPlayer>) {
        tests[test.id] = test
    }

    private fun addTest(id: String) {
        addTest(SimpleTest(pack, id))
    }

    override fun onTabComplete(player: CommandSender?, args: Array<out String>?): MutableIterable<String> {
        if (player !is ProxiedPlayer) return subCommands
        if (args == null || args.isEmpty()) {
            pack.message(player, "command.help")
            return emptyList
        }

        val lang = cache.getLanguage(player)
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

    init {
        if (plugin.testsEnabled) {
            subCommands.add("test")
            subCommands.add("tests")
            addTest("basic")
            addTest("multiline")
            addTest("placeholder")
            addTest("visibility_scope")
            addTest(ResolveFieldTest(pack))
            addTest("broadcast")
            addTest("action")
            addTest("pool")
            addTest(InvokeActionTest(pack))
            addTest(InvokePoolTest(pack))
        }
    }
}
