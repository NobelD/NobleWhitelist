package me.nobeld.noblewhitelist.model.base;

import net.kyori.adventure.audience.Audience;

public interface AdvPlatformManager {
    void createAdventure();
    void closeAdventure();
    Audience consoleAudience();
    Audience playerAudience(Object player);
}
