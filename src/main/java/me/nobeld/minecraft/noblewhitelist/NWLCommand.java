package me.nobeld.minecraft.noblewhitelist;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"SpellCheckingInspection"})
public class NWLCommand implements CommandExecutor, TabCompleter {
    private final NobleWhitelist plugin;
    public NWLCommand(NobleWhitelist plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp() || !sender.hasPermission("noblewhitelist.admin")) return false;
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("add")) {
                if (args.length >= 2 && args[1].equalsIgnoreCase("allOnline")) {
                    int total = 0;
                    if (Bukkit.getOnlinePlayers().isEmpty()) sendMsg(sender, plugin.messages().serverEmpty());
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (plugin.whitelistData().addByPlayer(player)) total++;
                    }
                    if (total == 0) sendMsg(sender, plugin.messages().serverActuallyAdded());
                    else sendMsg(sender, plugin.messages().amountPlayersAdded(total));
                    return true;
                }
                if (args.length >= 2 && modifyWhitelist(sender, args, true)) return true;
                sendMsg(sender, plugin.messages().invalidInput());
                return true;
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (args.length >= 2 && modifyWhitelist(sender, args, false)) return true;
                sendMsg(sender, plugin.messages().invalidInput());
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                if (args.length >= 2 && args[1].equalsIgnoreCase("clear")) {
                    sendMsg(sender, plugin.messages().clearSug1());
                    sendMsg(sender, plugin.messages().clearSug2());
                    return true;
                }
                Map<String, String> list = plugin.whitelistData().getWhitelist();
                if (list != null && !list.isEmpty()) {
                    if (list.size() > 70) {
                        sendMsg(sender, plugin.messages().listExced(list.size()));
                        return true;
                    }
                    if (list.size() >= 12) {
                        List<String> total = new ArrayList<>();
                        list.forEach((name, uuid) -> {
                            if (name.equals("none")) return;
                            total.add(name);
                        });
                        int ouuid = list.size() - total.size();
                        sendMsg(sender, plugin.messages().listSkip(ouuid));
                        sendMsg(sender, plugin.messages().listName(total));
                        return true;
                    }
                    sendMsg(sender, plugin.messages().listAmount(list.size()));
                    list.forEach((name, uuid) -> sendMsg(sender, plugin.messages().listString(name, uuid)));
                    return true;
                }
                sendMsg(sender, plugin.messages().whitelistEmpty());
                return true;
            } else if (args[0].equalsIgnoreCase("on")) {
                activeStatus(sender, true);
                return true;
            } else if (args[0].equalsIgnoreCase("off")) {
                activeStatus(sender, false);
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                plugin.whitelistData().loadWhitelist();
                plugin.configManager().reloadConfig();
                sendMsg(sender, plugin.messages().reload());
                return true;
            } else if (args[0].equalsIgnoreCase("forceclear")) {
                if (!plugin.whitelistData().clearWhitelist()) sendMsg(sender, plugin.messages().whitelistAlreadyEmpty());
                else sendMsg(sender, plugin.messages().whitelistCleared());
                return true;
            } else if (args[0].equalsIgnoreCase("status")) {
                Map<?, ?> list = plugin.whitelistData().getWhitelist();
                sendMsg(sender, plugin.messages().statusHeader());
                sendMsg(sender, plugin.messages().statusVersion(plugin.version));
                sendMsg(sender, plugin.messages().statusWhitelistSize(list.size()));
                sendMsg(sender, plugin.messages().statusWhitelistActive(plugin.fileData().whitelistActive() ? "<#F46C4E>on" : "<#969FA5>off"));
                sendMsg(sender, plugin.messages().statusNameCheck(plugin.fileData().checkNameString()));
                sendMsg(sender, plugin.messages().statusUuidCheck(plugin.fileData().checkUUIDString()));
                sendMsg(sender, plugin.messages().statusPermCheck(plugin.fileData().checkPermString()));
                return true;
            }
        }
        help(sender);
        return false;
    }
    public boolean modifyWhitelist(CommandSender sender, String[] args, boolean type) {
        if (!args[1].equalsIgnoreCase("name") && !args[1].equalsIgnoreCase("uuid")) return false;
        String baseType = args[1].toLowerCase();
        boolean bType = baseType.equalsIgnoreCase("name");
        if (args.length == 2) {
            sendMsg(sender, plugin.messages().invalidPlayerInput(baseType));
            return true;
        }
        Player onlinePlayer = null;
        boolean mType = args[0].equalsIgnoreCase("add");
        boolean parse;
        if (bType) {
            if (mType) for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().equalsIgnoreCase(args[2])) onlinePlayer = player;
                }
            if (onlinePlayer != null) parse = plugin.whitelistData().addByPlayer(onlinePlayer);
            else parse = plugin.whitelistData().modifyByName(args[2], type);
        }
        else parse = plugin.whitelistData().modifyByUUID(args[2], type);

        if (parse) {
            if (mType) sendMsg(sender, plugin.messages().playerAdded(args[2]));
            else sendMsg(sender, plugin.messages().playerRemoved(args[2]));
        } else {
            if (mType) sendMsg(sender, plugin.messages().playerAlready(args[2]));
            else sendMsg(sender, plugin.messages().playerNotFound(args[2]));
        }
        return true;
    }
    public void activeStatus(CommandSender sender, boolean activate) {
        String msg;
        boolean actually = plugin.fileData().whitelistActive();
        if (activate) msg = "on";
        else msg = "off";

        if (activate == actually) {
            sendMsg(sender, plugin.messages().whitelistAlready(msg));
            return;
        }
        plugin.fileData().setConfig("enabled", activate);
        sendMsg(sender, plugin.messages().whitelistChanged(msg));
    }
    public void help(CommandSender sender) {
        sendMsg(sender, plugin.messages().helpHeader());
        sendMsg(sender, plugin.messages().helpAdd());
        sendMsg(sender, plugin.messages().helpRemove());
        sendMsg(sender, plugin.messages().helpList());
        sendMsg(sender, plugin.messages().helpOn());
        sendMsg(sender, plugin.messages().helpOff());
        sendMsg(sender, plugin.messages().helpReload());
    }
    public void sendMsg(CommandSender sender, Component msg) {
        sender(sender).sendMessage(msg);
    }
    public Audience sender(CommandSender sender) {
        if (NobleWhitelist.hasPaper()) return sender;
        if (sender instanceof Player player) {
            return plugin.adventure().player(player);
        }
        if (sender instanceof ConsoleCommandSender) {
            return plugin.adventure().console();
        }
        return null;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp() || !sender.hasPermission("noblewhitelist.admin")) return null;
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            List<String> commands = new ArrayList<>();
            commands.add("add");
            commands.add("remove");
            commands.add("list");
            commands.add("on");
            commands.add("off");
            commands.add("reload");
            commands.add("status");
            for (String c : commands) {
                if (args[0].isEmpty() || c.startsWith(args[0].toLowerCase()))
                    completions.add(c);
            }
            return completions;
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                List<String> completions = new ArrayList<>();
                List<String> subcom = new ArrayList<>();
                subcom.add("name");
                subcom.add("uuid");
                subcom.add("allonline");
                for (String c : subcom) {
                    if (args[1].isEmpty() || c.startsWith(args[1].toLowerCase()))
                        completions.add(c);
                }
                return completions;
            } else if (args[0].equalsIgnoreCase("list")) {
                List<String> completions = new ArrayList<>();
                List<String> subcom = new ArrayList<>();
                subcom.add("clear");
                for (String c : subcom) {
                    if (args[1].isEmpty() || c.startsWith(args[1].toLowerCase()))
                        completions.add(c);
                }
                return completions;
            }
        } else if (args.length == 3) {
            if (args[1].equalsIgnoreCase("name")) {
                if (args[0].equalsIgnoreCase("remove")) {
                    Map<String, String> list = plugin.whitelistData().getWhitelist();
                    if (list != null && !list.isEmpty()) {
                        List<String> completions = new ArrayList<>();
                        List<String> subcom = new ArrayList<>();
                        list.forEach((name, uuid) -> {
                            if (name.startsWith("none$")) return;
                            if (subcom.size() < 100) subcom.add(name);
                        });
                        for (String c : subcom) {
                            if (args[2].isEmpty() || c.startsWith(args[2].toLowerCase()))
                                completions.add(c);
                        }
                        return completions;
                    }
                }
                if (args[0].equalsIgnoreCase("add")) {
                    List<String> completions = new ArrayList<>();
                    List<String> subcom = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!plugin.whitelistData().hasName(player.getName())) {
                            if (subcom.size() < 100) subcom.add(player.getName());
                        }
                    }
                    for (String c : subcom) {
                        if (args[2].isEmpty() || c.startsWith(args[2].toLowerCase()))
                            completions.add(c);
                    }
                    if (completions.isEmpty()) completions.add("<name>");
                    return completions;
                }
                List<String> completions = new ArrayList<>();
                completions.add("<name>");
                return completions;
            } else if (args[1].equalsIgnoreCase("uuid")) {
                if (args[0].equalsIgnoreCase("remove")) {
                    Map<String, String> list = plugin.whitelistData().getWhitelist();
                    if (list != null && !list.isEmpty()) {
                        List<String> completions = new ArrayList<>();
                        List<String> subcom = new ArrayList<>();
                        list.forEach((name, uuid) -> {
                            if (uuid.equalsIgnoreCase("none")) return;
                            if (subcom.size() < 100) subcom.add(uuid);
                        });
                        for (String c : subcom) {
                            if (args[2].isEmpty() || c.startsWith(args[2].toLowerCase()))
                                completions.add(c);
                        }
                        return completions;
                    }
                }
                if (args[0].equalsIgnoreCase("add")) {
                    List<String> completions = new ArrayList<>();
                    List<String> subcom = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (!plugin.whitelistData().hasUUID(player.getUniqueId().toString())) {
                            if (subcom.size() < 100) subcom.add(player.getUniqueId().toString());
                        }
                    }
                    for (String c : subcom) {
                        if (args[2].isEmpty() || c.startsWith(args[2].toLowerCase()))
                            completions.add(c);
                    }
                    if (completions.isEmpty()) completions.add("<uuid>");
                    return completions;
                }
                List<String> completions = new ArrayList<>();
                completions.add("<uuid>");
                return completions;
            }
        }
        return new ArrayList<>();
    }
}
