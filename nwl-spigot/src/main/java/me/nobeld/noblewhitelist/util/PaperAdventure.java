package me.nobeld.noblewhitelist.util;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PaperAdventure extends BukkitAdventure {
    public PaperAdventure() {
        super(null);
    }

    @Override
    public void startAdventure() {
    }

    @Override
    public void closeAdventure() {
        consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><red>Plugin successfully disabled!"));
    }

    @Override
    public Audience consoleAudience() {
        return Bukkit.getConsoleSender();
    }

    @Override
    public Audience playerAudience(Object player) {
        return (Player) player;
    }

    @Override
    public Audience senderAudience(Object sender) {
        return (CommandSender) sender;
    }

    public Audience senderAudienceSp(CommandSender sender) {
        return senderAudience(sender);
    }
}
