package com.asledgehammer.langpack.bungeecord.example.java;

import com.asledgehammer.langpack.bungeecord.BungeeLangPack;
import com.asledgehammer.langpack.core.objects.LangArg;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.SettingsChangedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Jab
 */
public class ExamplePlugin extends Plugin implements Listener {

    private final HashMap<UUID, Boolean> greetList = new HashMap<>();
    private final BungeeLangPack pack = new BungeeLangPack(this.getClass().getClassLoader());

    @Override
    public void onEnable() {
        pack.append("lang_example_java", true, true);
        ProxyServer.getInstance().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void on(SettingsChangedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (greetList.containsKey(playerId)) {
            greetList.remove(playerId);

            // Delay for one tick to let the server apply the settings changes to the player. -Jab
            ProxyServer.getInstance().getScheduler().schedule(this,
                    () -> pack.broadcast("event.connect", new LangArg("player", player.getName()))
                    , 1L, TimeUnit.SECONDS);
        }
    }

    @EventHandler
    public void on(PostLoginEvent event) {
        // !!NOTE: The server executes this event prior to the client sending the locale information.
        //         Log the information to be processed only when the client settings are sent. -Jab
        greetList.put(event.getPlayer().getUniqueId(), true);
    }

    @EventHandler
    public void on(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (greetList.containsKey(playerId)) {
            greetList.remove(player.getUniqueId());
            return;
        }

        pack.broadcast("event.disconnect", new LangArg("player", player.getName()));
    }
}
