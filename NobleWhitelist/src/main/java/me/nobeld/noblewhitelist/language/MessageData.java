package me.nobeld.noblewhitelist.language;

import me.nobeld.noblewhitelist.config.ConfigData;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.model.checking.CheckingOption;
import me.nobeld.noblewhitelist.model.checking.CheckingType;
import me.nobeld.noblewhitelist.model.storage.StorageType;
import me.nobeld.noblewhitelist.util.AdventureUtil;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MessageData {
    // #TODO resource bundle
    private final NWLData data;
    public MessageData(NWLData data) {
        this.data = data;
    }
    // Config
    @Nullable
    private static String parseString(@Nullable String string) {
        return string == null || string.isBlank() || string.isEmpty() ? null : string;
    }
    public Component warningNameConsole(String name) {
        return AdventureUtil.formatName(parseString(data.getConfigD().get(ConfigData.MessagesCF.nameChangeConsole)), name);
    }
    public Component warningNamePlayer(String name) {
        return AdventureUtil.formatName(parseString(data.getConfigD().get(ConfigData.MessagesCF.nameChangePlayer)), name);
    }
    public Component kickMsg(String name) {
        return AdventureUtil.formatName(parseString(data.getConfigD().get(ConfigData.MessagesCF.kickMsg)), name);
    }
    // Commands
    public static Component serverEmpty(boolean type) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The server is empty and no player was " + (type ? "added" : "removed") + ".");
    }
    public static Component serverActually(boolean type) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>All online players are actually " + (type ? "added to" : "removed from") + " the whitelist.");
    }
    public static Component serverAmount(boolean type, int total) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>Successfully " + (type ? "added" : "removed") + " <#8CDEFF>" + total + " <#FBC36F>players.");
    }
    public static Component clearSug1() {
        return AdventureUtil.formatAll("<prefix><#F46C4E>Are you sure to clear the whitelist?, this action is irreversible.");
    }
    public static Component clearSug2() {
        return AdventureUtil.formatAll("<prefix><#FBC36F>Use <#FF6040>/nwl confirm <#FBC36F>to proceed the action.");
    }
    public static Component listPage(int page) {
        return AdventureUtil.formatAll("<prefix><#FF9CBB>Whitelist Index <#B490F0>- <#99EAFE>Page<#65A8FF>: <#F07DF0>" + page);
    }
    public static Component listString(WhitelistEntry data) {
        String row;
        if (data.isWhitelisted()) row = "<#90FC4E>" + data.getRowId() + "<#39E52B> > ";
        else row = "<#F46C4E>" + data.getRowId() + "<#E3341C> > ";

        String name;
        if (data.getOptName().isPresent()) name = "<#76F2D6>" + data.getName();
        else name = "<#4E71AD>none";

        String uuid;
        if (data.getOptUUID().isPresent()) uuid = "<#F3FF8E>" + data.getUUID();
        else uuid = "<#D0845F>none";
        return AdventureUtil.formatAll(row + name + " <#F7C85D>- " + uuid);
    }
    public static Component listEmpty(int page) {
        return AdventureUtil.formatAll("<prefix><#FF9CBB>This page is empty. <#B490F0>- <#99EAFE>Page<#65A8FF>: <#F07DF0>" + page);
    }
    public static Component whitelistEmpty() {
        return AdventureUtil.formatAll("<prefix><#FF9CBB>The whitelist is empty.");
    }
    public static Component reload() {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The whitelist storage was reloaded and the config was force reloaded.");
    }
    public static Component whitelistAlreadyEmpty() {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The whitelist is already empty.");
    }
    public static Component whitelistCleared() {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The whitelist was cleared.");
    }
    public static Component statusHeader() {
        return AdventureUtil.formatAll("<prefix><#FF9CBB><bold>NWhitelist status:");
    }
    public static Component statusVersion(String version) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>Version: <#F07DF0>" + version);
    }
    public static Component statusWhitelistSize(long size) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>Whitelist size: <#F07DF0>" + size);
    }
    public static Component statusWhitelistActive(boolean fase) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>Whitelist: <#8CDEFF>" + (fase ? "<#FF6040>on" : "<#969FA5>off"));
    }
    public static Component statusNameCheck(CheckingOption check) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>Name Check: <#F07DF0>" + check.msg());
    }
    public static Component statusUuidCheck(CheckingOption check) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>UUID Check: <#F07DF0>" + check.msg());
    }
    public static Component statusPermCheck(CheckingOption check) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>Perm Check: <#F07DF0>" + check.msg());
    }
    public static Component statusStorageType(StorageType storage) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>Storage Type: <#F07DF0>" + storage.name());
    }
    public static Component onlyPlayer() {
        // #TODO use cloud sender parser instead of this message as parser
        return AdventureUtil.formatAll("<prefix><#FBC36F>This command can only be executed by a player.");
    }
    public static Component playerAdded() {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The provided player was added to the whitelist.");
    }
    public static Component playerSelfAdded() {
        return AdventureUtil.formatAll("<prefix><#FBC36F>You was added to the whitelist.");
    }
    public static Component playerAdded(String name) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The player <#99EAFE>" + name + " <#FBC36F>was added to the whitelist.");
    }
    public static Component playerAdded(UUID uuid) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The uuid <#99EAFE>" + uuid.toString() + " <#FBC36F>was added to the whitelist.");
    }
    public static Component playerSelfRemoved() {
        return AdventureUtil.formatAll("<prefix><#FBC36F>You has been removed from the whitelist.");
    }
    public static Component playerRemoved(String name) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The player <#99EAFE>" + name + " <#FBC36F>was removed from the whitelist.");
    }
    public static Component playerRemoved(UUID uuid) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The uuid <#99EAFE>" + uuid.toString() + " <#FBC36F>was removed from the whitelist.");
    }
    public static Component playerAlready(String name) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The player <#99EAFE>" + name + " <#FBC36F>is already in the whitelist.");
    }
    public static Component playerAlready() {
        return AdventureUtil.formatAll("<prefix><#FBC36F>Any of the data from this player is already registered.");
    }
    public static Component playerSelfAlready() {
        return AdventureUtil.formatAll("<prefix><#FBC36F>You are already added to the whitelist.");
    }
    public static Component playerAlready(UUID uuid) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The uuid <#99EAFE>" + uuid.toString() + " <#FBC36F>is already in the whitelist.");
    }
    public static Component playerSelfNotFound() {
        return AdventureUtil.formatAll("<prefix><#FBC36F>You are not in the whitelist.");
    }
    public static Component playerNotFound(String name) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The player <#99EAFE>" + name + " <#FBC36F>is not in the whitelist.");
    }
    public static Component playerNotFound(UUID uuid) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The player <#99EAFE>" + uuid.toString() + " <#FBC36F>is not in the whitelist.");
    }
    public static Component playerNotFound(long id) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>There is no player linked to this user id: <#99EAFE>" + id + " <#FBC36F>.");
    }
    public static Component playerToggledAlready(boolean canjoin) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>Nothing changed, the player join is already: <#F07DF0>" + canjoin);
    }
    public static Component playerToggled(String name, boolean canjoin) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The join for the player <#99EAFE>" + name + " <#FBC36F>was changed to: <#F07DF0>" + canjoin);
    }
    public static Component playerToggled(UUID uuid, boolean canjoin) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The join for the player <#99EAFE>" + uuid.toString() + " <#FBC36F>was changed to: <#F07DF0>" + canjoin);
    }
    public static Component playerToggled(long id, boolean canjoin) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The join for the user with id <#99EAFE>" + id + " <#FBC36F> was changed to: <#F07DF0>" + canjoin);
    }
    public static Component whitelistAlready(boolean status) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The whitelist is already <#99EAFE>" + (status ? "on" : "off") + ".");
    }
    public static Component whitelistChanged(boolean status) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The whitelist was set to <#99EAFE>" + (status ? "on" : "off"));
    }
    public static Component confirmationRequired() {
        return AdventureUtil.formatAll("<prefix><#FBC36F>Use <#FF6040>/nwl confirm <#FBC36F>to confirm this action.");
    }
    public static Component confirmationNoMore() {
        return AdventureUtil.formatAll("<prefix><#8CDEFF>You don't have any pending confirmation.");
    }
    public static Component playerAbout(WhitelistEntry data) {
        if (data.isSaved()) return AdventureUtil.formatAll("<prefix><#FF9CBB>Result data found: <#B490F0>- <#99EAFE>Index<#65A8FF>: <#F07DF0>" + data.getRowId());
        else return AdventureUtil.formatAll("<prefix><#FF9CBB>Result data found:");
    }
    public static Component playerAboutName(WhitelistEntry data) {
        String name;
        if (data.getOptName().isPresent()) name = "<#76F2D6>" + data.getName();
        else name = "<#4E71AD>none";
        return AdventureUtil.formatAll("<prefix><#FBC36F>Name: " + name);
    }
    public static Component playerAboutUuid(WhitelistEntry data) {
        String uuid;
        if (data.getOptUUID().isPresent()) uuid = "<#F3FF8E>" + data.getUUID();
        else uuid = "<#D0845F>none";
        return AdventureUtil.formatAll("<prefix><#FBC36F>UUID: " + uuid);
    }
    public static Component playerAboutUser(WhitelistEntry data) {
        String id = data.hasDiscord() ? "<#FC73C6>" + data.getDiscordID() : "<#C077CA>none";
        return AdventureUtil.formatAll("<prefix><#FBC36F>Discord ID: " + id);
    }
    public static Component playerAboutJoin(WhitelistEntry data) {
        String color = data.isWhitelisted() ? "<#90FC4E>" : "<#F46C4E>";
        return AdventureUtil.formatAll("<prefix><#FBC36F>Can join: " + color + data.isWhitelisted());
    }
    public static Component permissionInf1(NWLData data) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>Only OP: <#FF6040>" + (data.getConfigD().get(ConfigData.WhitelistCF.onlyOpPerm) ? "enabled" : "disabled" ));
    }
    public static Component permissionInf2(NWLData data) {
        int min = data.getConfigD().get(ConfigData.WhitelistCF.permissionMinimum);
        if (min <= -1) return AdventureUtil.formatAll("<prefix><#FBC36F>Permission minimum: <#FF6040>disabled");
        return AdventureUtil.formatAll("<prefix><#FBC36F>Permission minimum: <#FF6040>" + min);
    }
    public static Component permissionChanged(int minimum) {
        if (minimum <= -1) return AdventureUtil.formatAll("<prefix><#FBC36F>The permission minimum was disabled (-1)");
        return AdventureUtil.formatAll("<prefix><#FBC36F>The permission minimum was changed to: <#FF6040>" + minimum);
    }
    public static Component checkingAlready(CheckingType type, CheckingOption option) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The checking type of <#FF6040>'" + type.name() + "' <#FF6040>already has the value of '" + option.name() + "'");
    }
    public static Component checkingChange(CheckingType type, CheckingOption option) {
        return AdventureUtil.formatAll("<prefix><#FBC36F>The checking type of <#FF6040>'" + type.name() + "' <#FF6040>was changed to '" + option.name() + "'");
    }
    public static Component permissionCheckHeader(String name) {
        return AdventureUtil.formatAll("<prefix><#FF9CBB>Permission check result for: <#76F2D6>" + name);
    }
    public static Component permissionCheckOP(boolean op) {
        String color = op ? "<#90FC4E>" : "<#F46C4E>";
        return AdventureUtil.formatAll("<prefix><#FBC36F>OP Player: " + color + op);
    }
    public static Component permissionCheckByPass(boolean has) {
        String color = has ? "<#90FC4E>" : "<#F46C4E>";
        return AdventureUtil.formatAll("<prefix><#FBC36F>Full Bypass: " + color + has);
    }
    public static Component permissionCheckByPassMin(boolean has, int compared) {
        String color = has ? "<#90FC4E>" : "<#F46C4E>";
        return AdventureUtil.formatAll("<prefix><#FBC36F>Min Bypass: " + color + has + " <#4E71AD>(<#76F2D6>" + compared + "<#4E71AD>)");
    }
}
