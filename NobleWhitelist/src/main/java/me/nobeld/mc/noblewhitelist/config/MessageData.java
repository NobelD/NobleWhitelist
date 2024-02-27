package me.nobeld.mc.noblewhitelist.config;

import me.nobeld.mc.noblewhitelist.model.base.NWLData;
import me.nobeld.mc.noblewhitelist.model.storage.StorageType;
import me.nobeld.mc.noblewhitelist.util.AdventureUtil;
import me.nobeld.mc.noblewhitelist.model.whitelist.WhitelistEntry;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class MessageData {
    private final NWLData data;
    public MessageData(NWLData data) {
        this.data = data;
    }
    public Component warningNameConsole(String name) {
        return AdventureUtil.formatName(data.getConfigD().get(ConfigData.MessagesCF.nameChangeConsole), name);
    }
    public Component warningNamePlayer(String name) {
        return AdventureUtil.formatName(data.getConfigD().get(ConfigData.MessagesCF.nameChangePlayer), name);
    }
    public Component kickMsg(String name) {
        return AdventureUtil.formatName(data.getConfigD().get(ConfigData.MessagesCF.kickMsg), name);
    }
    public static Component serverEmpty(boolean type) {
        return AdventureUtil.formatName("<prefix><#FBC36F>The server is empty and no player was " + (type ? "added" : "removed") + ".", null);
    }
    public static Component serverActually(boolean type) {
        return AdventureUtil.formatName("<prefix><#FBC36F>All online players are actually " + (type ? "added to" : "removed from") + " the whitelist.", null);
    }
    public static Component serverAmount(boolean type, int total) {
        return AdventureUtil.formatName("<prefix><#FBC36F>Successfully " + (type ? "added" : "removed") + " <#8CDEFF>" + total + " <#FBC36F>players.", null);
    }
    public static Component clearSug1() {
        return AdventureUtil.formatName("<prefix><#F46C4E>Are you sure to clear the whitelist?, this action is irreversible.", null);
    }
    public static Component clearSug2() {
        return AdventureUtil.formatName("<prefix><#FBC36F>Use <#FF6040>/nwl confirm <#FBC36F>to proceed the action.", null);
    }
    public static Component listPage(int page) {
        return AdventureUtil.formatName("<prefix><#FF9CBB>Whitelist Index <#B490F0>- <#99EAFE>Page<#65A8FF>: <#F07DF0>" + page, null);
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
        return AdventureUtil.formatName(row + name + " <#F7C85D>- " + uuid, null);
    }
    public static Component listEmpty(int page) {
        return AdventureUtil.formatName("<prefix><#FF9CBB>This page is empty. <#B490F0>- <#99EAFE>Page<#65A8FF>: <#F07DF0>" + page, null);
    }
    public static Component whitelistEmpty() {
        return AdventureUtil.formatName("<prefix><#FF9CBB>The whitelist is empty.", null);
    }
    public static Component reload() {
        return AdventureUtil.formatName("<prefix><#FBC36F>The whitelist was reloaded. <#F79559>(is not necessary unless editing the whitelist file!)", null);
    }
    public static Component whitelistAlreadyEmpty() {
        return AdventureUtil.formatName("<prefix><#FBC36F>The whitelist is already empty.", null);
    }
    public static Component whitelistCleared() {
        return AdventureUtil.formatName("<prefix><#FBC36F>The whitelist was cleared.", null);
    }
    public static Component statusHeader() {
        return AdventureUtil.formatName("<prefix><#FF9CBB><bold>NWhitelist status:", null);
    }
    public static Component statusVersion(String version) {
        return AdventureUtil.formatName("<prefix><#FBC36F>Version: <#F07DF0>" + version, null);
    }
    public static Component statusWhitelistSize(long size) {
        return AdventureUtil.formatName("<prefix><#FBC36F>Whitelist size: <#F07DF0>" + size, null);
    }
    public static Component statusWhitelistActive(boolean fase) {
        return AdventureUtil.formatName("<prefix><#FBC36F>Whitelist: <#8CDEFF>" + (fase ? "<#FF6040>on" : "<#969FA5>off"), null);
    }
    public static Component statusNameCheck(ConfigData.CheckType check) {
        return AdventureUtil.formatName("<prefix><#FBC36F>Name Check: <#F07DF0>" + check.msg(), null);
    }
    public static Component statusUuidCheck(ConfigData.CheckType check) {
        return AdventureUtil.formatName("<prefix><#FBC36F>UUID Check: <#F07DF0>" + check.msg(), null);
    }
    public static Component statusPermCheck(ConfigData.CheckType check) {
        return AdventureUtil.formatName("<prefix><#FBC36F>Perm Check: <#F07DF0>" + check.msg(), null);
    }
    public static Component statusStorageType(StorageType storage) {
        return AdventureUtil.formatName("<prefix><#FBC36F>Storage Type: <#F07DF0>" + storage.name(), null);
    }
    public static Component onlyPlayer() {
        // #TODO use cloud sender parser instead of this message as parser
        return AdventureUtil.formatName("<prefix><#FBC36F>This command can only be executed by a player.", null);
    }
    public static Component playerAdded() {
        return AdventureUtil.formatName("<prefix><#FBC36F>The provided player was added to the whitelist.", null);
    }
    public static Component playerSelfAdded() {
        return AdventureUtil.formatName("<prefix><#FBC36F>You was added to the whitelist.", null);
    }
    public static Component playerAdded(String name) {
        return AdventureUtil.formatName("<prefix><#FBC36F>The player <#99EAFE>" + name + " <#FBC36F>was added to the whitelist.", null);
    }
    public static Component playerAdded(UUID uuid) {
        return AdventureUtil.formatName("<prefix><#FBC36F>The uuid <#99EAFE>" + uuid.toString() + " <#FBC36F>was added to the whitelist.", null);
    }
    public static Component playerSelfRemoved() {
        return AdventureUtil.formatName("<prefix><#FBC36F>You has been removed from the whitelist.", null);
    }
    public static Component playerRemoved(String name) {
        return AdventureUtil.formatName("<prefix><#FBC36F>The player <#99EAFE>" + name + " <#FBC36F>was removed from the whitelist.", null);
    }
    public static Component playerRemoved(UUID uuid) {
        return AdventureUtil.formatName("<prefix><#FBC36F>The uuid <#99EAFE>" + uuid.toString() + " <#FBC36F>was removed from the whitelist.", null);
    }
    public static Component playerAlready(String name) {
        return AdventureUtil.formatName("<prefix><#FBC36F>The player <#99EAFE>" + name + " <#FBC36F>is already in the whitelist.", null);
    }
    public static Component playerAlready() {
        return AdventureUtil.formatName("<prefix><#FBC36F>Any of the data from this player is already registered.", null);
    }
    public static Component playerSelfAlready() {
        return AdventureUtil.formatName("<prefix><#FBC36F>You are already added to the whitelist.", null);
    }
    public static Component playerAlready(UUID uuid) {
        return AdventureUtil.formatName("<prefix><#FBC36F>The uuid <#99EAFE>" + uuid.toString() + " <#FBC36F>is already in the whitelist.", null);
    }
    public static Component playerSelfNotFound() {
        return AdventureUtil.formatName("<prefix><#FBC36F>You are not in the whitelist.", null);
    }
    public static Component playerNotFound(String name) {
        return AdventureUtil.formatName("<prefix><#FBC36F>The player <#99EAFE>" + name + " <#FBC36F>is not in the whitelist.", null);
    }
    public static Component playerNotFound(UUID uuid) {
        return AdventureUtil.formatName("<prefix><#FBC36F>The player <#99EAFE>" + uuid.toString() + " <#FBC36F>is not in the whitelist.", null);
    }
    public static Component playerNotFound(long id) {
        return AdventureUtil.formatName("<prefix><#FBC36F>There is no player linked to this user id: <#99EAFE>" + id + " <#FBC36F>.", null);
    }
    public static Component playerToggledAlready(boolean canjoin) {
        return AdventureUtil.formatName("<prefix><#FBC36F>Nothing changed, the player join is already: <#F07DF0>" + canjoin, null);
    }
    public static Component playerToggled(String name, boolean canjoin) {
        return AdventureUtil.formatName("<prefix><#FBC36F>The join for the player <#99EAFE>" + name + " <#FBC36F>was changed to: <#F07DF0>" + canjoin, null);
    }
    public static Component playerToggled(UUID uuid, boolean canjoin) {
        return AdventureUtil.formatName("<prefix><#FBC36F>The join for the player <#99EAFE>" + uuid.toString() + " <#FBC36F>was changed to: <#F07DF0>" + canjoin, null);
    }
    public static Component playerToggled(long id, boolean canjoin) {
        return AdventureUtil.formatName("<prefix><#FBC36F>The join for the user with id <#99EAFE>" + id + " <#FBC36F> was changed to: <#F07DF0>" + canjoin, null);
    }
    public static Component whitelistAlready(boolean status) {
        return AdventureUtil.formatName("<prefix><#FBC36F>The whitelist is already <#99EAFE>" + (status ? "on" : "off") + ".", null);
    }
    public static Component whitelistChanged(boolean status) {
        return AdventureUtil.formatName("<prefix><#FBC36F>The whitelist was set to <#99EAFE>" + (status ? "on" : "off"), null);
    }
    public static Component confirmationRequired() {
        return AdventureUtil.formatName("<prefix><#FBC36F>Use <#FF6040>/nwl confirm <#FBC36F>to confirm this action.", null);
    }
    public static Component confirmationNoMore() {
        return AdventureUtil.formatName("<prefix><#8CDEFF>You don't have any pending confirmation.", null);
    }
    public static Component playerAbout(WhitelistEntry data) {
        if (data.isSaved()) return AdventureUtil.formatName("<prefix><#FF9CBB>This user was found: <#B490F0>- <#99EAFE>Index<#65A8FF>: <#F07DF0>" + data.getRowId(), null);
        else return AdventureUtil.formatName("<prefix><#FF9CBB>This user was found:", null);
    }
    public static Component playerAboutName(WhitelistEntry data) {
        String name;
        if (data.getOptName().isPresent()) name = "<#76F2D6>" + data.getName();
        else name = "<#4E71AD>none";
        return AdventureUtil.formatName("<prefix><#FBC36F>Name: " + name, null);
    }
    public static Component playerAboutUuid(WhitelistEntry data) {
        String uuid;
        if (data.getOptUUID().isPresent()) uuid = "<#F3FF8E>" + data.getUUID();
        else uuid = "<#D0845F>none";
        return AdventureUtil.formatName("<prefix><#FBC36F>UUID: " + uuid, null);
    }
    public static Component playerAboutUser(WhitelistEntry data) {
        String id;
        if (data.hasDiscord()) id = "<#FC73C6>" + data.getDiscordID();
        else id = "<#C077CA>none";
        return AdventureUtil.formatName("<prefix><#FBC36F>Discord ID: " + id, null);
    }
    public static Component playerAboutJoin(WhitelistEntry data) {
        String color;
        if (data.isWhitelisted()) color = "<#90FC4E>";
        else color = "<#F46C4E>";
        return AdventureUtil.formatName("<prefix><#FBC36F>Can join: " + color + data.isWhitelisted(), null);
    }
}
