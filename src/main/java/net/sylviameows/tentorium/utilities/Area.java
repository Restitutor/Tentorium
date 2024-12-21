package net.sylviameows.tentorium.utilities;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class Area {
    private Location a;
    private Location b;

    public Area(Location a, Location b) {
        this.a = a;
        this.b = b;
    }

    public boolean contains(Location location) {
        var min_x = Math.min(a.x(), b.x());
        var max_x = Math.max(a.x(), b.x());

        if (location.x() > max_x || location.x() < min_x) return false;

        var min_y = Math.min(a.y(), b.y());
        var max_y = Math.max(a.y(), b.y());

        if (location.y() > max_y || location.y() < min_y) return false;

        var min_z = Math.min(a.z(), b.z());
        var max_z = Math.max(a.z(), b.z());

        return !(location.z() > max_z) && !(location.z() < min_z);
    }

    public void forEach(Consumer<Block> consumer) {
        var min_x = Math.min(a.getBlockX(), b.getBlockX());
        var max_x = Math.max(a.getBlockX(), b.getBlockX());
        var min_y = Math.min(a.getBlockY(), b.getBlockY());
        var max_y = Math.max(a.getBlockY(), b.getBlockY());
        var min_z = Math.min(a.getBlockZ(), b.getBlockZ());
        var max_z = Math.max(a.getBlockZ(), b.getBlockZ());

        var world = a.getWorld();
        for (int x = min_x; x < max_x; x++) {
            for (int y = min_y; x < max_y; y++) {
                for (int z = min_z; x < max_z; x++) {
                    var block = world.getBlockAt(x, y, z);
                    consumer.accept(block);
                }
            }
        }
    }

    public void shift(Vector vector) {
        a.add(vector);
        b.add(vector);
    }


    public Location a() {
        return a;
    }
    public void a(Location location) {
        this.a = location;
    }

    public Location b() {
        return b;
    }
    public void b(Location location) {
        this.b = location;
    }
}
