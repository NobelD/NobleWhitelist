package me.nobeld.noblewhitelist.discord.model;

import me.nobeld.noblewhitelist.model.base.PlayerWrapper;
import net.dv8tion.jda.api.entities.Member;

import java.util.Optional;

public interface DiscordProvider {

    Optional<Member> getJDAMember(PlayerWrapper player);

    Optional<Long> getMemberID(PlayerWrapper player);

    boolean savePlayer(PlayerWrapper player, long id);
}
