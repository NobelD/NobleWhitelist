package me.nobeld.noblewhitelist;

import com.alessiodp.libby.BukkitLibraryManager;
import com.alessiodp.libby.Library;
import com.alessiodp.libby.PaperLibraryManager;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
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
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

import static me.nobeld.noblewhitelist.config.FileManager.separator;

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
    private BukkitAudiences provider;
    private static BukkitAdventure adventure = null;
    private boolean blocked = false;

    public BukkitAudiences getProvider() {
        if (hasPaper) {
            throw new IllegalStateException("Tried to access Adventure when the server is paper!");
        }
        if (provider == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return provider;
    }

    public void setProvider(BukkitAudiences provider) {
        this.provider = provider;
    }

    @Override
    public void onEnable() {
        // #TODO fix static classes and adventure
        plugin = this;
        if (!ServerUtil.canRun(this)) return;
        hasPaper = ServerUtil.hasPaper();

        boolean paperLoader = hasPaper && ServerUtil.isGreaterEquals(19, 4);

        NWLContainer bc = NWLContainer.builder(this)
                .load(() -> new LibsManager(
                              this,
                              paperLoader ? new PaperLibraryManager(this) : new BukkitLibraryManager(this),
                              hasPaper,
                              Collections.singletonList(Library.builder()
                                                                .groupId("net{}kyori")
                                                                .artifactId("adventure-platform-bukkit")
                                                                .version("4.3.2")
                                                                .resolveTransitiveDependencies(true)
                                                                .build())
                      )
                     )
                .loadFiles(getDataFolder().getPath(), PairData.of("config.yml", FileManager.FileType.YAML))
                .loadAdventure()
                .loadUpdateChecker(
                        "NobleWhitelist",
                        "spigot",
                        (a, l, p) -> {
                            a.sendMessage(AdventureUtil.formatAll("<prefix><#F1B65C>It seems that you are not using the latest version of <gold>Noble Whitelist <dark_green>| <#F1B65C>Latest: <#FF8B4D>" + l));
                            a.sendMessage(AdventureUtil.formatAll("<prefix><#F1B65C>Download it at: <#75CDFF>" + p));
                        })
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
    public ThreadFactory createThread(String name) {
        // Code from https://github.com/games647/FastLogin
        return new ThreadFactoryBuilder()
                .setNameFormat(name)
                // Hikari create daemons by default. We could use daemon threads for our own scheduler too
                // because we safely shut down
                .setDaemon(true)
                .build();
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

    public boolean hasPaper() {
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
            if (hasPaper()) adventure = new PaperAdventure();
            else adventure = new BukkitAdventure(this);
            adventure.startAdventure();
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
    public String configPath() {
        return getDataFolder().getPath() + separator();
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