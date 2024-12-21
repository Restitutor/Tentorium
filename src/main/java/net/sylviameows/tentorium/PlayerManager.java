package net.sylviameows.tentorium;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class PlayerManager implements Listener {
    private static final HashMap<Player, TentoriumPlayer> players = new HashMap<>();

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        players.put(player, new TentoriumPlayer(player));
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        var player = players.get(event.getPlayer());

        if (player.mode() != null) player.mode().leave(player.parent());
        players.remove(player.parent());
    }

    public static TentoriumPlayer get(Player player) {
        return players.get(player);
    }
}
