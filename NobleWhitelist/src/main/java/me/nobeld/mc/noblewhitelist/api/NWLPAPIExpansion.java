package me.nobeld.mc.noblewhitelist.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.nobeld.mc.noblewhitelist.util.ServerUtil;
import me.nobeld.mc.noblewhitelist.NobleWhitelist;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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
        return plugin.version();
    }
    @Override
    public boolean persist() {
        return true;
    }
    @Override
    public String onRequest(OfflinePlayer player, String params) {
        Player p = (Player) player;

        return switch (params.toLowerCase()) {
            case "whitelist_active" -> ServerUtil.toS(plugin.api().whitelist());
            case "join_type" -> plugin.api().getSuccessType(p).string();
            case "is_whitelisted" -> ServerUtil.toS(plugin.api().isWhitelisted(p));
            case "bypass" -> ServerUtil.toS(plugin.api().hasByPass(p));
            case "optional_join" -> ServerUtil.toS(plugin.api().optionalJoin(p));
            case "can_pass" -> ServerUtil.toS(plugin.api().canPass(p));
            case "has_discord" -> ServerUtil.toS(plugin.api().hasDiscordLinked(p));
            case "discord_id" -> plugin.api().getDiscordUser(p).map(Objects::toString).orElse("none");
            case "is_denied" -> ServerUtil.toS(plugin.api().isWhitelistDenied(p));
            default -> null;
        };
    }
}
