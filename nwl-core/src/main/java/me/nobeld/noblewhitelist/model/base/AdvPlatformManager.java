package me.nobeld.noblewhitelist.model.base;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;

public interface AdvPlatformManager {
    void startAdventure();
    void closeAdventure();
    Audience consoleAudience();
    Audience playerAudience(Object player);
    Audience senderAudience(Object sender);
}
