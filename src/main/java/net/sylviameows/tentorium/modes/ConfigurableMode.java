package net.sylviameows.tentorium.modes;

import net.sylviameows.tentorium.config.serializable.ModeConfig;
import net.sylviameows.tentorium.utilities.Area;
import org.bukkit.Location;

public interface ConfigurableMode {
    ModeConfig options();
    default Location spawn() {
        return options().location();
    }
    default Area lobby() {
        return options().region();
    }
    default int voidLevel() {
        return options().voidLevel();
    }

    void reload();
}
