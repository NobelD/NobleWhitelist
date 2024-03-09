package me.nobeld.noblewhitelist.util;

import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.model.base.AdvPlatformManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BukkitAdventure implements AdvPlatformManager {
    private static BukkitAudiences adventure;
    private final NobleWhitelist data;
    public BukkitAdventure(NobleWhitelist data) {
        this.data = data;
    }
    @Override
    public void createAdventure() {
        adventure = BukkitAudiences.create(data);
    }
    @Override
    public @NonNull BukkitAudiences adventure() {
        if (adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }
    @Override
    public void closeAdventure() {
        if (adventure != null) {
            consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><red>Plugin successfully disabled!"));
            adventure.close();
            adventure = null;
        }
    }
    @Override
    public Audience consoleAudience() {
        return adventure().console();
    }
    @Override
    public Audience playerAudience(Object player) {
        return adventure().player((Player) player);
    }
}
