package me.nobeld.minecraft.noblewhitelist;

import de.leonhard.storage.Yaml;
import io.papermc.lib.PaperLib;
import me.nobeld.minecraft.noblewhitelist.api.NWExpansion;
import me.nobeld.minecraft.noblewhitelist.api.NobleWhitelistApi;
import me.nobeld.minecraft.noblewhitelist.data.WhitelistChecker;
import me.nobeld.minecraft.noblewhitelist.data.WhitelistData;
import me.nobeld.minecraft.noblewhitelist.util.Metrics;
import me.nobeld.minecraft.noblewhitelist.config.ConfigManager;
import me.nobeld.minecraft.noblewhitelist.config.FileData;
import me.nobeld.minecraft.noblewhitelist.config.MessageData;
import me.nobeld.minecraft.noblewhitelist.util.ServerUtil;
import me.nobeld.minecraft.noblewhitelist.util.UpdateChecker;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

import static me.nobeld.minecraft.noblewhitelist.util.ServerUtil.*;

public class NobleWhitelist extends JavaPlugin {
    public final String version = this.getDescription().getVersion();
    private ConfigManager configManager;
    private WhitelistData whitelistData;
    private WhitelistChecker whitelistChecker;
    private UpdateChecker checker;
    private NobleWhitelistApi api;
    private static boolean hasPaper;
    private BukkitAudiences adventure;
    @Override
    public void onEnable() {
        if (!canRun(this)) return;
        hasPaper = ServerUtil.hasPaper();

        if (!hasPaper) this.adventure = BukkitAudiences.create(this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new NWExpansion(this).register();
        }
        configManager = new ConfigManager(this);
        whitelistData = new WhitelistData(this);
        whitelistChecker = new WhitelistChecker(this);
        this.api = new NobleWhitelistApi(this);

        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        PluginCommand command = getCommand("nwhitelist");
        if (command != null) {
            command.setExecutor(new NWLCommand(this));
            command.setTabCompleter(new NWLCommand(this));
        }
        consoleMsg().sendMessage(convertMsg("<prefix><green>Plugin activated, thanks for using it ^^"));
        checker = new UpdateChecker(this, version);
        if (checker.canUpdate(false)) {
            consoleMsg().sendMessage(convertMsg("<prefix><#F1B65C>There is a new version available: <#C775FF>" + checker.getLatest(), null));
            consoleMsg().sendMessage(convertMsg("<prefix><#F1B65C>Download it at #75CDFF>https://www.github.com/NobelD/NobleWhitelist/releases", null));
        }

        Metrics metrics = new Metrics(this, 20050);
        metrics.addCustomChart(new Metrics.MultiLineChart("players_and_servers", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            return valueMap;
        }));
    }
    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }
        configManager().reloadConfig();
        consoleMsg().sendMessage(convertMsg("<prefix><red>Plugin disabled, see you later!"));
    }
    public @NonNull BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }
    public Audience consoleMsg() {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        if (hasPaper) {
            return console;
        }
        return adventure().console();
    }
    public Audience playerMsg(Player player) {
        if (PaperLib.isPaper()) {
            return player;
        }
        return adventure().player(player);

    }
    public NobleWhitelistApi api() {
        return this.api;
    }
    public static boolean hasPaper() {
        return hasPaper;
    }
    public ConfigManager configManager() {
        return configManager;
    }
    public FileData fileData() {
        return configManager.fileData();
    }
    public MessageData messages() {
        return configManager.messageData();
    }
    public Yaml whitelistFile() {
        return configManager.whitelistFile();
    }
    public WhitelistData whitelistData() {
        return whitelistData;
    }
    public WhitelistChecker whitelistChecker() {
        return whitelistChecker;
    }
    public UpdateChecker getUptChecker() {
        return checker;
    }
}