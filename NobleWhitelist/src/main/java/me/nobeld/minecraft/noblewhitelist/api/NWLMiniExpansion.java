package me.nobeld.minecraft.noblewhitelist.api;

import io.github.miniplaceholders.api.Expansion;
import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import org.bukkit.entity.Player;

import static me.nobeld.minecraft.noblewhitelist.util.ServerUtil.toS;

public class NWLMiniExpansion {
    public NWLMiniExpansion(NobleWhitelist plugin) {
        Expansion expansion = Expansion.builder("NWhitelist")
                .globalPlaceholder("whitelist_active", (queque, ctx) -> Tag.selfClosingInserting(Component.text(toS(plugin.api().whitelist()))))
                .audiencePlaceholder("join_type", (aud, queque, ctx) -> {
                    final Player player = (Player) aud;
                    return Tag.selfClosingInserting(Component.text(plugin.api().getSuccessType(player).string()));
                })
                .audiencePlaceholder("is_whitelisted", (aud, queque, ctx) -> {
                    final Player player = (Player) aud;
                    return Tag.selfClosingInserting(Component.text(toS(plugin.api().isWhitelisted(player))));
                })
                .audiencePlaceholder("bypass", (aud, queque, ctx) -> {
                    final Player player = (Player) aud;
                    return Tag.selfClosingInserting(Component.text(toS(plugin.api().hasByPass(player))));
                })
                .audiencePlaceholder("optional_join", (aud, queque, ctx) -> {
                    final Player player = (Player) aud;
                    return Tag.selfClosingInserting(Component.text(toS(plugin.api().optionalJoin(player))));
                })
                .audiencePlaceholder("can_pass", (aud, queque, ctx) -> {
                    final Player player = (Player) aud;
                    return Tag.selfClosingInserting(Component.text(toS(plugin.api().canPass(player))));
                })
                .build();

        expansion.register();
    }
}
