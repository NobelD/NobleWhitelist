package me.nobeld.minecraft.noblewhitelist.data;

import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import me.nobeld.minecraft.noblewhitelist.api.AutoWhitelistEvent;
import me.nobeld.minecraft.noblewhitelist.config.ConfigFile;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.SuccessData;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.SuccessEnum;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class WhitelistChecker {
    private final NobleWhitelist plugin;
    public WhitelistChecker(NobleWhitelist plugin) {
        this.plugin = plugin;
    }
    public void changeData(Player player) {
        Optional<PlayerWhitelisted> data = plugin.whitelistData().getData(player);
        if (data.isEmpty()) {
            if (ConfigFile.getConfig(ConfigFile.autoRegister)) {
                plugin.whitelistData().addOptPlayer(player)
                        .ifPresent(d -> Bukkit.getPluginManager().callEvent(new AutoWhitelistEvent(player, d)));
            }
        } else plugin.whitelistData().replaceData(data.get(), player);
    }
    public SuccessEnum successType(Player player) {
        SuccessData suc = plugin.whitelistData().getSuccess(player);
        if (suc == null) return SuccessEnum.UNKNOWN;
        return suc.successEnum();
    }
    public boolean canPass(Player player) {
        PlayerWhitelisted data = plugin.getStorage().loadPlayer(player);
        if (data == null) return false;
        SuccessData suc = plugin.whitelistData().registerSuccess(data, player);

        if (plugin.whitelistData().checkData(data, player).nameDiffUuid() && ConfigFile.getConfig(ConfigFile.enforceNameDiffID)) return false;

        ConfigFile.CheckType name = ConfigFile.checkName();
        ConfigFile.CheckType uuid = ConfigFile.checkUUID();
        ConfigFile.CheckType perm = ConfigFile.checkPerm();

        if (name.isRequired() && !suc.onlyName()) return false;
        if (uuid.isRequired() && !suc.onlyUuid()) return false;
        if (perm.isRequired() && !suc.onlyPerm()) return false;
        if (name.isDisabled()) return suc.uuid() || suc.perm();
        if (uuid.isDisabled()) return suc.name() || suc.perm();
        if (perm.isDisabled()) return suc.name() || suc.uuid();
        return suc.hasAny();
    }
}
