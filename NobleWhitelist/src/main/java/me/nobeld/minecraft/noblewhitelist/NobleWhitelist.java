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
    private DataGetter storage = null;
    private boolean hasDatabase = false;
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

        long total = getStorage().getTotal();
        consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><green>Plugin activated, thanks for using it ^^"));
        consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><green>Loaded <yellow>" + total + " <green>players."));
        if (!hasDatabase && total >= 100) consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><green>Mind in use database as storage type since there is a lot of players."));

        checker = new UpdateChecker(this);
        if (checker.canUpdate(ConfigFile.getConfig(ConfigFile.notifyUpdate), false)) {
            consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><#F1B65C>There is a new version available: <#C775FF>" + checker.getLatest(), null));
            consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><#F1B65C>Download it at <#75CDFF>https://www.github.com/NobelD/NobleWhitelist/releases", null));
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
            ((SQLDatabase)this.storage).close();
        }
        if (hasDatabase()) ((SQLDatabase) storage).close();
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
        boolean isRemote = type.contains("mysql") || type.contains("mariadb");
        boolean isLocal = type.contains("sqlite");

        boolean isYaml = type.contains("yaml");
        boolean isJson = type.contains("json");

        if (isJson) {
            this.storage = new WhitelistJson();
        } else if (isYaml) {
            this.storage = new WhitelistYaml();
        } else {
            try {
                HikariConfig databaseConfig = new HikariConfig();
                databaseConfig.setConnectionTimeout(30000L);
                databaseConfig.setMaxLifetime(30000L);
                if (isRemote && !isLocal) {
                    this.consoleMsg().sendMessage(ServerUtil.formatAll("<prefix><green>Connecting to remote database for whitelist."));
                    databaseConfig.setUsername(ConfigFile.getConfig(ConfigFile.storageUser));
                    databaseConfig.setPassword(ConfigFile.getConfig(ConfigFile.storagePassword));
                    this.storage = new MySQLDatabase(this.getName(), this.getThreadFactory(), ConfigFile.getConfig(ConfigFile.storageType), ConfigFile.getConfig(ConfigFile.storageHost), ConfigFile.getConfig(ConfigFile.storagePort), ConfigFile.getConfig(ConfigFile.storageDBName), databaseConfig);
                } else {
                    this.storage = new SQLiteDatabase(this.getName(), this.getThreadFactory(), databaseConfig);
                }

                this.hasDatabase = true;
                ((SQLDatabase)this.storage).createTables();
            } catch (Exception var7) {
                if (ConfigFile.getConfig(ConfigFile.storageSecurityClose)) {
                    log(Level.SEVERE, "Failed to setup database, the server will be closed.");
                    log(Level.SEVERE, var7.getMessage());
                    Bukkit.getServer().shutdown();
                } else {
                    log(Level.SEVERE, "Failed to setup database, plugin will be disabled.");
                    log(Level.SEVERE, var7.getMessage());
                    Bukkit.getPluginManager().disablePlugin(this);
                }
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
        return this.hasDatabase;
    }

    public DataGetter getStorage() {
        return this.storage;
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