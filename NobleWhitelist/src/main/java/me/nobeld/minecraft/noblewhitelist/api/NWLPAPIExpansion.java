package me.nobeld.minecraft.noblewhitelist.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.nobeld.minecraft.noblewhitelist.util.ServerUtil.toS;

public class NWLPAPIExpansion extends PlaceholderExpansion {
    private final NobleWhitelist plugin;
    public NWLPAPIExpansion(NobleWhitelist plugin) {
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
            return toS(plugin.api().whitelist());
        }
        if (params.equalsIgnoreCase("join_type")) {
            return plugin.api().getSuccessType((Player) player).string();
        }
        if (params.equalsIgnoreCase("is_whitelisted")) {
            return toS(plugin.api().isWhitelisted((Player) player));
        }
        if (params.equalsIgnoreCase("bypass")) {
            return toS(plugin.api().hasByPass((Player) player));
        }
        if (params.equalsIgnoreCase("optional_join")) {
            return toS(plugin.api().optionalJoin((Player) player));
        }
        if (params.equalsIgnoreCase("can_pass")) {
            return toS(plugin.api().canPass((Player) player));
        }
        return null;
    }
}
