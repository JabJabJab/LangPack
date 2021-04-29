package com.asledgehammer.langpack.sponge

import com.asledgehammer.langpack.core.objects.LangArg
import com.asledgehammer.langpack.core.test.LangTest
import com.asledgehammer.langpack.sponge.test.InvokeActionTest
import com.asledgehammer.langpack.sponge.test.InvokePoolTest
import com.asledgehammer.langpack.sponge.test.ResolveFieldTest
import com.asledgehammer.langpack.sponge.test.SimpleTest
import org.spongepowered.api.command.CommandCallable
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*

/**
 * **LangCommand** handles all command executions under the *lang* command.
 *
 * @author Jab
 *
 * @property plugin The plugin instance.
 */
internal class LangCommand(private val plugin: LangPlugin) : CommandCallable {

    private val tests = HashMap<String, LangTest<SpongeLangPack, Player>>()
    private val subCommands = ArrayList<String>()
    private val pack = plugin.pack
    private val desc: Optional<Text> = Optional.of(Text.of("Used to test the LangPack API"))

    override fun process(source: CommandSource, argsString: String): CommandResult {
        if (source !is Player) return CommandResult.success()

        val player: Player = source
        var found = false

        // If no sub-command is used.
        if (argsString.isEmpty()) {
            pack.message(player, "command.help")
            return CommandResult.success()
        }

        val args = argsString.split(" ", "\t")

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

        return CommandResult.success()
    }

    override fun getSuggestions(
        source: CommandSource,
        arguments: String,
        targetPosition: Location<World>?,
    ): MutableList<String> = Collections.emptyList()

    override fun testPermission(source: CommandSource): Boolean = source.hasPermission("langpack.lang")
    override fun getShortDescription(source: CommandSource): Optional<Text> = desc
    override fun getHelp(source: CommandSource): Optional<Text> = Optional.of(Text.of(""))
    override fun getUsage(source: CommandSource): Text = Text.of("")

    private fun onTestCommand(player: Player, args: List<String>) {

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

        val names = ArrayList(tests.keys)
        if (names.isNotEmpty()) {
            names.sortBy { it }
            for (test in names) {
                pack.message(player, "tests.line", LangArg("test", test))
            }
        }

        pack.message(player, "tests.end")
    }

    private fun addTest(test: LangTest<SpongeLangPack, Player>) {
        tests[test.id] = test
    }

    private fun addTest(id: String) {
        addTest(SimpleTest(pack, id))
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
