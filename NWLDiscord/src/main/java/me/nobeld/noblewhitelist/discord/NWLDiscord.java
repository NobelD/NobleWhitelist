package me.nobeld.noblewhitelist.discord;

import com.alessiodp.libby.BukkitLibraryManager;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.config.FileManager;
import me.nobeld.noblewhitelist.discord.config.ConfigData;
import me.nobeld.noblewhitelist.discord.model.NWLDContainer;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.util.ServerUtil;
import me.nobeld.noblewhitelist.util.SpigotMetrics;
import me.nobeld.noblewhitelist.util.UpdateChecker;
import me.nobeld.noblewhitelist.discord.config.MessageData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NWLDiscord extends JavaPlugin implements NWLDsData {
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
                .loadFiles(getDataFolder().getPath(), PairData.of("config.yml", FileManager.FileType.YAML), PairData.of("messages.yml", FileManager.FileType.YAML))
                .load(() -> Bukkit.getServer().getPluginManager().registerEvents(new Listener(this), this))
                .loadJDA()
                .loadUpdateChecker("NWLDiscord", "spigot", ServerUtil.getVersion() > 17 ? Runtime.version().feature() >= 21 ? null : "spigot-j17" : "spigot-mc17")
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
    public NWLData getNWL() {
        return NobleWhitelist.getPlugin();
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
    @Override
    public InputStream resourceStream(String name) {
        return getClassLoader().getResourceAsStream(name);
    }
}