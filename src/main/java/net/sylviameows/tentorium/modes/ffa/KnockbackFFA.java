package net.sylviameows.tentorium.modes.ffa;

import net.sylviameows.tentorium.utilities.Area;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class KnockbackFFA extends FFA {
    private final Material WEAPON_MATERIAL = Material.STICK;

    @Override
    protected int voidLevel() {
        return 54;
    }

    private final Location SPAWN_LOCATION = new Location(Bukkit.getWorld("world"), 20.5, 114, -580.5, 0, 0);
    private final Area SPAWN_AREA = new Area(
            new Location(Bukkit.getWorld("world"), 24, 110, -533),
            new Location(Bukkit.getWorld("world"), 16, 120, -587)
    );

    @Override
    protected void respawn(Player player) {
        super.respawn(player);

        player.teleportAsync(SPAWN_LOCATION);
        player.clearActivePotionEffects();
//        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 4 * 20, 0, true, false, true));
    }

    @Override
    public String id() {
        return "knockback";
    }

    @Override
    public void join(Player player) {
        super.join(player);
        respawn(player);
    }

    @Override
    void giveKit(Player player) {
        var inventory = player.getInventory();
        inventory.clear();

        var item = new ItemStack(WEAPON_MATERIAL, 1);
        item.editMeta(meta -> {
            meta.setUnbreakable(true);
            meta.setFood(null);
            meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        });


        inventory.setItem(0, item);
    }

    @EventHandler
    public void damage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && players.contains(player)) {
            if (SPAWN_AREA.contains(player.getLocation()) || event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
                return;
            }


            if (event.getDamageSource().getDamageType() == DamageType.GENERIC_KILL) return;
            if (player.getLocation().y() <= voidLevel()) {
                return;
            }
            event.setDamage(0.0D);
        }
    }

    @Override
    protected void rewardKiller(Player killer) {
        super.rewardKiller(killer);

        var uuid = killer.getUniqueId().toString();
        var score = database().fetchInt(uuid, "kb_kills");
        database().update(uuid, "kb_kills", score+1);
    }
}
