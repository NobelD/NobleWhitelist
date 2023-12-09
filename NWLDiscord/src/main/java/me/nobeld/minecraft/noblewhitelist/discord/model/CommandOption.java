package me.nobeld.minecraft.noblewhitelist.discord.model;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public record CommandOption(OptionType type, String param, String description, boolean required) {
    public OptionData build() {
        return new OptionData(type, param, description, required);
    }
}
