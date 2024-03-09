package me.nobeld.noblewhitelist.model.base;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;

public interface AdvPlatformManager {
    AudienceProvider adventure();
    void createAdventure();
    void closeAdventure();
    Audience consoleAudience();
    Audience playerAudience(Object player);
}
