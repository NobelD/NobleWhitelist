package me.nobeld.minecraft.noblewhitelist.model;

public enum StorageType {
    NONE("none"),
    JSON("json"),
    YAML("yaml"),
    SQLITE("SQLite"),
    MYSQL("MySQL"),
    MARIADB("MariaDB");
    private final String name;
    StorageType(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public boolean isFile() {
        return this == JSON || this == YAML;
    }
    public boolean isDatabase() {
        return isLocalDatabase() || isRemoteDatabase();
    }
    public boolean isLocalDatabase() {
        return this == SQLITE;
    }
    public boolean isRemoteDatabase() {
        return this == MYSQL || this == MARIADB;
    }
}
