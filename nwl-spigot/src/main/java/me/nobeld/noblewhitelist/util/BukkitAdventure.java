package me.nobeld.noblewhitelist.util;

import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.model.base.AdvPlatformManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitAdventure implements AdvPlatformManager {
    private final NobleWhitelist data;

    public BukkitAdventure(NobleWhitelist data) {
        this.data = data;
    }

    @Override
    public void startAdventure() {
        data.setProvider(BukkitAudiences.create(data));
    }

    @Override
    public void closeAdventure() {
        if (data.getProvider() != null) {
            consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><red>Plugin successfully disabled!"));
            data.getProvider().close();
            data.setProvider(null);
        }
    }

    @Override
    public Audience consoleAudience() {
        return data.getProvider().console();
    }

    @Override
    public Audience playerAudience(Object player) {
        return data.getProvider().player((Player) player);
    }

    @Override
    public Audience senderAudience(Object sender) {
        return data.getProvider().sender((CommandSender) sender);
    }

    public Audience senderAudienceSp(CommandSender sender) {
        return senderAudience(sender);
    }

    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
            .character('\u00a7')
            .hexColors()
            .extractUrls()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    public static String asLegacy(Component component) {
        return legacySerializer.serialize(component);
    }
}
