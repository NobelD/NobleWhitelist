package me.nobeld.noblewhitelist.logic;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.config.ConfigData;
import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.model.storage.DataGetter;
import me.nobeld.noblewhitelist.model.storage.StorageType;
import me.nobeld.noblewhitelist.storage.*;
import me.nobeld.noblewhitelist.storage.root.DatabaseSQL;
import me.nobeld.noblewhitelist.util.AdventureUtil;

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
        } else if (type.contains("toml")) {
            storageType = StorageType.TOML;
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
                    storageInst = new FileJson();
                }
                case YAML -> {
                    NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>Loading <yellow>Yaml <green>file."));
                    storageInst = new FileYaml();
                }
                case TOML -> {
                    NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>Loading <yellow>Toml <green>file."));
                    storageInst = new FileToml();
                }
                default -> {
                    HikariConfig databaseConfig = new HikariConfig();
                    databaseConfig.setConnectionTimeout(30000L);
                    databaseConfig.setMaxLifetime(30000L);
                    if (storageType.isRemoteDatabase()) {
                        NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>Connecting to <yellow>remote database <green>for whitelist."));
                        databaseConfig.setUsername(config.get(ConfigData.StorageCF.storageUser));
                        databaseConfig.setPassword(config.get(ConfigData.StorageCF.storagePassword));
                        storageInst = new DatabaseMySQL(
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
                        storageInst = new DatabaseSQLite(data.name(), getThreadFactory(), databaseConfig);
                    }
                    ((DatabaseSQL) storageInst).createTables();
                }
            }
            NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>The whitelist storage was loaded."));
        } catch (Exception e) {
            switch(config.get(ConfigData.StorageCF.failAction)) {
                case CLOSE -> {
                    NobleWhitelist.log(Level.SEVERE, "Failed to setup storage, the server will be closed.");
                    NobleWhitelist.log(Level.SEVERE, e.getMessage());
                    data.closeServer();
                }
                case BLOCK -> {
                    NobleWhitelist.log(Level.SEVERE, "Failed to setup storage, the server will block the joins until reconnect to the database (use /nwl reload).");
                    NobleWhitelist.log(Level.SEVERE, e.getMessage());
                    data.setBlocked(true);
                }
                case COMMAND -> {
                    NobleWhitelist.log(Level.SEVERE, "Failed to setup storage, the plugin will be disabled and a command will be executed.");
                    NobleWhitelist.log(Level.SEVERE, e.getMessage());
                    data.runCommand(config.get(ConfigData.StorageCF.failCommand));
                }
                default -> {
                    NobleWhitelist.log(Level.SEVERE, "Failed to setup storage, the plugin will be disabled.");
                    NobleWhitelist.log(Level.SEVERE, e.getMessage());
                    data.disable();
                }
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
