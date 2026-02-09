package me.nobeld.noblewhitelist.api;

import io.github.miniplaceholders.api.Expansion;
import io.github.miniplaceholders.api.MiniPlaceholders;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.model.BPlayer;
import me.nobeld.noblewhitelist.util.ServerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.entity.Player;

import java.util.Objects;

public class NWLMiniExpansion {
    public static boolean isCompatible() { // TODO, currently behaviour is not enable if version 3.0 or greater
        try {
            MiniPlaceholders.class.getMethod("getPlatform");
            return true;
        } catch (NoSuchMethodException ignored) {
            // ignore if new version
        }
        return false;
    }

    public static void register(NobleWhitelist plugin) {
        Expansion expansion = Expansion.builder("nwhitelist")
                .globalPlaceholder("whitelist_active", (q, c) -> Tag.selfClosingInserting(Component.text(ServerUtil.toS(plugin.getApi().whitelist()))))
                .audiencePlaceholder("is_whitelisted", (a, q, c) -> {
                    final Player player = (Player) a;
                    return Tag.selfClosingInserting(Component.text(ServerUtil.toS(plugin.getApi().isWhitelisted(BPlayer.of(player)))));
                })
                .audiencePlaceholder("bypass", (a, q, c) -> {
                    final Player player = (Player) a;
                    return Tag.selfClosingInserting(Component.text(ServerUtil.toS(plugin.getApi().hasByPass(BPlayer.of(player)))));
                })
                .audiencePlaceholder("optional_join", (a, q, c) -> {
                    final Player player = (Player) a;
                    return Tag.selfClosingInserting(Component.text(ServerUtil.toS(plugin.getApi().optionalJoin(BPlayer.of(player)))));
                })
                .audiencePlaceholder("can_pass", (a, q, c) -> {
                    final Player player = (Player) a;
                    return Tag.selfClosingInserting(Component.text(ServerUtil.toS(plugin.getApi().canPass(BPlayer.of(player)))));
                })
                .audiencePlaceholder("has_discord", (a, q, c) -> {
                    final Player player = (Player) a;
                    return Tag.selfClosingInserting(Component.text(ServerUtil.toS(plugin.getApi().hasDiscordLinked(BPlayer.of(player)))));
                })
                .audiencePlaceholder("discord_id", (a, q, c) -> {
                    final Player player = (Player) a;
                    return Tag.selfClosingInserting(Component.text(plugin.getApi().getDiscordUser(BPlayer.of(player)).map(Objects::toString).orElse("none")));
                })
                .audiencePlaceholder("is_denied", (a, q, c) -> {
                    final Player player = (Player) a;
                    return Tag.selfClosingInserting(Component.text(ServerUtil.toS(plugin.getApi().isWhitelistDenied(BPlayer.of(player)))));
                })
                .build();

        expansion.register();
    }
}
