package me.nobeld.noblewhitelist.discord.temp;

import me.nobeld.noblewhitelist.temp.SchedulerUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class FoliaLoad implements ServerLoadDelegator, Listener {
    private Map<JavaPlugin, List<Runnable>> runnables;
    private boolean loaded = false;
    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        if (event.getType() == ServerLoadEvent.LoadType.STARTUP) {
            if (!loaded) {
                loaded = true;
                if (runnables != null) {
                    for (Map.Entry<JavaPlugin, List<Runnable>> e : runnables.entrySet()) {
                        Executor executor = SchedulerUtil.mainExecutor(e.getKey());
                        e.getValue().forEach(executor::execute);
                    }
                }
            }
        }

    }
    @Override
    public void delegate(JavaPlugin plugin, Runnable runnable) {
        if (loaded) {
            SchedulerUtil.mainExecutor(plugin).execute(runnable);
        } else {
            if (runnables == null) {
                runnables = new HashMap<>();
            }
            runnables.compute(plugin, (k, v) -> {
                if (v == null) {
                    v = new ArrayList<>();
                }
                v.add(runnable);
                return v;
            });
        }
    }
    @Override
    public void delegateAsync(JavaPlugin plugin, Runnable runnable) {
        if (loaded) {
            SchedulerUtil.asyncExecutor(plugin).execute(runnable);
        } else {
            if (runnables == null) {
                runnables = new HashMap<>();
            }
            runnables.compute(plugin, (k, v) -> {
                if (v == null) {
                    v = new ArrayList<>();
                }
                v.add(() -> SchedulerUtil.asyncExecutor(plugin).execute(runnable));
                return v;
            });
        }
    }
}
