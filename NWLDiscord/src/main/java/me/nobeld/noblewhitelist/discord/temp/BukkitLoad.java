package me.nobeld.noblewhitelist.discord.temp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitLoad implements ServerLoadDelegator {
    @Override
    public void delegate(JavaPlugin plugin, Runnable runnable) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable);
    }
    @Override
    public void delegateAsync(JavaPlugin plugin, Runnable runnable) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable));
    }
}
