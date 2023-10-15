package me.nobeld.minecraft.noblewhitelist.data;

import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import org.bukkit.entity.Player;

public class WhitelistChecker {
    private final NobleWhitelist plugin;
    public WhitelistChecker(NobleWhitelist plugin) {
        this.plugin = plugin;
    }
    public void changeData(Player player) {
        if (!plugin.whitelistData().hasData(player)) {
            if (plugin.fileData().autoRegister()) plugin.whitelistData().addByPlayer(player);
            return;
        }
        plugin.whitelistData().getByPlayer(player).replaceData(player);
    }
    public WhitelistData.SuccessEnum successType(WhitelistData.SuccessData data) {
        if (data.hasAll()) return WhitelistData.SuccessEnum.ALL;
        if (data.isNormal()) return WhitelistData.SuccessEnum.NORMAL;
        if (data.onlyName()) return WhitelistData.SuccessEnum.ONLY_NAME;
        if (data.onlyUuid()) return WhitelistData.SuccessEnum.ONLY_UUID;
        if (data.onlyPerm()) return WhitelistData.SuccessEnum.BYPASS;
        return WhitelistData.SuccessEnum.NONE;
    }
    public WhitelistData.SuccessEnum successType(Player player) {
        return successType(plugin.whitelistData().getSuccess(player));
    }
    public boolean canPass(Player player) {
        WhitelistData.SuccessData data = plugin.whitelistData().registerSuccess(player);
        if (plugin.fileData().checkName() == 2 && !data.onlyName()) return false;
        if (plugin.fileData().checkUUID() == 2 && !data.onlyUuid()) return false;
        if (plugin.fileData().checkPerm() == 2 && !data.onlyPerm()) return false;
        if (plugin.fileData().checkName() == 0) return data.uuid() || data.perm();
        if (plugin.fileData().checkUUID() == 0) return data.name() || data.perm();
        if (plugin.fileData().checkPerm() == 0) return data.name() || data.uuid();
        return data.hasAny();
    }
    public boolean optionalJoin(Player player) {
        return plugin.whitelistData().registerSuccess(player).hasAny();
    }
    public boolean optionalJoin(WhitelistData.SuccessEnum type) {
        return type != WhitelistData.SuccessEnum.NONE;
    }
    public boolean isWhitelisted(Player player) {
        return plugin.whitelistData().registerSuccess(player).isWhitelisted();
    }
    public boolean isWhitelisted(WhitelistData.SuccessEnum type) {
        return type != WhitelistData.SuccessEnum.NONE && type != WhitelistData.SuccessEnum.BYPASS;
    }
    public boolean isByPass(Player player) {
        return plugin.whitelistData().registerSuccess(player).isBypass();
    }
    public boolean isByPass(WhitelistData.SuccessEnum type) {
        return type == WhitelistData.SuccessEnum.BYPASS;
    }
}
