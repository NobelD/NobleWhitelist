package me.nobeld.mc.noblewhitelist.discord.model.command;

import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.discord.jda5.JDAInteraction;

import java.util.List;
import java.util.function.Function;

public abstract class OptionCommand extends BaseCommand {
    private final List<BaseCommand> subCommand;
    public OptionCommand(Function<Command.Builder<JDAInteraction>, Command.Builder<JDAInteraction>> builder, List<BaseCommand> subCommand) {
        super(builder);
        this.subCommand = subCommand;
    }
    @Override
    public void register(CommandManager<JDAInteraction> mng, Command.Builder<JDAInteraction> base) {
        Command.Builder<JDAInteraction> sub = getBaseBuilder(base);

        this.getSubCommand().forEach(cmd -> {
            if (cmd instanceof OptionCommand opt) {
                opt.register(mng, sub);
            } else {
                mng.command(cmd.getCommand(sub));
            }
        });
    }
    public Command.Builder<JDAInteraction> getBaseBuilder(Command.Builder<JDAInteraction> base) {
        return this.builder.apply(base);
    }
    public List<BaseCommand> getSubCommand() {
        return subCommand;
    }
    @Override
    public Command.Builder<JDAInteraction> getCommand(Command.Builder<JDAInteraction> builder) {
        return null;
    }
}
