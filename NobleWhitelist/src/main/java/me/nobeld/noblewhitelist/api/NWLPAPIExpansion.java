package me.nobeld.noblewhitelist.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.nobeld.noblewhitelist.model.BPlayer;
import me.nobeld.noblewhitelist.util.ServerUtil;
import me.nobeld.noblewhitelist.NobleWhitelist;
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
        // TODO return "nwhitelist";
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
            case "whitelist_active" -> ServerUtil.toS(plugin.getApi().whitelist());
            case "is_whitelisted" -> ServerUtil.toS(plugin.getApi().isWhitelisted(BPlayer.of(p)));
            case "bypass" -> ServerUtil.toS(plugin.getApi().hasByPass(BPlayer.of(p)));
            case "optional_join" -> ServerUtil.toS(plugin.getApi().optionalJoin(BPlayer.of(p)));
            case "can_pass" -> ServerUtil.toS(plugin.getApi().canPass(BPlayer.of(p)));
            case "has_discord" -> ServerUtil.toS(plugin.getApi().hasDiscordLinked(BPlayer.of(p)));
            case "discord_id" -> plugin.getApi().getDiscordUser(BPlayer.of(p)).map(Objects::toString).orElse("none");
            case "is_denied" -> ServerUtil.toS(plugin.getApi().isWhitelistDenied(BPlayer.of(p)));
            default -> null;
        };
    }
}
