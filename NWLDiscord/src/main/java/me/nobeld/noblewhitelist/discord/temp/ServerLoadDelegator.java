package me.nobeld.noblewhitelist.discord.temp;

import org.bukkit.plugin.java.JavaPlugin;

public interface ServerLoadDelegator {
    void delegate(JavaPlugin plugin, Runnable runnable);

    void delegateAsync(JavaPlugin plugin, Runnable runnable);
}
