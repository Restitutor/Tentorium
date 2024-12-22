package net.sylviameows.tentorium;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.sylviameows.tentorium.commands.*;
import net.sylviameows.tentorium.database.SQLite;
import net.sylviameows.tentorium.modes.Mode;
import net.sylviameows.tentorium.modes.Parkour;
import net.sylviameows.tentorium.modes.ffa.KitFFA;
import net.sylviameows.tentorium.modes.ffa.KnockbackFFA;
import net.sylviameows.tentorium.modes.spleef.ClassicSpleef;
import net.sylviameows.tentorium.modes.spleef.TNTSpleef;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public class TentoriumCore extends JavaPlugin {
    private static ComponentLogger LOGGER;
    private static TentoriumCore INSTANCE;

    public static ComponentLogger logger() {
        return LOGGER;
    }
    public static TentoriumCore instance() {
        return INSTANCE;
    }

    public static NamespacedKey identififer(String value) {
        return new NamespacedKey("tentorium", value);
    }

    public static HashMap<String, Mode> modes = new HashMap<>();

    private static SQLite DATABASE;

    public static SQLite database() {
        return DATABASE;
    }

    @Override
    public void onEnable() {
        TentoriumCore.INSTANCE = this;
        TentoriumCore.LOGGER = getComponentLogger();

        logger().info("Connecting to database...");
        DATABASE = new SQLite(this);
        DATABASE.load();

        logger().info("Registering events...");
        Listener[] events = {
                new PlayerManager(),
                new InventoryListener(this),
                new SpawnListener()
        };

        int events_loaded = 0;
        var pm = Bukkit.getPluginManager();
        for (Listener listener : events) {
            pm.registerEvents(listener, this);
            events_loaded++;
        }

        logger().info("Loaded "+events_loaded+" event(s)!");

        logger().info("Loading gamemodes...");

        Mode[] modes = {
                new KnockbackFFA(),
                new KitFFA(),
                new ClassicSpleef(),
                new TNTSpleef(),
                new Parkour()
        };

        int modes_loaded = 0;
        for (Mode mode : modes) {
            TentoriumCore.modes.put(mode.id(), mode);
            pm.registerEvents(mode, this);
            modes_loaded++;
        }

        logger().info("Loaded "+modes_loaded+" gamemodes(s)!");

        var lem = this.getLifecycleManager();
        lem.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("join", "join a gamemode", List.of("j", "mode"), new JoinCommand());
            commands.register("leave", "leave the game you're in", List.of("spawn", "lobby", "hub", "l"), new LeaveCommand());

            commands.register("kit", "select a kit of your choice", List.of("kits"), new KitCommand());
            commands.register("bypass", "bypass spawn protections", new BypassCommand());

            commands.register("select", "select a map", new SelectCommand());

            for (Mode mode : modes) {
                commands.register(mode.id(), new ModeAlias(mode.id()));
            }
        });

        logger().info("Plugin ready for use!");
    }

    @Override
    public void onDisable() {

    }
}
