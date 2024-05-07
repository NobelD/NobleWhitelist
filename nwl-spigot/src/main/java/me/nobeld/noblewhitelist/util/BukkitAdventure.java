package me.nobeld.noblewhitelist.util;

import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.model.base.AdvPlatformManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
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
}
