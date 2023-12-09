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
package me.nobeld.minecraft.noblewhitelist.storage;

import com.zaxxer.hikari.HikariConfig;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.sqlite.JDBC;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.getPlugin;
import static me.nobeld.minecraft.noblewhitelist.config.FileManager.separator;

public class SQLiteDatabase extends SQLDatabase {
    private static final String SQLITE_DRIVER = "org.sqlite.SQLiteDataSource";
    private final Lock lock = new ReentrantLock();
    public SQLiteDatabase(String poolName, ThreadFactory threadFactory, HikariConfig config) {
        super(poolName, threadFactory, setParams(config));
    }
    private static HikariConfig setParams(HikariConfig config) {
        config.setDataSourceClassName(SQLITE_DRIVER);

        config.setConnectionTestQuery("SELECT 1");
        config.setMaximumPoolSize(1);

        config.addDataSourceProperty("url", JDBC.PREFIX + getPlugin().getDataFolder().getPath() + separator() + "whitelist.sql");

        // a try to fix https://www.spigotmc.org/threads/fastlogin.101192/page-26#post-1874647
        // format strings retrieved by the timestamp column to match them from MySQL
        // vs the default: yyyy-MM-dd HH:mm:ss.SSS
        try {
            SQLiteConfig.class.getDeclaredMethod("setDateStringFormat", String.class);

            SQLiteConfig sqLiteConfig = new SQLiteConfig();
            sqLiteConfig.setDateStringFormat("yyyy-MM-dd HH:mm:ss");
            config.addDataSourceProperty("config", sqLiteConfig);
        } catch (NoSuchMethodException noSuchMethodException) {
            // Versions below this driver version do set the default timestamp value, so this change is not necessary
        }
        return config;
    }
    @Override
    public PlayerWhitelisted loadPlayer(@NotNull Player player) {
        lock.lock();
        try {
            return super.loadPlayer(player);
        } finally {
            lock.unlock();
        }
    }
    @Override
    public PlayerWhitelisted loadPlayer(@NotNull String name) {
        lock.lock();
        try {
            return super.loadPlayer(name);
        } finally {
            lock.unlock();
        }
    }
    @Override
    public PlayerWhitelisted loadPlayer(@NotNull UUID uuid) {
        lock.lock();
        try {
            return super.loadPlayer(uuid);
        } finally {
            lock.unlock();
        }
    }
    @Override
    public PlayerWhitelisted loadPlayer(long id) {
        lock.lock();
        try {
            return super.loadPlayer(id);
        } finally {
            lock.unlock();
        }
    }
    @Override
    public List<PlayerWhitelisted> loadAccounts(long id) {
        lock.lock();
        try {
            return super.loadAccounts(id);
        } finally {
            lock.unlock();
        }
    }
    @Override
    public void save(@NotNull PlayerWhitelisted data) {
        lock.lock();
        try {
            super.save(data);
        } finally {
            lock.unlock();
        }
    }
    @Override
    public void createTables() throws SQLException {
        try (Connection con = dataSource.getConnection();
             Statement createStmt = con.createStatement()) {
            // SQLite has a different syntax for auto increment
            createStmt.executeUpdate(CREATE_TABLE.replace("AUTO_INCREMENT", "AUTOINCREMENT"));
        }
    }
}
