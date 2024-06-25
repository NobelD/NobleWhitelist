package me.nobeld.noblewhitelist.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AdventureUtil {
    private static Supplier<Boolean> supplierUsePrefix = null;
    private static Supplier<String> supplierPrefix = null;
    private static final boolean usePrefix = true;
    private static final String prefix = "<bold><#17B90C>N<#7FD024>Whitelist</bold> <grey>>";

    // #TODO temporary static prefix until revamp
    public static void replaceData(Supplier<Boolean> usePrefix, Supplier<String> prefix) {
        supplierUsePrefix = usePrefix;
        supplierPrefix = prefix;
    }

    private static final MiniMessage miniSerializer = MiniMessage.builder()
            .tags(TagResolver.standard())
            .build();

    public static Component formatAll(String msg) {
        return miniSerializer.deserialize(
                msg,
                TagResolver.builder()
                        .resolver(prefixTag())
                        .resolver(MiniPlaceholdersUtil.getGlobalTagOnly())
                        .build()
                                         );
    }

    public static Component formatName(String msg, String name) {
        return miniSerializer.deserialize(
                msg,
                TagResolver.builder()
                        .resolver(prefixTag())
                        .resolver(playerName(name))
                        .resolver(MiniPlaceholdersUtil.getGlobalTagOnly())
                        .build()
                                         );
    }

    public static Component formatAudience(String msg, Audience audience) {
        return miniSerializer.deserialize(
                msg,
                TagResolver.builder()
                        .resolver(prefixTag())
                        .resolver(MiniPlaceholdersUtil.getAudienceGlobalTag(audience))
                        .build()
                                         );
    }

    private static Component base(String msg) {
        return miniSerializer.deserialize(msg);
    }

    private static boolean usePrefix() {
        if (supplierUsePrefix != null) {
            return supplierUsePrefix.get();
        }
        return usePrefix;
    }

    private static String prefix() {
        if (supplierPrefix != null && supplierPrefix.get() != null) {
            return supplierPrefix.get();
        }
        return prefix;
    }

    private static @NotNull TagResolver prefixTag() {
        final Component component = base(usePrefix() ? (prefix() + "<reset> ") : "");
        return TagResolver.resolver("prefix", Tag.selfClosingInserting(component));
    }

    private static @NotNull TagResolver playerName(String name) {
        final Component component = name == null ? Component.text("null") : Component.text(name);
        return TagResolver.resolver("name", Tag.selfClosingInserting(component));
    }
}
