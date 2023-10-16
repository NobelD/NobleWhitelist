package me.nobeld.minecraft.noblewhitelist.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import me.nobeld.minecraft.noblewhitelist.data.WhitelistData.SuccessEnum;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NWExpansion extends PlaceholderExpansion {
    private final NobleWhitelist plugin;
    public NWExpansion(NobleWhitelist plugin) {
        this.plugin = plugin;
    }
    @Override
    public @NotNull String getIdentifier() {
        return "NWhitelist";
    }
    @Override
    public @NotNull String getAuthor() {
        return "NobelD";
    }
    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }
    @Override
    public boolean persist() {
        return true;
    }
    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("whitelist_active")) {
            if (plugin.api().whitelist()) {
                return "yes";
            } else return "no";
        }
        if (params.equalsIgnoreCase("join_type")) {
            String type = "unknown";
            SuccessEnum sType = plugin.api().getSuccessType((Player) player);
            if (sType == SuccessEnum.ALL) type = "all";
            if (sType == SuccessEnum.NORMAL) type = "normal";
            if (sType == SuccessEnum.ONLY_UUID) type = "uuid";
            if (sType == SuccessEnum.ONLY_NAME) type = "name";
            if (sType == SuccessEnum.BYPASS) type = "bypass";
            if (sType == SuccessEnum.NONE) type = "none";
            return type;
        }
        if (params.equalsIgnoreCase("is_whitelisted")) {
            if (plugin.api().isWhitelisted((Player) player)) {
                return "yes";
            } else return "no";
        }
        if (params.equalsIgnoreCase("bypass")) {
            if (plugin.api().hasByPass((Player) player)) {
                return "yes";
            } else return "no";
        }
        if (params.equalsIgnoreCase("optional_join")) {
            if (plugin.api().optionalJoin((Player) player)) {
                return "yes";
            } else return "no";
        }
        if (params.equalsIgnoreCase("can_pass")) {
            if (plugin.api().canPass((Player) player)) {
                return "yes";
            } else return "no";
        }
        return null;
    }
}
