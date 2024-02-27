package me.nobeld.mc.noblewhitelist.discord;

import com.alessiodp.libby.BukkitLibraryManager;
import me.nobeld.mc.noblewhitelist.discord.config.ConfigData;
import me.nobeld.mc.noblewhitelist.discord.model.NWLDContainer;
import me.nobeld.mc.noblewhitelist.discord.model.NWLDData;
import me.nobeld.mc.noblewhitelist.util.AdventureUtil;
import me.nobeld.mc.noblewhitelist.util.SpigotMetrics;
import me.nobeld.mc.noblewhitelist.util.UpdateChecker;
import me.nobeld.mc.noblewhitelist.discord.config.MessageData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NWLDiscord extends JavaPlugin implements NWLDData {
    private static NWLDiscord plugin;
    private ConfigData config;
    private MessageData message;
    private JDAManager jdaManager;
    private UpdateChecker checker;
    @Override
    public void onEnable() {
        plugin = this;

        Plugin pl = Bukkit.getPluginManager().getPlugin("NobleWhitelist");
        if (pl == null || !pl.isEnabled()) {
            this.getLogger().log(Level.SEVERE, "The integration cannot be enabled because the base plugin was not found or is disabled.");
            this.getLogger().log(Level.SEVERE, "Download it here: https://modrinth.com/plugin/noble-whitelist");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        NWLDContainer bc = NWLDContainer.builder(this).loadLibs(new BukkitLibraryManager(this), null)
                .loadUpdateChecker("https://api.github.com/repos/NobelD/NobleWhitelist/releases/latest",
                        "NWLDiscord",
                        (a, l) -> {
                            a.sendMessage(AdventureUtil.formatAll("<prefix><#F1B65C>There is a new version available for the <gold>Discord Integration: <#C775FF>" + l));
                            a.sendMessage(AdventureUtil.formatAll("<prefix><#F1B65C>Download it at: <#75CDFF>https://modrinth.com/plugin/noble-whitelist-discord-integration"));
                        })
                .loadFiles()
                .load(() -> Bukkit.getServer().getPluginManager().registerEvents(new Listener(this), this))
                .loadJDA()
                .printMessage()
                .load(() -> {
                    SpigotMetrics metrics = new SpigotMetrics(this, 20417);
                    metrics.addCustomChart(new SpigotMetrics.MultiLineChart("players_and_servers", () -> {
                        Map<String, Integer> valueMap = new HashMap<>();
                        valueMap.put("servers", 1);
                        valueMap.put("players", Bukkit.getOnlinePlayers().size());
                        return valueMap;
                    }));
                })
                .build();

        config = bc.getConfig();
        message = bc.getMessage();
        checker = bc.getUpdate();
        jdaManager = bc.getJDAManager();

        jdaManager.enableCommands();
    }
    @Override
    public void onDisable() {
        NWLDContainer.closeData(this);
    }
    public static void log(Level level, String msg) {
        plugin.getLogger().log(level, msg);
    }
    public static NWLDiscord getPlugin() {
        return plugin;
    }
    @Override
    public ConfigData getConfigD() {
        return config;
    }
    @Override
    public MessageData getMessageD() {
        return message;
    }
    @Override
    public JDAManager getJDAManager() {
        return jdaManager;
    }
    @Override
    public UpdateChecker getUptChecker() {
        return checker;
    }
    @Override
    public String name() {
        return getName();
    }
    @SuppressWarnings("deprecation")
    @Override
    public String version() {
        return getDescription().getVersion();
    }
    @Override
    public Logger logger() {
        return getLogger();
    }
    @Override
    public void disable() {
        Bukkit.getPluginManager().disablePlugin(this);
    }
    @Override
    public void enableMsg(Runnable runnable) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, runnable);
    }
}