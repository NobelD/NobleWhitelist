package me.nobeld.minecraft.noblewhitelist.discord;

import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData;
import me.nobeld.minecraft.noblewhitelist.discord.config.MessageData;
import me.nobeld.minecraft.noblewhitelist.discord.util.LibsManager;
import me.nobeld.minecraft.noblewhitelist.util.Metrics;
import me.nobeld.minecraft.noblewhitelist.util.ServerUtil;
import me.nobeld.minecraft.noblewhitelist.util.UpdateChecker;
import net.byteflux.libby.BukkitLibraryManager;
import net.essentialsx.discord.EssentialsDiscord;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class NWLDiscord extends JavaPlugin {
    private static NWLDiscord plugin;
    private JDAManager jdaManager;
    private JavaPlugin essentials;
    private UpdateChecker checker;
    @Override
    public void onEnable() {
        plugin = this;
        new LibsManager(new BukkitLibraryManager(this));

        ConfigData.configFile();
        MessageData.messageFile();

        Plugin pl = Bukkit.getPluginManager().getPlugin("NobleWhitelist");
        if (pl == null || !pl.isEnabled()) {
            this.getLogger().log(Level.SEVERE, "Can not enable the plugin because the base plugin is not installed.");
            this.getLogger().log(Level.SEVERE, "Download it here: https://github.com/NobelD/NobleWhitelist/releases");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Plugin ess = Bukkit.getPluginManager().getPlugin("EssentialsDiscord");
        if (ess != null && ess.isEnabled() && ConfigData.get(ConfigData.essentialsIntegration)) {
            essentials = (JavaPlugin) ess;
        }
        jdaManager = new JDAManager(this);
        checker = new UpdateChecker(this);
        Bukkit.getServer().getPluginManager().registerEvents(new Listener(this), this);

        NobleWhitelist.getPlugin().consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><green>Loaded Discord integration!"));

        if (getUptChecker().canUpdate("NWLDiscord", ConfigData.get(ConfigData.notifyUpdate), false)) {
            NobleWhitelist.getPlugin().consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><#F1B65C>There is a new version available for the <gold>Discord Integration: <#C775FF>" + checker.getLatest()));
            NobleWhitelist.getPlugin().consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><#F1B65C>Download it at: <#75CDFF>https://www.github.com/NobelD/NobleWhitelist/releases"));
        }
        Metrics metrics = new Metrics(this, 20417);
        metrics.addCustomChart(new Metrics.MultiLineChart("players_and_servers", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            return valueMap;
        }));
    }
    @Override
    public void onDisable() {
        if (jdaManager != null) jdaManager.disable();
    }
    public JDAManager getJDAManager() {
        return jdaManager;
    }
    public UpdateChecker getUptChecker() {
        return checker;
    }
    public static void log(Level level, String msg) {
        plugin.getLogger().log(level, msg);
    }
    public static NWLDiscord getPlugin() {
        return plugin;
    }
    public boolean hasEssentials() {
        //return essentials != null;
        return false;
    }
    public EssentialsDiscord getEssentials() {
        return (EssentialsDiscord) essentials;
    }
}