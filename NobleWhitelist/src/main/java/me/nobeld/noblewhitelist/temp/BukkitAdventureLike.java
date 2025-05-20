package me.nobeld.noblewhitelist.temp;

import me.nobeld.noblewhitelist.model.base.AdvPlatformManager;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface BukkitAdventureLike extends AdvPlatformManager {
    default Audience playerAudience(Object player) {
        return playerAudience((Player) player);
    }

    Audience playerAudience(Player player);

    Audience senderAudience(CommandSender sender);
}
