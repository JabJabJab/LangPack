package jab.sledgehammer.langpack.sponge.example.java;

import jab.sledgehammer.langpack.core.objects.LangArg;
import jab.sledgehammer.langpack.sponge.SpongeLangPack;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.PlayerChangeClientSettingsEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

import java.util.HashMap;
import java.util.UUID;

@Plugin(
        id = "langpack_sponge_example_java",
        name = "LangPack_Sponge_Example_Java",
        version = "1.0.0",
        dependencies = {@Dependency(id = "langpack")}
)
public class ExamplePlugin {

    final HashMap<UUID, Boolean> greetMap = new HashMap<>();
    final SpongeLangPack pack = new SpongeLangPack(getClass().getClassLoader());

    @Listener
    public void on(GameInitializationEvent event) {
        pack.append("lang_example_java", true);
    }

    @Listener
    public void on(GameStoppingServerEvent event) {
        greetMap.clear();
    }

    @Listener
    public void on(PlayerChangeClientSettingsEvent event) {
        Player player = event.getTargetEntity();
        UUID playerId = player.getUniqueId();
        if (greetMap.containsKey(playerId)) {
            greetMap.remove(playerId);
            // Delay for one tick to let the server apply the settings changes to the player. -Jab
            Task.Builder tasks = Sponge.getRegistry().createBuilder(Task.Builder.class);
            tasks.delayTicks(1L);
            tasks.execute(() ->
                    pack.broadcast("event.enter_server", new LangArg("player", player.getName()))).submit(this);
        }
    }

    @Listener
    public void on(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        // !!NOTE: The server executes this event prior to the client sending the locale information.
        //         Log the information to be processed only when the client settings are sent. -Jab
        greetMap.put(player.getUniqueId(), true);
    }
}
