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
package me.nobeld.mc.noblewhitelist.storage;

import com.zaxxer.hikari.HikariConfig;

import java.util.Locale;
import java.util.concurrent.ThreadFactory;

public class MySQLDatabase extends SQLDatabase {
    // Code from https://github.com/games647/FastLogin
    private static final String JDBC_PROTOCOL = "jdbc:";
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MARIADB_DRIVER = "org.mariadb.jdbc.Driver";
    public MySQLDatabase(String poolName, ThreadFactory threadFactory, String driver, String ip, int port, String database, HikariConfig config) {
        super(poolName, threadFactory, setParams(driver, ip, port, database, config));
    }
    private static HikariConfig setParams(String driver, String host, int port, String database, HikariConfig config) {
        if (driver.trim().equalsIgnoreCase("mysql")) {
            config.setDriverClassName(MYSQL_DRIVER);
        } else if (driver.trim().equalsIgnoreCase("mariadb")) {
            config.setDriverClassName(MARIADB_DRIVER);
        } else {
            config.setDriverClassName(driver);
        }

        config.addDataSourceProperty("autoReconnect", true);
        config.addDataSourceProperty("leakDetectionThreshold", true);
        config.addDataSourceProperty("verifyServerCertificate", false);
        config.addDataSourceProperty("useSSL", false);
        config.addDataSourceProperty("requireSSL", false);
        config.addDataSourceProperty("paranoid", true);

        config.setJdbcUrl(JDBC_PROTOCOL + buildJDBCUrl(driver, host, port, database));
        addPerformanceProperties(config);
        return config;
    }
    private static String buildJDBCUrl(String driver, String host, int port, String database) {
        MySQLVariant variant = MySQLVariant.fromDriver(driver);
        if (variant == null) {
            throw new IllegalArgumentException("Unknown storage driver");
        }

        return variant.getJdbcPrefix() + "://" + host + ':' + port + '/' + database;
    }
    private static void addPerformanceProperties(HikariConfig config) {
        // disabled by default - will return the same prepared statement instance
        config.addDataSourceProperty("cachePrepStmts", true);
        // default prepStmtCacheSize 25 - number of cached statements
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        // default prepStmtCacheSqlLimit 256 - length of SQL
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        // default false - available in newer versions caches the statement server-side
        config.addDataSourceProperty("useServerPrepStmts", true);
        // default false - prefer use of local values for autocommit and
        // transaction isolation (alwaysSendSetIsolation) should only be enabled if we always use the set* methods
        // instead of raw SQL
        // https://forums.mysql.com/read.php?39,626495,626512
        config.addDataSourceProperty("useLocalSessionState", true);
        // rewrite batched statements to a single statement, adding them behind each other
        // only useful for addBatch statements and inserts
        config.addDataSourceProperty("rewriteBatchedStatements", true);
        // cache result metadata
        config.addDataSourceProperty("cacheResultSetMetadata", true);
        // cache results of show variables and collation per URL
        config.addDataSourceProperty("cacheServerConfiguration", true);
        // default false - set auto commit only if not matching
        config.addDataSourceProperty("elideSetAutoCommits", true);

        // default true - internal timers for idle calculation -> removes System.getCurrentTimeMillis call per query
        // Some platforms are slow on this, it could affect the throughput about 3% according to MySQL
        // performance gems presentation
        // In our case it can be useful to see the time in error messages
        // config.addDataSourceProperty("maintainTimeStats", false);
    }
    enum MySQLVariant {
        MYSQL("mysql"),
        MARIADB("mariadb");
        private final String jdbcPrefix;
        public static MySQLVariant fromDriver(String driver) {
            String normalizedDriver = driver.toLowerCase(Locale.ENGLISH);
            if (normalizedDriver.contains("mysql")) {
                return MYSQL;
            } else if (normalizedDriver.contains("mariadb")) {
                return MARIADB;
            }
            return null;
        }
        MySQLVariant(String jdbcPrefix) {
            this.jdbcPrefix = jdbcPrefix;
        }
        public String getJdbcPrefix() {
            return jdbcPrefix;
        }
    }
}
