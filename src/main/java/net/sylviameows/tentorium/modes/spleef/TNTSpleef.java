package net.sylviameows.tentorium.modes.spleef;

import com.fastasyncworldedit.core.function.mask.BlockMaskBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.block.BlockType;
import net.sylviameows.tentorium.TentoriumCore;
import net.sylviameows.tentorium.utilities.Area;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class TNTSpleef extends Spleef {
    private final Location SPAWN_LOCATION = new Location(Bukkit.getWorld("world"), -416.5, 49, -19.5, 90, 0);
    private final Area SPAWN_AREA = new Area(
            new Location(Bukkit.getWorld("world"), -423, 48, -16),
            new Location(Bukkit.getWorld("world"), -414, 53, -24)
    );
    private final int VOID_LEVEL = 10;

    public TNTSpleef() {
        Bukkit.getScheduler().runTaskTimer(TentoriumCore.instance(), this::tick, 1L, 1L);
    }

    private void tick() {
        if (!active) return;
        alive.forEach(player -> {
            var world = player.getWorld();
            var bounding_box = player.getBoundingBox();

            var center = new Location(player.getWorld(), bounding_box.getCenterX(), bounding_box.getMinY(), bounding_box.getCenterZ());
            center.add(0, -0.5, 0);
            var center_block = world.getBlockAt(center);

            if (center_block.getType().hasGravity()) {
                Bukkit.getScheduler().runTaskLater(TentoriumCore.instance(), () -> {
                    center_block.setType(Material.AIR); // break block
                    world.getBlockAt(center_block.getLocation().add(0, -1, 0)).setType(Material.AIR); // break tnt
                }, 4L);
                return;
            }

            // backup detection
            var min = new Location(player.getWorld(), bounding_box.getMinX(), bounding_box.getMinY(), bounding_box.getMinZ());

            Location[] corners = new Location[4];
            corners[0] = min.add(0, -0.5, 0);
            corners[1] = min.clone().add(bounding_box.getWidthX(), 0, 0);
            corners[2] = min.clone().add(0, 0, bounding_box.getWidthZ());
            corners[3] = min.clone().add(bounding_box.getWidthX(), 0, bounding_box.getWidthZ());

            for (Location location : corners) {
                var block = world.getBlockAt(location);

                if (block.getType().hasGravity()) {
                    Bukkit.getScheduler().runTaskLater(TentoriumCore.instance(), () -> {
                        block.setType(Material.AIR); // break block
                        world.getBlockAt(location.add(0, -1, 0)).setType(Material.AIR); // break tnt
                    }, 4L);
                }
            }
        });
    }

    @Override
    public String id() {
        return "tntrun";
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
    protected void refresh() {
        var world = BukkitAdapter.adapt(Bukkit.getWorld("world"));
        try (EditSession session = WorldEdit.getInstance().newEditSessionBuilder().world(world).limitUnlimited().build()) {
            BlockType[] layers = {
                    BlockType.REGISTRY.get("minecraft:red_concrete_powder"),
                    BlockType.REGISTRY.get("minecraft:orange_concrete_powder"),
                    BlockType.REGISTRY.get("minecraft:yellow_concrete_powder"),
                    BlockType.REGISTRY.get("minecraft:lime_concrete_powder"),
                    BlockType.REGISTRY.get("minecraft:light_blue_concrete_powder")
            };
            int gap = 3;

            int y = 46;

            Mask mask = new BlockMaskBuilder().add(BlockType.REGISTRY.get("minecraft:air")).build(session);
            for (int i = 0; i < layers.length; i++) {
                var block_region = new CuboidRegion(
                        BlockVector3.at(-423, y, -32),
                        BlockVector3.at(-447, y, -8)
                );

                var tnt_region = new CuboidRegion(
                        BlockVector3.at(-423, y-1, -32),
                        BlockVector3.at(-447, y-1, -8)
                );

                session.replaceBlocks(tnt_region, mask, BlockType.REGISTRY.get("minecraft:tnt"));
                session.replaceBlocks(block_region, mask, layers[i]);
                y = y - gap - 2 /* adds the tnt and following block we set to make it not wrong or whatever */;
            }
        }
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent event) {
        if (!players.contains(event.getPlayer())) return;

        event.setCancelled(true);
    }
}
