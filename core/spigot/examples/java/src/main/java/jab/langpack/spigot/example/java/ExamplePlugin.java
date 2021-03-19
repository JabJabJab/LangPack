package jab.langpack.spigot.example.java;

import jab.langpack.core.objects.LangArg;
import jab.langpack.spigot.SpigotLangPack;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin implements Listener {

    private SpigotLangPack pack;
    private boolean joinMsg = false;
    private boolean leaveMsg = false;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        FileConfiguration cfg = getConfig();
        if (cfg.contains("join_messages") && cfg.isBoolean("join_messages")) {
            joinMsg = cfg.getBoolean("join_messages");
        }
        if (cfg.contains("leave_messages") && cfg.isBoolean("leave_messages")) {
            leaveMsg = cfg.getBoolean("leave_messages");
        }

        pack = new SpigotLangPack(getClassLoader());
        pack.append("lang_example_java", true, true);

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {

        if (!joinMsg) {
            return;
        }

        // !!NOTE: The server executes this event prior to the client sending the locale information. Slightly delay
        // any join event if using LangPack for the player. - Jab
        getServer().getScheduler().runTaskLater(this, () -> pack.broadcast("event.enter_server", new LangArg("player", event.getPlayer().getDisplayName())), 20L);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {

        if (!leaveMsg) {
            return;
        }

        Player player = event.getPlayer();
        pack.broadcast("event.leave_server", new LangArg("player", player.getDisplayName()));
    }
}
