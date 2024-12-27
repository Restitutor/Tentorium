package net.sylviameows.tentorium.modes.spleef;

import com.fastasyncworldedit.core.function.mask.BlockMaskBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.block.BlockType;
import net.kyori.adventure.text.Component;
import net.sylviameows.tentorium.utilities.Area;
import net.sylviameows.tentorium.utilities.ItemUtilities;
import net.sylviameows.tentorium.utilities.Palette;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class ClassicSpleef extends Spleef {
    private final Location SPAWN_LOCATION = new Location(Bukkit.getWorld("world"), -327.5, 64, -118.5, -90, 0);
    private final Area SPAWN_AREA = new Area(
            new Location(Bukkit.getWorld("world"), -322, 63, -123),
            new Location(Bukkit.getWorld("world"), -331, 69, -115)
    );
    private final int VOID_LEVEL = 20;

    @Override
    public Component name() {
        return super.name().color(Palette.AQUA);
    }

    @Override
    public String id() {
        return "spleef";
    }

    @Override
    protected Location spawn() {
        return SPAWN_LOCATION;
    }

    @Override
    protected Area lobby() {
        return SPAWN_AREA;
    }

    @Override
    protected int voidLevel() {
        return VOID_LEVEL;
    }

    @Override
    protected void setupPlayer(Player player) {
        var inventory = player.getInventory();
        inventory.clear();
        inventory.addItem(ItemUtilities.createItem(Material.DIAMOND_SHOVEL, meta -> meta.addEnchant(Enchantment.EFFICIENCY, 5, true)));
    }

    @Override
    protected void refresh() {
        var world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
        try (EditSession session = WorldEdit.getInstance().newEditSessionBuilder().world(world).limitUnlimited().build()) {
            int layers = 6;
            int gap = 3;

            int y = 60;

            Mask mask = new BlockMaskBuilder().add(BlockType.REGISTRY.get("minecraft:air")).build(session);
            for (int i = 0; i < layers; i++) {
                var region = new CuboidRegion(
                        BlockVector3.at(-322, y, -107),
                        BlockVector3.at(-298, y, -131)
                );

                session.replaceBlocks(region, mask, BlockType.REGISTRY.get("minecraft:snow_block"));
                y = y - gap - 1 /* adds the one block we set to make it not wrong or whatever */;
            }
        }
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent event) {
        var player = event.getPlayer();
        if (!active && players.contains(player)) {
            event.setCancelled(true);
        } else if (players.contains(player) && !alive.contains(player)) {
            event.setCancelled(true);
        } else if (alive.contains(player) && event.getBlock().getType() != Material.SNOW_BLOCK) {
            event.setCancelled(true);
        }
    }

    @Override
    protected void winner(Player player) {
        var uuid = player.getUniqueId().toString();
        var score = database().fetchInt(uuid, "spleef_wins");
        database().update(uuid, "spleef_wins", score+1);
    }

    @Override
    public String leaderboardStatId() {
        return "spleef_wins";
    }
}
