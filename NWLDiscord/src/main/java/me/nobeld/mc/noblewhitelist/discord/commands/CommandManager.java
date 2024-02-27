package me.nobeld.mc.noblewhitelist.discord.commands;

import io.leangen.geantyref.TypeToken;
import me.nobeld.mc.noblewhitelist.discord.JDAManager;
import me.nobeld.mc.noblewhitelist.discord.commands.admin.AdminFind;
import me.nobeld.mc.noblewhitelist.discord.commands.admin.AdminModify;
import me.nobeld.mc.noblewhitelist.discord.commands.admin.AdminWhitelist;
import me.nobeld.mc.noblewhitelist.discord.commands.basic.BasicAccounts;
import me.nobeld.mc.noblewhitelist.discord.commands.basic.BasicModify;
import me.nobeld.mc.noblewhitelist.discord.config.ConfigData;
import me.nobeld.mc.noblewhitelist.discord.model.NWLDData;
import me.nobeld.mc.noblewhitelist.discord.model.command.BaseCommand;
import me.nobeld.mc.noblewhitelist.discord.model.requirement.GuildRequirement;
import me.nobeld.mc.noblewhitelist.discord.model.requirement.MemberRequirement;
import me.nobeld.mc.noblewhitelist.discord.model.requirement.NWLRequirementFailure;
import me.nobeld.mc.noblewhitelist.discord.model.requirement.NWLRequirementInterface;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import org.incendo.cloud.Command;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.discord.jda5.JDA5CommandManager;
import org.incendo.cloud.discord.jda5.JDAInteraction;
import org.incendo.cloud.discord.slash.CommandScope;
import org.incendo.cloud.discord.slash.DiscordPermission;
import org.incendo.cloud.discord.slash.DiscordSetting;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.processors.requirements.RequirementPostprocessor;
import org.incendo.cloud.processors.requirements.Requirements;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private final NWLDData data;
    private final JDAManager manager;
    private final JDA5CommandManager<JDAInteraction> cmdManager;
    public static final CloudKey<Requirements<JDAInteraction, NWLRequirementInterface>> REQUIREMENTS_KEY =
            CloudKey.of("requirements", new TypeToken<>() {
            });
    final RequirementPostprocessor<JDAInteraction, NWLRequirementInterface> postprocessor =
            RequirementPostprocessor.of(REQUIREMENTS_KEY, new NWLRequirementFailure());
    // #TODO waiting for cloud button parser for premium suggestion
    public CommandManager(NWLDData data, JDAManager manager) {
        this.data = data;
        this.manager = manager;

        cmdManager = new JDA5CommandManager<>(
                ExecutionCoordinator.asyncCoordinator(),
                JDAInteraction.InteractionMapper.identity()
        );
        cmdManager.discordSettings().set(DiscordSetting.EPHEMERAL_ERROR_MESSAGES, true);
        cmdManager.discordSettings().set(DiscordSetting.AUTO_REGISTER_SLASH_COMMANDS, false);
        cmdManager.registerCommandPostProcessor(postprocessor);
    }
    public void registerCommands(Guild guild) {
        cmdManager.registerGuildCommands(guild);
    }
    public void loadCommands() {
        GuildRequirement nr = new GuildRequirement(data);
        MemberRequirement mr = new MemberRequirement(manager);

        Command.Builder<JDAInteraction> adminBuilder = cmdManager
                .commandBuilder("wladmin", Description.of("Command for admins to manage the whitelist."))
                .apply(CommandScope.guilds())
                .meta(REQUIREMENTS_KEY, Requirements.of(nr, mr));

        if (data.getConfigD().get(ConfigData.serverManagePermission)) adminBuilder = adminBuilder.permission(DiscordPermission.of(Permission.getRaw(Permission.MANAGE_SERVER)));

        final Command.Builder<JDAInteraction> adminC = adminBuilder;

        List<BaseCommand> adminCmd = new ArrayList<>();
        adminCmd.addAll(new AdminFind(data).getCommands());
        adminCmd.addAll(new AdminModify(data).getCommands());
        adminCmd.addAll(new AdminWhitelist(data).getCommands());

        adminCmd.forEach(b -> b.register(cmdManager, adminC));

        final Command.Builder<JDAInteraction> basicBuilder = cmdManager
                .commandBuilder("whitelist", Description.of("Command for user to manage their data from the whitelist."))
                .apply(CommandScope.guilds())
                .meta(REQUIREMENTS_KEY, Requirements.of(nr, mr));

        List<BaseCommand> basicCmd = new ArrayList<>();
        basicCmd.addAll(new BasicModify(data).getCommands());
        basicCmd.addAll(new BasicAccounts(data).getCommands());

        basicCmd.forEach(b -> b.register(cmdManager, basicBuilder));
    }
    public JDA5CommandManager<JDAInteraction> getCloudManager() {
        return cmdManager;
    }
}
