package me.nobeld.mc.noblewhitelist.logic;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import me.nobeld.mc.noblewhitelist.NobleWhitelist;
import me.nobeld.mc.noblewhitelist.config.ConfigData;
import me.nobeld.mc.noblewhitelist.model.PairData;
import me.nobeld.mc.noblewhitelist.model.base.NWLData;
import me.nobeld.mc.noblewhitelist.model.storage.DataGetter;
import me.nobeld.mc.noblewhitelist.model.storage.StorageType;
import me.nobeld.mc.noblewhitelist.storage.*;
import me.nobeld.mc.noblewhitelist.util.AdventureUtil;
import org.bukkit.Bukkit;

import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;

public class StorageLoader {
    private final NWLData data;
    private final ConfigData config;
    public StorageLoader(NWLData data, ConfigData config) {
        this.data = data;
        this.config = config;
    }
    public PairData<DataGetter, StorageType> setupStorage() {
        String type = (config.get(ConfigData.StorageCF.storageType)).toLowerCase();
        DataGetter storageInst = null;
        StorageType storageType;

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
                    NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>Loading <yellow>Json <green>file."));
                    storageInst = new WhitelistJson();
                }
                case YAML -> {
                    NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>Loading <yellow>Yaml <green>file."));
                    storageInst = new WhitelistYaml();
                }
                default -> {
                    HikariConfig databaseConfig = new HikariConfig();
                    databaseConfig.setConnectionTimeout(30000L);
                    databaseConfig.setMaxLifetime(30000L);
                    if (storageType.isRemoteDatabase()) {
                        NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>Connecting to <yellow>remote database <green>for whitelist."));
                        databaseConfig.setUsername(config.get(ConfigData.StorageCF.storageUser));
                        databaseConfig.setPassword(config.get(ConfigData.StorageCF.storagePassword));
                        storageInst = new MySQLDatabase(
                                data.name(),
                                this.getThreadFactory(),
                                config.get(ConfigData.StorageCF.storageType),
                                config.get(ConfigData.StorageCF.storageHost),
                                config.get(ConfigData.StorageCF.storagePort),
                                config.get(ConfigData.StorageCF.storageDBName),
                                databaseConfig
                        );
                    } else {
                        NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>Loading <yellow>local <green>database."));
                        storageInst = new SQLiteDatabase(data.name(), getThreadFactory(), databaseConfig);
                    }
                    ((SQLDatabase) storageInst).createTables();
                }
            }
            NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>The whitelist storage was loaded."));
        } catch (Exception e) {
            if (config.get(ConfigData.StorageCF.closeFail)) {
                NobleWhitelist.log(Level.SEVERE, "Failed to setup storage, the server will be closed.");
                NobleWhitelist.log(Level.SEVERE, e.getMessage());
                Bukkit.getServer().shutdown();
            } else {
                NobleWhitelist.log(Level.SEVERE, "Failed to setup storage, plugin will be disabled.");
                NobleWhitelist.log(Level.SEVERE, e.getMessage());
                data.disable();
            }
        }
        return PairData.of(storageInst, storageType);
    }
    private ThreadFactory getThreadFactory() {
        // Code from https://github.com/games647/FastLogin
        return new ThreadFactoryBuilder()
                .setNameFormat(data.name() + " Pool Thread #%1$d")
                // Hikari create daemons by default. We could use daemon threads for our own scheduler too
                // because we safely shut down
                .setDaemon(true)
                .build();
    }
}
