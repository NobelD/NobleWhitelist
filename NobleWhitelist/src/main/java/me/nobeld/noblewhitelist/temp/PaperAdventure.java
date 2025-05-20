package me.nobeld.noblewhitelist.temp;

import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PaperAdventure implements BukkitAdventureLike {
    public PaperAdventure() {
    }
    @Override
    public void createAdventure() {
    }
    @Override
    public void closeAdventure() {
    }
    @Override
    public Audience consoleAudience() {
        return Bukkit.getServer().getConsoleSender();
    }
    @Override
    public Audience playerAudience(Player player) {
        return player;
    }

    @Override
    public Audience senderAudience(CommandSender sender) {
        return sender;
    }
}
