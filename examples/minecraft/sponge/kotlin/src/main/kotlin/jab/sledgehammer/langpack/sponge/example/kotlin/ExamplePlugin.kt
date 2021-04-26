package jab.sledgehammer.langpack.sponge.example.kotlin

import jab.sledgehammer.langpack.core.objects.LangArg
import jab.sledgehammer.langpack.sponge.SpongeLangPack
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.living.humanoid.player.PlayerChangeClientSettingsEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.game.state.GameStoppingServerEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.plugin.Dependency
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.scheduler.Task
import java.util.*

/**
 * **ExamplePlugin** TODO: Document.
 *
 * @author Jab
 */
@Plugin(
    id = "langpack_sponge_example_kotlin",
    name = "LangPack_Sponge_Example_Kotlin",
    version = "1.0.0",
    dependencies = [Dependency(id = "langpack")]
)
class ExamplePlugin {

    private val greetMap = HashMap<UUID, Boolean>()
    private val pack = SpongeLangPack(this::class.java.classLoader)

    @Listener
    fun on(event: GameInitializationEvent) {
        pack.append("lang_example_kotlin", true)
    }

    @Listener
    fun on(event: GameStoppingServerEvent) {
        greetMap.clear()
    }

    @Listener
    fun on(event: PlayerChangeClientSettingsEvent) {
        val player = event.targetEntity
        val playerId = player.uniqueId
        if (greetMap.containsKey(playerId)) {
            greetMap.remove(playerId)
            // Delay for one tick to let the server apply the settings changes to the player. -Jab
            val tasks = Sponge.getRegistry().createBuilder(Task.Builder::class.java)
            tasks.delayTicks(1L)
            tasks.execute(Runnable {
                pack.broadcast("event.enter_server", LangArg("player", player.name))
            }).submit(this)
        }
    }

    @Listener
    fun on(event: ClientConnectionEvent.Join) {
        event.isMessageCancelled = true
        val player = event.targetEntity
        // The server executes this event prior to the client sending the locale information. Log the information to be
        // processed only when the client settings are sent. -Jab
        greetMap[player.uniqueId] = true
    }

    @Listener
    fun on(event: ClientConnectionEvent.Disconnect) {
        event.isMessageCancelled = true
        val player = event.targetEntity
        val playerId = player.uniqueId
        if (greetMap.containsKey(playerId)) {
            greetMap.remove(playerId)
            return
        }
        pack.broadcast("event.leave_server", LangArg("player", player.name))
    }
}
