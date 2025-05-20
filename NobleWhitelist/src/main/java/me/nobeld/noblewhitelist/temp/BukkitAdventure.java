package me.nobeld.noblewhitelist.temp;

import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.util.AdventureUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BukkitAdventure implements BukkitAdventureLike {
    private static BukkitAudiences adventure;
    private final NobleWhitelist data;
    public BukkitAdventure(NobleWhitelist data) {
        this.data = data;
    }
    @Override
    public void createAdventure() {
        adventure = BukkitAudiences.create(data);
    }
    public @NotNull BukkitAudiences adventure() {
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
    public Audience playerAudience(Player player) {
        return adventure().player(player);
    }

    @Override
    public Audience senderAudience(CommandSender sender) {
        return adventure().sender(sender);
    }
}
