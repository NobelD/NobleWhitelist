package me.nobeld.noblewhitelist.util;

import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class MiniPlaceholdersUtil {
    private static final boolean hasMini = JavaUtil.hasClass("io.github.miniplaceholders.api.MiniPlaceholders");
    public static TagResolver getAudienceGlobalTag(Audience audience) {
        if (!hasMini) return TagResolver.builder().build();
        return MiniPlaceholders.getAudienceGlobalPlaceholders(audience);
    }
    public static TagResolver getAudienceTagOnly(Audience audience) {
        if (!hasMini) return TagResolver.builder().build();
        return MiniPlaceholders.getAudiencePlaceholders(audience);
    }
    public static TagResolver getGlobalTagOnly() {
        if (!hasMini) return TagResolver.builder().build();
        return MiniPlaceholders.getGlobalPlaceholders();
    }
}
