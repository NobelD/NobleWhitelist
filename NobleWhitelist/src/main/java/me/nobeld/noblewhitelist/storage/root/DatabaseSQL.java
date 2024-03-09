/*
 * SPDX-License-Identifier: MIT
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 games647 and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.nobeld.noblewhitelist.storage.root;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.model.storage.DataGetter;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import me.nobeld.noblewhitelist.model.base.PlayerWrapper;
import me.nobeld.noblewhitelist.util.UUIDUtil;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class DatabaseSQL implements DataGetter {
    // Code from https://github.com/games647/FastLogin
    protected static final String TABLE_NAME = "noble_whitelist";
    protected static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `" + TABLE_NAME + "` ("
            + "`ID` INTEGER PRIMARY KEY AUTO_INCREMENT, "
            + "`Name` VARCHAR(40), "
            + "`UUID` VARCHAR(36), "
            + "`Discord` VARCHAR(40), "
            + "`Whitelisted` BOOLEAN NOT NULL, "
            + "UNIQUE (`ID`,`Name`,`UUID`) "
            + ')';
    protected static final String COUNT_ALL = "SELECT COUNT(*) FROM " + TABLE_NAME;
    protected static final String GET_BY_NAME = "SELECT * FROM `" + TABLE_NAME + "` WHERE LOWER(`Name`)=? LIMIT 1";
    protected static final String GET_BY_UUID = "SELECT * FROM `" + TABLE_NAME + "` WHERE `UUID`=? LIMIT 1";
    protected static final String GET_BY_DISCORD = "SELECT * FROM `" + TABLE_NAME + "` WHERE `Discord`=? LIMIT 1";
    protected static final String GET_BY_DISCORD_ALL = "SELECT * FROM `" + TABLE_NAME + "` WHERE `Discord`=? LIMIT 100";
    protected static final String DELETE_BY_ID= "DELETE FROM `" + TABLE_NAME + "` WHERE `ID`=?";
    protected static final String DELETE_ALL = "DROP TABLE `" + TABLE_NAME + "`";
    protected static final String INSERT_DATA = "INSERT INTO `" + TABLE_NAME
            + "` (`Name`, `UUID`, `Discord`, `Whitelisted`) " + "VALUES (?, ?, ?, ?) ";
    protected static final String UPDATE_DATA = "UPDATE `" + TABLE_NAME
            + "` SET `Name`=?, `UUID`=?, `Discord`=?, `Whitelisted`=? WHERE `ID`=?";
    protected static String SELECT_AMOUNT(int m) {
        if (m <= 1) return "SELECT * FROM `" + TABLE_NAME + "` ORDER BY `ID` LIMIT 10";
        int amount = 10 * (m - 1);
        return "SELECT * FROM `" + TABLE_NAME + "` ORDER BY `ID` LIMIT 10 OFFSET " + amount;
    }
    protected final HikariDataSource dataSource;
    public DatabaseSQL(String poolName, ThreadFactory threadFactory, HikariConfig config) {
        config.setPoolName(poolName);
        if (threadFactory != null) {
            config.setThreadFactory(threadFactory);
        }
        this.dataSource = new HikariDataSource(config);
    }
    public void createTables() throws SQLException {
        try (Connection con = dataSource.getConnection();
             Statement createStmt = con.createStatement()) {
            createStmt.executeUpdate(CREATE_TABLE);
        }
    }
    @Override
    public WhitelistEntry loadPlayer(@NotNull PlayerWrapper player) {
        WhitelistEntry result = this.loadPlayer(player.getUUID());
        if (result != null) return result;
        result = loadPlayer(player.getName());
        return result;
    }
    @Override
    public WhitelistEntry loadPlayer(@NotNull String name) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(GET_BY_NAME)) {
            statement.setString(1, name.toLowerCase());

            try (ResultSet result = statement.executeQuery()) {
                return parseResult(result).orElse(null);
            }
        } catch (SQLException sqlEx) {
            NobleWhitelist.log(Level.SEVERE, "Failed to query data: " + name);
            NobleWhitelist.log(Level.SEVERE, sqlEx.getMessage());
        }

        return null;
    }
    @Override
    public WhitelistEntry loadPlayer(@NotNull UUID uuid) {
        String id = UUIDUtil.noDashUUID(uuid);
        try (Connection con = dataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(GET_BY_UUID)) {
            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                return parseResult(resultSet).orElse(null);
            }
        } catch (SQLException sqlEx) {
            NobleWhitelist.log(Level.SEVERE, "Failed to query data: " + id);
            NobleWhitelist.log(Level.SEVERE, sqlEx.getMessage());
        }

        return null;
    }
    @Override
    public WhitelistEntry loadPlayer(long id) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(GET_BY_DISCORD)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                return parseResult(resultSet).orElse(null);
            }
        } catch (SQLException sqlEx) {
            NobleWhitelist.log(Level.SEVERE, "Failed to query data: " + id);
            NobleWhitelist.log(Level.SEVERE, sqlEx.getMessage());
        }

        return null;
    }
    @Override
    public List<WhitelistEntry> loadAccounts(long id) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(GET_BY_DISCORD_ALL)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<WhitelistEntry> list = new ArrayList<>();
                if (resultSet.next()) {
                    getResult(resultSet).map(list::add);
                }
            }
        } catch (SQLException sqlEx) {
            NobleWhitelist.log(Level.SEVERE, "Failed to query data: " + id);
            NobleWhitelist.log(Level.SEVERE, sqlEx.getMessage());
        }

        return null;
    }
    @Override
    public List<WhitelistEntry> listIndex(int page) {
        String state = SELECT_AMOUNT(page);
        List<WhitelistEntry> list = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             Statement statement = con.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(state)) {
                while (resultSet.next()) {
                    long id = resultSet.getInt(1);
                    String name = resultSet.getString(2);
                    String uuid = resultSet.getString(3);
                    long discord = resultSet.getLong(4);
                    boolean whitelisted = resultSet.getBoolean(5);

                    list.add(new WhitelistEntry(id, name, UUIDUtil.parseUUID(uuid), discord, whitelisted));
                }
            }
        } catch (SQLException sqlEx) {
            NobleWhitelist.log(Level.SEVERE, "Failed to get data.");
            NobleWhitelist.log(Level.SEVERE, sqlEx.getMessage());
        }
        return list;
    }
    private Optional<WhitelistEntry> parseResult(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return getResult(resultSet);
        }

        return Optional.empty();
    }
    private Optional<WhitelistEntry> getResult(ResultSet resultSet) throws SQLException {
        long id = resultSet.getInt(1);

        String name = resultSet.getString(2);
        String uuid = resultSet.getString(3);
        long discord = resultSet.getLong(4);
        boolean whitelisted = resultSet.getBoolean(5);

        return Optional.of(new WhitelistEntry(id, name, UUIDUtil.parseUUID(uuid), discord, whitelisted));
    }
    @Override
    public void save(@NotNull WhitelistEntry data) {
        try (Connection con = dataSource.getConnection()) {
            String uuid = data.getOptUUID().map(UUIDUtil::noDashUUID).orElse(null);

            data.getSaveLock().lock();
            try {
                if (data.isSaved()) {
                    try (PreparedStatement saveStmt = con.prepareStatement(UPDATE_DATA)) {
                        saveStmt.setString(1, data.getName());
                        saveStmt.setString(2, uuid);
                        saveStmt.setLong(3, data.getDiscordID());
                        saveStmt.setBoolean(4, data.isWhitelisted());

                        saveStmt.setLong(5, data.getRowId());
                        saveStmt.execute();
                    }
                } else {
                    try (PreparedStatement saveStmt = con.prepareStatement(INSERT_DATA, RETURN_GENERATED_KEYS)) {
                        saveStmt.setString(1, data.getName());
                        saveStmt.setString(2, uuid);
                        saveStmt.setLong(3, data.getDiscordID());
                        saveStmt.setBoolean(4, data.isWhitelisted());

                        saveStmt.execute();
                        try (ResultSet generatedKeys = saveStmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                data.setRowId(generatedKeys.getInt(1));
                            }
                        }
                    }
                }
            } finally {
                data.getSaveLock().unlock();
            }
        } catch (SQLException ex) {
            NobleWhitelist.log(Level.SEVERE, "Failed to query data: " + data);
            NobleWhitelist.log(Level.SEVERE, ex.getMessage());
        }
    }
    @Override
    public void delete(@NotNull WhitelistEntry data) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_BY_ID)) {
            statement.setLong(1, data.getRowId());
            statement.executeUpdate();
        } catch (SQLException sqlEx) {
            NobleWhitelist.log(Level.SEVERE, "Failed to delete data: " + data.getRowId());
            NobleWhitelist.log(Level.SEVERE, sqlEx.getMessage());
        }
    }
    @Override
    public boolean clear() {
        boolean s;
        try (Connection con = dataSource.getConnection();
             Statement statement = con.createStatement()) {
            statement.executeUpdate(DELETE_ALL);
            s = true;
        } catch (SQLException sqlEx) {
            NobleWhitelist.log(Level.SEVERE, "Failed to clear all data.");
            NobleWhitelist.log(Level.SEVERE, sqlEx.getMessage());
            s = false;
        }
        try {
            createTables();
        } catch (SQLException ignored) {}
        return s;
    }
    @Override
    public long getTotal() {
        try (Connection con = dataSource.getConnection();
             Statement statement = con.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(COUNT_ALL)) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        } catch (SQLException sqlEx) {
            NobleWhitelist.log(Level.SEVERE, "Failed to get data.");
            NobleWhitelist.log(Level.SEVERE, sqlEx.getMessage());
            return 0;
        }
    }
    @Override
    public void reload() {}
    public void close() {
        dataSource.close();
    }
}
