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
    public static PairData<DataGetter, StorageType> setupStorage(NWLData data, ConfigData config) {
        String type = config.get(ConfigData.StorageCF.storageType).toLowerCase();
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
                    boolean remote = storageType.isRemoteDatabase();
                    NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll(remote
                            ? "<prefix><green>Loading connection to <yellow>remote database <green>for database access."
                            : "<prefix><green>Loading <yellow>local <green>database."));
                    HikariConfig databaseConfig = new HikariConfig();
                    databaseConfig.setConnectionTimeout(config.get(ConfigData.StorageCF.storageTimeout) * 1_000L);
                    databaseConfig.setMaxLifetime(config.get(ConfigData.StorageCF.storageLifetime) * 1_000L);
                    databaseConfig.setKeepaliveTime(0);
                    if (remote) {
                        databaseConfig.setUsername(config.get(ConfigData.StorageCF.storageUser));
                        databaseConfig.setPassword(config.get(ConfigData.StorageCF.storagePassword));

                        if (config.get(ConfigData.StorageCF.storageUseSSL)) {
                            databaseConfig.addDataSourceProperty("allowPublicKeyRetrieval", config.get(ConfigData.StorageCF.storagePublicKeyRetrieval));
                            databaseConfig.addDataSourceProperty("serverRSAPublicKeyFile", config.get(ConfigData.StorageCF.storagePublicKeyFile));
                            databaseConfig.addDataSourceProperty("sslMode", config.get(ConfigData.StorageCF.storageSSLMode));
                        }
                        storageInst = new DatabaseMySQL(
                                data.name(),
                                getThreadFactory(data),
                                config.get(ConfigData.StorageCF.storageType),
                                config.get(ConfigData.StorageCF.storageHost),
                                config.get(ConfigData.StorageCF.storagePort),
                                config.get(ConfigData.StorageCF.storageDBName),
                                databaseConfig
                        );
                    } else {
                        storageInst = new DatabaseSQLite(data.name(), getThreadFactory(data), databaseConfig);
                    }
                    ((DatabaseSQL) storageInst).createTables();
                }
            }
            NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>The whitelist storage was loaded."));
            data.setBlocked(false);
        } catch (Exception e) {
            switch(config.getEnumUpper(ConfigData.StorageCF.failAction)) {
                case CLOSE -> {
                    NobleWhitelist.log(Level.SEVERE, "Failed to setup storage, the server will be closed.", e);
                    data.closeServer();
                }
                case BLOCK -> {
                    NobleWhitelist.log(Level.SEVERE, "Failed to setup storage, the server will block the joins until reconnect to the database (use /nwl reload).", e);
                    data.setBlocked(true);
                }
                case COMMAND -> {
                    NobleWhitelist.log(Level.SEVERE, "Failed to setup storage, the plugin will be disabled and a command will be executed.", e);
                    data.runCommand(config.get(ConfigData.StorageCF.failCommand));
                    data.disable();
                }
                default -> {
                    NobleWhitelist.log(Level.SEVERE, "Failed to setup storage, the plugin will be disabled.", e);
                    data.disable();
                }
            }
        }
        return PairData.of(storageInst, storageType);
    }
    private static ThreadFactory getThreadFactory(NWLData data) {
        // Code from https://github.com/games647/FastLogin
        return new ThreadFactoryBuilder()
                .setNameFormat(data.name() + " Pool Thread #%1$d")
                // Hikari create daemons by default. We could use daemon threads for our own scheduler too
                // because we safely shut down
                .setDaemon(true)
                .build();
    }
}
