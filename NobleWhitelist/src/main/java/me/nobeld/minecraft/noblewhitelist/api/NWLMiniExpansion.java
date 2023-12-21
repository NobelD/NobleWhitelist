package me.nobeld.minecraft.noblewhitelist.api;

import io.github.miniplaceholders.api.Expansion;
import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.entity.Player;

import java.util.Objects;

import static me.nobeld.minecraft.noblewhitelist.util.ServerUtil.toS;

public class NWLMiniExpansion {
    public NWLMiniExpansion(NobleWhitelist plugin) {
        Expansion expansion = Expansion.builder("NWhitelist")
                .globalPlaceholder("whitelist_active", (q, c) -> Tag.selfClosingInserting(Component.text(toS(plugin.api().whitelist()))))
                .audiencePlaceholder("join_type", (a, q, c) -> {
                    final Player player = (Player) a;
                    return Tag.selfClosingInserting(Component.text(plugin.api().getSuccessType(player).string()));
                })
                .audiencePlaceholder("is_whitelisted", (a, q, c) -> {
                    final Player player = (Player) a;
                    return Tag.selfClosingInserting(Component.text(toS(plugin.api().isWhitelisted(player))));
                })
                .audiencePlaceholder("bypass", (a, q, c) -> {
                    final Player player = (Player) a;
                    return Tag.selfClosingInserting(Component.text(toS(plugin.api().hasByPass(player))));
                })
                .audiencePlaceholder("optional_join", (a, q, c) -> {
                    final Player player = (Player) a;
                    return Tag.selfClosingInserting(Component.text(toS(plugin.api().optionalJoin(player))));
                })
                .audiencePlaceholder("can_pass", (a, q, c) -> {
                    final Player player = (Player) a;
                    return Tag.selfClosingInserting(Component.text(toS(plugin.api().canPass(player))));
                })
                .audiencePlaceholder("has_discord", (a, q, c) -> {
                    final Player player = (Player) a;
                    return Tag.selfClosingInserting(Component.text(toS(plugin.api().hasDiscordLinked(player))));
                })
                .audiencePlaceholder("discord_id", (a, q, c) -> {
                    final Player player = (Player) a;
                    return Tag.selfClosingInserting(Component.text(plugin.api().getDiscordUser(player).map(Objects::toString).orElse("none")));
                })
                .audiencePlaceholder("is_denied", (a, q, c) -> {
                    final Player player = (Player) a;
                    return Tag.selfClosingInserting(Component.text(toS(plugin.api().isWhitelistDenied(player))));
                })
                .build();

        expansion.register();
    }
}
