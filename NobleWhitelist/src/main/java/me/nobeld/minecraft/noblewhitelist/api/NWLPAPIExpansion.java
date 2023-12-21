package me.nobeld.minecraft.noblewhitelist.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
        return plugin.getUptChecker().version;
    }
    @Override
    public boolean persist() {
        return true;
    }
    @Override
    public String onRequest(OfflinePlayer player, String params) {
        Player p = (Player) player;

        return switch (params.toLowerCase()) {
            case "whitelist_active" -> toS(plugin.api().whitelist());
            case "join_type" -> plugin.api().getSuccessType(p).string();
            case "is_whitelisted" -> toS(plugin.api().isWhitelisted(p));
            case "bypass" -> toS(plugin.api().hasByPass(p));
            case "optional_join" -> toS(plugin.api().optionalJoin(p));
            case "can_pass" -> toS(plugin.api().canPass(p));
            case "has_discord" -> toS(plugin.api().hasDiscordLinked(p));
            case "discord_id" -> plugin.api().getDiscordUser(p).map(Objects::toString).orElse("none");
            case "is_denied" -> toS(plugin.api().isWhitelistDenied(p));
            default -> null;
        };
    }
}
