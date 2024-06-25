package me.nobeld.noblewhitelist.model.whitelist;

import me.nobeld.noblewhitelist.util.UUIDUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class WhitelistEntry {
    private long rowId;
    private UUID uuid;
    private String name;
    private long discordID;
    private boolean isWhitelisted;
    private final ReentrantLock saveLock = new ReentrantLock();

    public WhitelistEntry(long rowID, @Nullable String name, @Nullable UUID uuid, long discordID, boolean isWhitelisted) {
        this.rowId = rowID;
        this.uuid = uuid;
        this.name = checkFromNull(name);
        this.discordID = discordID;
        this.isWhitelisted = isWhitelisted;
    }

    public WhitelistEntry(@Nullable String name, @Nullable UUID uuid, long discordID, boolean isWhitelisted) {
        this(-1, name, uuid, discordID, isWhitelisted);
    }

    public WhitelistEntry(@Nullable String name, @Nullable UUID uuid) {
        this(name, uuid, -1, true);
    }

    public ReentrantLock getSaveLock() {
        return saveLock;
    }

    public synchronized boolean isSaved() {
        return rowId >= 0;
    }

    public synchronized long getRowId() {
        return rowId;
    }

    public synchronized void setRowId(long generatedId) {
        this.rowId = generatedId;
    }

    @Nullable
    public synchronized UUID getUUID() {
        return uuid;
    }

    public synchronized Optional<UUID> getOptUUID() {
        return Optional.ofNullable(uuid);
    }

    public synchronized void setUuid(@Nullable UUID id) {
        this.uuid = id;
    }

    @Nullable
    public synchronized String getName() {
        if (name == null || name.isBlank() || name.isEmpty()) name = null;
        return name;
    }

    public synchronized Optional<String> getOptName() {
        if (name == null || name.isBlank() || name.isEmpty()) name = null;
        return Optional.ofNullable(name);
    }

    public synchronized void setName(@Nullable String name) {
        if (name == null || name.isBlank() || name.isEmpty()) name = null;
        this.name = name;
    }

    public synchronized long getDiscordID() {
        return discordID;
    }

    public synchronized void setDiscordID(long id) {
        this.discordID = id;
    }

    public synchronized boolean hasDiscord() {
        return discordID >= 0;
    }

    public synchronized boolean isWhitelisted() {
        return isWhitelisted;
    }

    public synchronized void setWhitelisted(boolean whitelisted) {
        this.isWhitelisted = whitelisted;
    }

    public synchronized String getSubDataString() {
        String id = getOptUUID().map(UUIDUtil::noDashUUID).orElse("none$");
        String name = getOptName().orElse("none$");
        return name + "; " + id + "; " + getDiscordID() + "; " + isWhitelisted();
    }

    @Nullable
    private synchronized String checkFromNull(@Nullable String s) {
        if (s == null || s.equalsIgnoreCase("null") || s.equalsIgnoreCase("none$")) s = null;
        return s;
    }

    public synchronized Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        map.put("name", this.getOptName().orElse("none"));
        map.put("uuid", this.getOptUUID().map(UUID::toString).orElse("none"));
        map.put("user_id", this.hasDiscord() ? String.valueOf(this.getDiscordID()) : "none");
        map.put("user_mention", this.hasDiscord() ? "<@" + this.getDiscordID() + ">" : "none");
        map.put("row", this.isSaved() ? String.valueOf(this.getRowId()) : "none");
        return map;
    }

    public synchronized Map<String, String> toMap(@NotNull Map<String, String> map) {
        map.put("name", this.getOptName().orElse("none"));
        map.put("uuid", this.getOptUUID().map(UUID::toString).orElse("none"));
        map.put("user_id", this.hasDiscord() ? String.valueOf(this.getDiscordID()) : "none");
        map.put("user_mention", this.hasDiscord() ? "<@" + this.getDiscordID() + ">" : "none");
        map.put("row", this.isSaved() ? String.valueOf(this.getRowId()) : "none");
        return map;
    }
}
