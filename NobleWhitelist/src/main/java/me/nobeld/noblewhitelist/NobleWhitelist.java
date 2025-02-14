package me.nobeld.noblewhitelist;

import com.alessiodp.libby.BukkitLibraryManager;
import me.nobeld.noblewhitelist.api.NWLMiniExpansion;
import me.nobeld.noblewhitelist.api.NWLPAPIExpansion;
import me.nobeld.noblewhitelist.api.NobleWhitelistApi;
import me.nobeld.noblewhitelist.command.NWlCommand;
import me.nobeld.noblewhitelist.config.ConfigData;
import me.nobeld.noblewhitelist.config.FileManager;
import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.logic.StorageLoader;
import me.nobeld.noblewhitelist.logic.WhitelistChecker;
import me.nobeld.noblewhitelist.logic.WhitelistData;
import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.model.base.AdvPlatformManager;
import me.nobeld.noblewhitelist.model.base.NWLContainer;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.model.storage.DataGetter;
import me.nobeld.noblewhitelist.model.storage.StorageType;
import me.nobeld.noblewhitelist.storage.root.DatabaseSQL;
import me.nobeld.noblewhitelist.util.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NobleWhitelist extends JavaPlugin implements NWLData {
    private static NobleWhitelist plugin;
    private static boolean hasPaper;
    private NWlCommand commands;
    private WhitelistData whitelistData;
    private WhitelistChecker whitelistChecker;
    private UpdateChecker uptChecker;
    private NobleWhitelistApi api;
    private DataGetter storage;
    private StorageType storageType = StorageType.NONE;
    private ConfigData configData;
    private MessageData messageData;
    private static BukkitAdventure adventure = null;
    private boolean blocked = false;
    @Override
    public void onEnable() {
        // #TODO fix static classes and adventure
        plugin = this;
        if (!ServerUtil.canRun(this)) return;
        hasPaper = ServerUtil.hasPaper();
        NWLContainer bc = NWLContainer.builder(this)
                .loadLibs(new BukkitLibraryManager(this), null)
                .loadFiles(getDataFolder().getPath(), PairData.of("config.yml", FileManager.FileType.YAML))
                .loadAdventure()
                .loadUpdateChecker("NobleWhitelist", "spigot", ServerUtil.getVersion() > 17 ? null : "spigot-mc17")
                .load(() -> {
                    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                        new NWLPAPIExpansion(this).register();
                    }
                    if (Bukkit.getPluginManager().getPlugin("MiniPlaceholders") != null) {
                        new NWLMiniExpansion(this);
                    }
                })
                .loadStorage()
                .loadData()
                .load(this::loadExtra)
                .printMessage()
                .load(() -> {
                    SpigotMetrics metrics = new SpigotMetrics(this, 20050);
                    metrics.addCustomChart(new SpigotMetrics.MultiLineChart("players_and_servers", () -> {
                        Map<String, Integer> valueMap = new HashMap<>();
                        valueMap.put("servers", 1);
                        valueMap.put("players", Bukkit.getOnlinePlayers().size());
                        return valueMap;
                    }));
                    metrics.addCustomChart(new SpigotMetrics.SimplePie("storage_type", () -> storageType.getName()));
                })
                .build();

        this.configData = bc.getConfig();
        this.messageData = bc.getMessage();
        this.uptChecker = bc.getUpdate();
        this.storage = bc.getStorage();
        this.storageType = bc.getType();
        this.whitelistData = bc.getWlData();
        this.whitelistChecker = bc.getWlChecker();
    }
    private void loadExtra() {
        this.api = new NobleWhitelistApi(this);
        Bukkit.getServer().getPluginManager().registerEvents(new Listener(this), this);
        try {
            this.commands = new NWlCommand(this);
        } catch (Throwable e) {
            logger().log(Level.SEVERE, "Cannot load the commands constructor, no commands will be available.\nConsider to update otherwise report this problem.", e);
        }
    }
    @Override
    public void onDisable() {
        NWLContainer.closeData(this);
    }
    @Override
    public void reloadDataBase() {
        if (storage != null && storageType.isDatabase()) ((DatabaseSQL) storage).close();
        PairData<DataGetter, StorageType> st = StorageLoader.setupStorage(this, configData);
        this.storage = st.getFirst();
        this.storageType = st.getSecond();
    }
    @Override
    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
    @Override
    public boolean isBlocked() {
        return blocked;
    }
    public static NobleWhitelist getPlugin() {
        return plugin;
    }
    @Override
    public NobleWhitelistApi getApi() {
        return this.api;
    }
    public static BukkitAdventure adv() {
        return adventure;
    }
    public NWlCommand getCommand() {
        return commands;
    }
    public static void log(Level level, String msg) {
        plugin.getLogger().log(level, msg);
    }
    public static void log(Level level, String msg, Exception ex) {
        plugin.getLogger().log(level, msg, ex);
    }
    public static boolean hasPaper() {
        return hasPaper;
    }
    @Override
    public StorageType getStorageType() {
        return this.storageType;
    }
    @Override
    public DataGetter getStorage() {
        return this.storage;
    }
    @Override
    public AdvPlatformManager getAdventure() {
        if (adventure == null) {
            adventure = new BukkitAdventure(this);
            adventure.createAdventure();
        }
        return adventure;
    }
    @Override
    public ConfigData getConfigD() {
        return configData;
    }
    @Override
    public MessageData getMessageD() {
        return messageData;
    }
    @Override
    public WhitelistData whitelistData() {
        return this.whitelistData;
    }
    @Override
    public WhitelistChecker whitelistChecker() {
        return this.whitelistChecker;
    }
    @Override
    public UpdateChecker getUptChecker() {
        return this.uptChecker;
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
    public void disable() {
        Bukkit.getPluginManager().disablePlugin(this);
    }
    @Override
    public void closeServer() {
        Bukkit.getServer().shutdown();
    }
    @Override
    public void runCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
    @Override
    public Logger logger() {
        return getLogger();
    }
}