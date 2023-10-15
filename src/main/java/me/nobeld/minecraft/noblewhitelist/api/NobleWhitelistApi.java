package me.nobeld.minecraft.noblewhitelist.api;

import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import me.nobeld.minecraft.noblewhitelist.data.WhitelistData;
import org.bukkit.entity.Player;

public class NobleWhitelistApi {
    private final NobleWhitelist plugin;
    public NobleWhitelistApi(NobleWhitelist plugin) {
        this.plugin = plugin;
    }
    public WhitelistData.SuccessEnum getSuccessType(Player player) {
        return plugin.whitelistChecker().successType(player);
    }
    public void createPlayerData(Player player) {
        plugin.whitelistData().addByPlayer(player);
    }
    public boolean hasData(Player player) {
        return plugin.whitelistData().hasData(player);
    }
    public boolean hasByPass(Player player) {
        return plugin.whitelistChecker().isByPass(player);
    }
    public boolean isWhitelisted(Player player) {
        return plugin.whitelistChecker().isWhitelisted(player);
    }
    public boolean optionalJoin(Player player) {
        return plugin.whitelistChecker().optionalJoin(player);
    }
    public boolean canPass(Player player) {
        return plugin.whitelistChecker().canPass(player);
    }
    public boolean whitelist() {
        return plugin.fileData().whitelistActive();
    }
    public void whitelist(boolean activated) {
        plugin.fileData().set("enabled", activated);
    }
}
