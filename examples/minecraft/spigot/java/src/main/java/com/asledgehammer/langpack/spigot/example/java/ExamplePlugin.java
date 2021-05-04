package com.asledgehammer.langpack.spigot.example.java;

import com.asledgehammer.langpack.core.objects.LangArg;
import com.asledgehammer.langpack.spigot.SpigotLangPack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Jab
 */
public class ExamplePlugin extends JavaPlugin implements Listener {

    private final HashMap<UUID, Boolean> greetList = new HashMap<>();
    private final SpigotLangPack pack = new SpigotLangPack(getClassLoader());

    @Override
    public void onEnable() {
        pack.append("lang_example_java", true, true);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        greetList.clear();
    }

    @EventHandler
    public void on(PlayerLocaleChangeEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        if (greetList.containsKey(playerId)) {
            String playerName = player.getDisplayName();
            greetList.remove(playerId);
            // Delay for one tick to let the server apply the settings changes to the player. -Jab
            getServer().getScheduler().runTaskLater(this,
                    () -> pack.broadcast("event.enter_server", new LangArg("player", playerName)), 1L);
        }
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        // The server executes this event prior to the client sending the locale information. Log the information to be
        // processed only when the client settings are sent. -Jab
        greetList.put(event.getPlayer().getUniqueId(), true);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        if (greetList.containsKey(playerId)) {
            greetList.remove(playerId);
            return;
        }
        pack.broadcast("event.leave_server", new LangArg("player", player.getDisplayName()));
    }
}
