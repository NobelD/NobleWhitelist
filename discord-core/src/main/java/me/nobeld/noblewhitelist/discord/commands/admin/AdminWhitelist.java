package me.nobeld.noblewhitelist.discord.commands.admin;

import me.nobeld.noblewhitelist.discord.config.ConfigData;
import me.nobeld.noblewhitelist.discord.config.MessageData;
import me.nobeld.noblewhitelist.discord.language.CMDDescription;
import me.nobeld.noblewhitelist.discord.model.NWLDsData;
import me.nobeld.noblewhitelist.discord.model.command.BaseCommand;
import me.nobeld.noblewhitelist.discord.model.command.SubCommand;
import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.model.checking.CheckingOption;
import me.nobeld.noblewhitelist.model.checking.CheckingType;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static me.nobeld.noblewhitelist.discord.commands.CommandManager.REQUIREMENTS_KEY;
import static me.nobeld.noblewhitelist.discord.model.command.BaseCommand.getRequirements;
import static me.nobeld.noblewhitelist.discord.model.command.BaseCommand.replyMsg;
import static me.nobeld.noblewhitelist.discord.util.DiscordUtil.parseMessage;
import static org.incendo.cloud.parser.standard.EnumParser.enumParser;
import static org.incendo.cloud.parser.standard.IntegerParser.integerParser;

public class AdminWhitelist {
    private final NWLDsData data;

    public AdminWhitelist(NWLDsData data) {
        this.data = data;
    }

    public List<BaseCommand> getCommands() {
        return List.of(list(), on(), off(), permStatus(), permSet(), checkingStatus(), checkingSet());
    }

    private SubCommand list() {
        return new SubCommand(b -> b.literal("list", CMDDescription.entryList())
                .optional("page", integerParser(1))
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminList))
                .handler(c -> {
                    int page = c.getOrDefault("page", 1);

                    List<WhitelistEntry> list = data.getNWL().getApi().getIndex(page);
                    if (list != null && !list.isEmpty()) {
                        List<String> entries = new ArrayList<>();
                        for (WhitelistEntry entry : list) {
                            Map<String, String> m = data.getMessageD().baseHolder(entry);
                            entries.add(parseMessage(data.getMessageD().getMsg(MessageData.PlaceHolders.listEntry), m));
                        }
                        StringBuilder sb = new StringBuilder();
                        for (String entry : entries) {
                            sb.append("$$").append(entry).append("$$");
                        }
                        Map<String, String> m = Map.of("count_total", String.valueOf(list.size()),
                                                       "page", String.valueOf(page),
                                                       "list_entry", sb.toString().replace("$$$$", "\n").replace("$$", "")
                                                      );

                        replyMsg(data, c, MessageData.Command.listPage, m);
                    } else if (page > 1)
                        replyMsg(data, c, MessageData.Error.whitelistPageEmpty, Map.of("page", String.valueOf(page)));
                    else replyMsg(data, c, MessageData.Error.whitelistEmpty);
                })
        ) {
        };
    }

    private SubCommand on() {
        return new SubCommand(b -> b.literal("on", CMDDescription.whitelistOn())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminOn))
                .handler(c -> {
                    if (data.getNWL().getApi().whitelist(true)) {
                        replyMsg(data, c, MessageData.Command.wlOn);
                    } else
                        replyMsg(data, c, MessageData.Command.wlAlreadyOn);
                })
        ) {
        };
    }

    private SubCommand off() {
        return new SubCommand(b -> b.literal("off", CMDDescription.whitelistOff())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminOff))
                .handler(c -> {
                    if (data.getNWL().getApi().whitelist(false)) {
                        replyMsg(data, c, MessageData.Command.wlOff);
                    } else
                        replyMsg(data, c, MessageData.Command.wlAlreadyOff);
                })
        ) {
        };
    }

    private SubCommand permStatus() {
        return new SubCommand(b -> b.literal("permstatus", CMDDescription.permStatus())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminPermStatus))
                .handler(c -> {
                    PairData<Boolean, Integer> pair = data.getNWL().getApi().getPermStatus();
                    String o = pair.getFirst() ? data.getMessageD().getMsg(MessageData.PlaceHolders.enabled) : data.getMessageD().getMsg(MessageData.PlaceHolders.disabled);
                    String m = pair.getSecond() <= -1 ? data.getMessageD().getMsg(MessageData.PlaceHolders.disabled) : String.valueOf(pair.getSecond());
                    Map<String, String> p = Map.of("only_op", o, "perm_min", m);
                    replyMsg(data, c, MessageData.Command.permStatus, p);
                })
        ) {
        };
    }

    private SubCommand permSet() {
        return new SubCommand(b -> b.literal("permset", CMDDescription.permSet())
                .required("minimum", integerParser(-1))
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminPermSet))
                .handler(c -> {
                    final int min = c.get("minimum");
                    String m = min <= -1 ? data.getMessageD().getMsg(MessageData.PlaceHolders.disabled) : String.valueOf(min);
                    data.getNWL().getApi().setPermMinimum(min);
                    replyMsg(data, c, MessageData.Command.permSet, Map.of("perm_min", m));
                })
        ) {
        };
    }

    private SubCommand checkingStatus() {
        return new SubCommand(b -> b.literal("checkstatus", CMDDescription.checkingStatus())
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminCheckStatus))
                .handler(c ->
                                 replyMsg(data, c, MessageData.Command.checkStatus, Map.of(
                                                  "checking_name", cParse(data.getNWL().getConfigD().checkName()),
                                                  "checking_uuid", cParse(data.getNWL().getConfigD().checkUUID()),
                                                  "checking_perm", cParse(data.getNWL().getConfigD().checkPerm())
                                                                                          )
                                         ))
        ) {
        };
    }

    private String cParse(CheckingOption opt) {
        return switch (opt) {
            case DISABLED -> data.getMessageD().getMsg(MessageData.PlaceHolders.checkingDisabled);
            case OPTIONAL -> data.getMessageD().getMsg(MessageData.PlaceHolders.checkingOptional);
            case REQUIRED -> data.getMessageD().getMsg(MessageData.PlaceHolders.checkingRequired);
        };
    }

    private SubCommand checkingSet() {
        return new SubCommand(b -> b.literal("checkset", CMDDescription.checkingSet())
                .required("type", enumParser(CheckingType.class))
                .required("option", enumParser(CheckingOption.class))
                .meta(REQUIREMENTS_KEY, getRequirements(data, ConfigData.CommandsOpt.adminCheckSet))
                .handler(c -> {
                    final CheckingType type = c.get("type");
                    final CheckingOption opt = c.get("option");
                    data.getNWL().getConfigD().setChecking(type, opt);
                    replyMsg(data, c, MessageData.Command.checkSet, Map.of("type", type.name(), "option", opt.name()));
                })
        ) {
        };
    }
}
