package me.nobeld.minecraft.noblewhitelist.discord.commands;

import me.nobeld.minecraft.noblewhitelist.discord.JDAManager;
import me.nobeld.minecraft.noblewhitelist.discord.commands.admin.CommandAdmin;
import me.nobeld.minecraft.noblewhitelist.discord.commands.basic.CommandBasic;
import me.nobeld.minecraft.noblewhitelist.discord.config.ConfigData;
import me.nobeld.minecraft.noblewhitelist.discord.model.CommandGeneral;
import me.nobeld.minecraft.noblewhitelist.discord.model.InteractResult;
import me.nobeld.minecraft.noblewhitelist.discord.model.SubCommand;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NWLCommands extends ListenerAdapter {
    public static Map<Long, PlayerWhitelisted> commandQueue = new HashMap<>();
    private final List<CommandGeneral> commands = new ArrayList<>();
    private final JDAManager manager;
    public NWLCommands(JDAManager manager) {
        this.manager = manager;
        commands.add(new CommandBasic());
        commands.add(new CommandAdmin());

        registerCommands(manager);
    }
    public Map<Long, PlayerWhitelisted> getCommandQueue() {
        return commandQueue;
    }
    public void registerCommands(JDAManager manager) {
        List<CommandData> commandsList = new ArrayList<>();
        for (CommandGeneral com : commands) {
            if (com.getPermission() == null) commandsList.add(com.build());
            else commandsList.add(com.build().setDefaultPermissions(com.getPermission()));
        }
        manager.getCommandsList().addAll(commandsList);
    }
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getGuild() == null || event.getGuild().getIdLong() != ConfigData.get(ConfigData.serverID)) {
            event.reply("This command can not be used here.").setEphemeral(true).queue();
            return;
        }
        if (event.getUser().isBot()) {
            event.reply("Other bots can not execute this commands.").setEphemeral(true).queue();
            return;
        }
        Member member = event.getMember();
        if (member == null) {
            event.reply("This command can only be executed by valid members.").setEphemeral(true).queue();
            return;
        }

        String command = event.getName();
        String subCommand = event.getSubcommandName();

        for (CommandGeneral gen : commands) {
            if (!gen.getName().equalsIgnoreCase(command)) continue;
            boolean sub = false;
            if (!gen.notSubcommands()) {
                assert gen.getSubcommands() != null;
                for (SubCommand com : gen.getSubcommands()) {
                    if (!com.getName().equalsIgnoreCase(subCommand)) continue;
                    com.onCommand(new InteractResult(manager, member, com, event));
                    sub = true;
                    break;
                }
            }
            if (!sub) gen.onCommand(event);
            break;
        }
    }
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        //TODO premium suggestion
        if (event.getComponentId().equals("nwl_suggest_yes")) {
            event.reply("yes action").queue();
        } else if (event.getComponentId().equals("nwl_suggest_no")) {
            event.reply("no action").queue();
        }
    }
}
