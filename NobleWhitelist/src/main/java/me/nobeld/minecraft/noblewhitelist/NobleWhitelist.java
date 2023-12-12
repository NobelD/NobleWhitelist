package me.nobeld.minecraft.noblewhitelist;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import me.nobeld.minecraft.noblewhitelist.api.NWLMiniExpansion;
import me.nobeld.minecraft.noblewhitelist.api.NWLPAPIExpansion;
import me.nobeld.minecraft.noblewhitelist.api.NobleWhitelistApi;
import me.nobeld.minecraft.noblewhitelist.command.NWlCommand;
import me.nobeld.minecraft.noblewhitelist.config.ConfigFile;
import me.nobeld.minecraft.noblewhitelist.config.WhitelistJson;
import me.nobeld.minecraft.noblewhitelist.config.WhitelistYaml;
import me.nobeld.minecraft.noblewhitelist.data.WhitelistChecker;
import me.nobeld.minecraft.noblewhitelist.data.WhitelistData;
import me.nobeld.minecraft.noblewhitelist.model.DataGetter;
import me.nobeld.minecraft.noblewhitelist.model.StorageType;
import me.nobeld.minecraft.noblewhitelist.storage.MySQLDatabase;
import me.nobeld.minecraft.noblewhitelist.storage.SQLDatabase;
import me.nobeld.minecraft.noblewhitelist.storage.SQLiteDatabase;
import me.nobeld.minecraft.noblewhitelist.util.LibsManager;
import me.nobeld.minecraft.noblewhitelist.util.Metrics;
import me.nobeld.minecraft.noblewhitelist.util.ServerUtil;
import me.nobeld.minecraft.noblewhitelist.util.UpdateChecker;
import net.byteflux.libby.BukkitLibraryManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;

import static me.nobeld.minecraft.noblewhitelist.config.ConfigFile.refreshData;

public class NobleWhitelist extends JavaPlugin {
    private static NobleWhitelist plugin;
    private static boolean hasPaper;
    private NWlCommand commands;
    private BukkitAudiences adventure;
    private WhitelistData whitelistData;
    private WhitelistChecker whitelistChecker;
    private UpdateChecker checker;
    private NobleWhitelistApi api;
    private DataGetter storageInst = null;
    private StorageType storageType = StorageType.NONE;
    @Override
    public void onEnable() {
        plugin = this;
        new LibsManager(new BukkitLibraryManager(this));

        if (!ServerUtil.canRun(this)) return;
        hasPaper = ServerUtil.hasPaper();

        this.adventure = BukkitAudiences.create(this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new NWLPAPIExpansion(this).register();
        }
        if (Bukkit.getPluginManager().getPlugin("MiniPlaceholders") != null) {
            new NWLMiniExpansion(this);
        }
        refreshData();
        setupStorage();

        whitelistData = new WhitelistData(this);
        whitelistChecker = new WhitelistChecker(this);
        this.api = new NobleWhitelistApi(this);

        Bukkit.getServer().getPluginManager().registerEvents(new Listener(this), this);
        setupCommand();

        long total = getStorageInst().getTotal();
        consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><green>Plugin activated, thanks for using it ^^"));
        consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><green>Loaded <yellow>" + total + " <green>players."));
        if (!storageType.isDatabase() && total >= 100) consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><green>Mind in use database as storage type since there is a lot of players."));

        checker = new UpdateChecker(this);
        if (checker.canUpdate(ConfigFile.getConfig(ConfigFile.notifyUpdate), false)) {
            consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><#F1B65C>There is a new version available for <gold>Noble Whitelist: <#C775FF>" + checker.getLatest(), null));
            consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><#F1B65C>Download it at: <#75CDFF>https://www.github.com/NobelD/NobleWhitelist/releases", null));
        }

        Metrics metrics = new Metrics(this, 20050);
        metrics.addCustomChart(new Metrics.MultiLineChart("players_and_servers", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            return valueMap;
        }));
    }
    @Override
    public void onDisable() {
        reloadConfig();
        if (this.hasDatabase()) {
            ((SQLDatabase)this.storageInst).close();
        }
        if (hasDatabase()) ((SQLDatabase) storageInst).close();
        if(this.adventure != null) {
            consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><red>Plugin disabled, see you later!"));
            this.adventure.close();
            this.adventure = null;
        }
    }
    public void setupCommand() {
        commands = new NWlCommand(this);
    }
    public NWlCommand getCommand() {
        return commands;
    }
    public void setupStorage() {
        String type = (ConfigFile.getConfig(ConfigFile.storageType)).toLowerCase();

        if (type.contains("json")) {
            storageType = StorageType.JSON;
        } else if (type.contains("yaml")) {
            storageType = StorageType.YAML;
        } else if (type.contains("mysql")) {
            storageType = StorageType.MYSQL;
        } else if (type.contains("mariadb")) {
            storageType = StorageType.MARIADB;
        } else {
            storageType = StorageType.SQLITE;
        }

        try {
            switch (storageType) {
                case JSON -> {
                    consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><green>Loading <yellow>Json <green>file."));
                    this.storageInst = new WhitelistJson();
                }
                case YAML -> {
                    consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><green>Loading <yellow>Yaml <green>file."));
                    this.storageInst = new WhitelistYaml();
                    storageType = StorageType.YAML;
                }
                default -> {
                    HikariConfig databaseConfig = new HikariConfig();
                    databaseConfig.setConnectionTimeout(30000L);
                    databaseConfig.setMaxLifetime(30000L);
                    if (storageType.isRemoteDatabase()) {
                        this.consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><green>Connecting to <yellow>remote database <green>for whitelist."));
                        databaseConfig.setUsername(ConfigFile.getConfig(ConfigFile.storageUser));
                        databaseConfig.setPassword(ConfigFile.getConfig(ConfigFile.storagePassword));
                        this.storageInst = new MySQLDatabase(this.getName(), this.getThreadFactory(), ConfigFile.getConfig(ConfigFile.storageType), ConfigFile.getConfig(ConfigFile.storageHost), ConfigFile.getConfig(ConfigFile.storagePort), ConfigFile.getConfig(ConfigFile.storageDBName), databaseConfig);
                    } else {
                        consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><green>Loading <yellow>local <green>database."));
                        this.storageInst = new SQLiteDatabase(this.getName(), this.getThreadFactory(), databaseConfig);
                    }
                    ((SQLDatabase) this.storageInst).createTables();
                }
            }
            consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><green>Storage loaded successfully."));
        } catch (Exception e) {
            if (ConfigFile.getConfig(ConfigFile.storageSecurityClose)) {
                log(Level.SEVERE, "Failed to setup storage, the server will be closed.");
                log(Level.SEVERE, e.getMessage());
                Bukkit.getServer().shutdown();
            } else {
                log(Level.SEVERE, "Failed to setup storage, plugin will be disabled.");
                log(Level.SEVERE, e.getMessage());
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }
    }
    private ThreadFactory getThreadFactory() {
        // Code from https://github.com/games647/FastLogin
        return new ThreadFactoryBuilder()
                .setNameFormat(getName() + " Pool Thread #%1$d")
                // Hikari create daemons by default. We could use daemon threads for our own scheduler too
                // because we safely shut down
                .setDaemon(true)
                .build();
    }
    public boolean hasDatabase() {
        return this.storageType.isDatabase();
    }
    public StorageType getStorageType() {
        return this.storageType;
    }

    public DataGetter getStorageInst() {
        return this.storageInst;
    }
    public @NonNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }
    public Audience consoleMsg() {
        return this.adventure().console();
    }

    public Audience playerMsg(Player player) {
        return this.adventure().player(player);
    }

    public static NobleWhitelist getPlugin() {
        return plugin;
    }
    public static void log(Level level, String msg) {
        plugin.getLogger().log(level, msg);
    }
    public NobleWhitelistApi api() {
        return this.api;
    }
    public static boolean hasPaper() {
        return hasPaper;
    }
    public WhitelistData whitelistData() {
        return this.whitelistData;
    }
    public WhitelistChecker whitelistChecker() {
        return this.whitelistChecker;
    }
    public UpdateChecker getUptChecker() {
        return this.checker;
    }
}