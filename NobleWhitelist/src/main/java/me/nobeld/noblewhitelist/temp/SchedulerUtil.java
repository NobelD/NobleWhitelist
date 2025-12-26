package me.nobeld.noblewhitelist.temp;

import me.nobeld.noblewhitelist.util.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executor;

public class SchedulerUtil {
    private static boolean isAllowed() {
        return ServerUtil.hasPaper() && ServerUtil.matchVersion(20, 1);
    }

    public static Executor asyncExecutor(JavaPlugin plugin) {
        if (isAllowed()) {
            return t -> Bukkit.getAsyncScheduler().runNow(plugin, s -> t.run());
        } else {
            return t -> Bukkit.getScheduler().runTaskAsynchronously(plugin, t);
        }
    }

    public static Executor mainExecutor(JavaPlugin plugin) {
        if (isAllowed()) {
            return t -> Bukkit.getGlobalRegionScheduler().run(plugin, s -> t.run());
        } else {
            return t -> Bukkit.getScheduler().runTask(plugin, t);
        }
    }

    public static Executor entityExecutor(JavaPlugin plugin, Entity entity) {
        if (isAllowed()) {
            return t -> entity.getScheduler().run(plugin, s -> t.run(), null);
        } else {
            return t -> Bukkit.getScheduler().runTask(plugin, t);
        }
    }

    public static Executor regionExecutor(JavaPlugin plugin, Location location) {
        if (isAllowed()) {
            return t -> Bukkit.getRegionScheduler().run(plugin, location, s -> t.run());
        } else {
            return t -> Bukkit.getScheduler().runTask(plugin, t);
        }
    }
}
