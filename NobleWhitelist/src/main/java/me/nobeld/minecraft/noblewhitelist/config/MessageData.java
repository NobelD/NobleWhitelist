package me.nobeld.minecraft.noblewhitelist.config;

import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import me.nobeld.minecraft.noblewhitelist.util.ServerUtil;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public class MessageData {
    public static Component warningNameConsole(String name) {
        return ServerUtil.formatAll(ConfigFile.getConfig(ConfigFile.nameChangeConsole), name);
    }
    public static Component warningNamePlayer(String name) {
        return ServerUtil.formatAll(ConfigFile.getConfig(ConfigFile.nameChangePlayer), name);
    }
    public static Component kickMsg(String name) {
        return ServerUtil.formatAll(ConfigFile.getConfig(ConfigFile.kickMsg), name);
    }
    public static Component serverEmpty(boolean type) {
        return ServerUtil.formatAll("<prefix><#FBC36F>The server is empty and no player was " + (type ? "added" : "removed") + ".", null);
    }
    public static Component serverActually(boolean type) {
        return ServerUtil.formatAll("<prefix><#FBC36F>All online players are actually " + (type ? "added to" : "removed from") + " the whitelist.", null);
    }
    public static Component serverAmount(boolean type, int total) {
        return ServerUtil.formatAll("<prefix><#FBC36F>Successfully " + (type ? "added" : "removed") + " <#8CDEFF>" + total + " <#FBC36F>players.", null);
    }
    public static Component clearSug1() {
        return ServerUtil.formatAll("<prefix><#F46C4E>Are you sure to clear the whitelist?, this action is irreversible.", null);
    }
    public static Component clearSug2() {
        return ServerUtil.formatAll("<prefix><#FBC36F>Use <#FF6040>/nwl confirm <#FBC36F>to proceed the action.", null);
    }
    public static Component listPage(int page) {
        return ServerUtil.formatAll("<prefix><#FF9CBB>Whitelist Index <#B490F0>- <#99EAFE>Page<#65A8FF>: <#F07DF0>" + page, null);
    }
    public static Component listString(PlayerWhitelisted data) {
        String row;
        if (data.isWhitelisted()) row = "<#90FC4E>" + data.getRowId() + "<#39E52B> > ";
        else row = "<#F46C4E>" + data.getRowId() + "<#E3341C> > ";

        String name;
        if (data.getOptName().isPresent()) name = "<#76F2D6>" + data.getName();
        else name = "<#4E71AD>none";

        String uuid;
        if (data.getOptUUID().isPresent()) uuid = "<#F3FF8E>" + data.getUUID();
        else uuid = "<#D0845F>none";
        return ServerUtil.formatAll(row + name + " <#F7C85D>- " + uuid, null);
    }
    public static Component listEmpty(int page) {
        return ServerUtil.formatAll("<prefix><#FF9CBB>This page is empty. <#B490F0>- <#99EAFE>Page<#65A8FF>: <#F07DF0>" + page, null);
    }
    public static Component whitelistEmpty() {
        return ServerUtil.formatAll("<prefix><#FF9CBB>The whitelist is empty.", null);
    }
    public static Component reload() {
        return ServerUtil.formatAll("<prefix><#FBC36F>The whitelist was reloaded. <#F79559>(is not necessary unless editing the whitelist file!)", null);
    }
    public static Component whitelistAlreadyEmpty() {
        return ServerUtil.formatAll("<prefix><#FBC36F>The whitelist is already empty.", null);
    }
    public static Component whitelistCleared() {
        return ServerUtil.formatAll("<prefix><#FBC36F>The whitelist was cleared.", null);
    }
    public static Component statusHeader() {
        return ServerUtil.formatAll("<prefix><#FF9CBB><bold>NWhitelist status:", null);
    }
    public static Component statusVersion(String version) {
        return ServerUtil.formatAll("<prefix><#FBC36F>Version: <#F07DF0>" + version, null);
    }
    public static Component statusWhitelistSize(long size) {
        return ServerUtil.formatAll("<prefix><#FBC36F>Whitelist size: <#F07DF0>" + size, null);
    }
    public static Component statusWhitelistActive(boolean fase) {
        return ServerUtil.formatAll("<prefix><#FBC36F>Whitelist: <#8CDEFF>" + (fase ? "<#FF6040>on" : "<#969FA5>off"), null);
    }
    public static Component statusNameCheck(ConfigFile.CheckType check) {
        return ServerUtil.formatAll("<prefix><#FBC36F>Name Check: <#F07DF0>" + check.msg(), null);
    }
    public static Component statusUuidCheck(ConfigFile.CheckType check) {
        return ServerUtil.formatAll("<prefix><#FBC36F>UUID Check: <#F07DF0>" + check.msg(), null);
    }
    public static Component statusPermCheck(ConfigFile.CheckType check) {
        return ServerUtil.formatAll("<prefix><#FBC36F>Perm Check: <#F07DF0>" + check.msg(), null);
    }
    public static Component statusStorageType(NobleWhitelist plugin) {
        return ServerUtil.formatAll("<prefix><#FBC36F>Storage Type: <#F07DF0>" + plugin.getStorageType().name(), null);
    }
    public static Component playerAdded() {
        return ServerUtil.formatAll("<prefix><#FBC36F>The provided player was added to the whitelist.", null);
    }
    public static Component playerSelfAdded() {
        return ServerUtil.formatAll("<prefix><#FBC36F>You was added to the whitelist.", null);
    }
    public static Component playerAdded(String name) {
        return ServerUtil.formatAll("<prefix><#FBC36F>The player <#99EAFE>" + name + " <#FBC36F>was added to the whitelist.", null);
    }
    public static Component playerAdded(UUID uuid) {
        return ServerUtil.formatAll("<prefix><#FBC36F>The uuid <#99EAFE>" + uuid.toString() + " <#FBC36F>was added to the whitelist.", null);
    }
    public static Component playerSelfRemoved() {
        return ServerUtil.formatAll("<prefix><#FBC36F>You has been removed from the whitelist.", null);
    }
    public static Component playerRemoved(String name) {
        return ServerUtil.formatAll("<prefix><#FBC36F>The player <#99EAFE>" + name + " <#FBC36F>was removed from the whitelist.", null);
    }
    public static Component playerRemoved(UUID uuid) {
        return ServerUtil.formatAll("<prefix><#FBC36F>The uuid <#99EAFE>" + uuid.toString() + " <#FBC36F>was removed from the whitelist.", null);
    }
    public static Component playerAlready(String name) {
        return ServerUtil.formatAll("<prefix><#FBC36F>The player <#99EAFE>" + name + " <#FBC36F>is already in the whitelist.", null);
    }
    public static Component playerAlready() {
        return ServerUtil.formatAll("<prefix><#FBC36F>Any of the data from this player is already registered.", null);
    }
    public static Component playerSelfAlready() {
        return ServerUtil.formatAll("<prefix><#FBC36F>You are already added to the whitelist.", null);
    }
    public static Component playerAlready(UUID uuid) {
        return ServerUtil.formatAll("<prefix><#FBC36F>The uuid <#99EAFE>" + uuid.toString() + " <#FBC36F>is already in the whitelist.", null);
    }
    public static Component playerSelfNotFound() {
        return ServerUtil.formatAll("<prefix><#FBC36F>You are not in the whitelist.", null);
    }
    public static Component playerNotFound(String name) {
        return ServerUtil.formatAll("<prefix><#FBC36F>The player <#99EAFE>" + name + " <#FBC36F>is not in the whitelist.", null);
    }
    public static Component playerNotFound(UUID uuid) {
        return ServerUtil.formatAll("<prefix><#FBC36F>The player <#99EAFE>" + uuid.toString() + " <#FBC36F>is not in the whitelist.", null);
    }
    public static Component playerNotFound(long id) {
        return ServerUtil.formatAll("<prefix><#FBC36F>There is no player linked to this user id: <#99EAFE>" + id + " <#FBC36F>.", null);
    }
    public static Component playerToggledAlready(boolean canjoin) {
        return ServerUtil.formatAll("<prefix><#FBC36F>Nothing changed, the player join is already: <#F07DF0>" + canjoin, null);
    }
    public static Component playerToggled(String name, boolean canjoin) {
        return ServerUtil.formatAll("<prefix><#FBC36F>The join for the player <#99EAFE>" + name + " <#FBC36F>was changed to: <#F07DF0>" + canjoin, null);
    }
    public static Component playerToggled(UUID uuid, boolean canjoin) {
        return ServerUtil.formatAll("<prefix><#FBC36F>The join for the player <#99EAFE>" + uuid.toString() + " <#FBC36F>was changed to: <#F07DF0>" + canjoin, null);
    }
    public static Component playerToggled(long id, boolean canjoin) {
        return ServerUtil.formatAll("<prefix><#FBC36F>The join for the user with id <#99EAFE>" + id + " <#FBC36F> was changed to: <#F07DF0>" + canjoin, null);
    }
    public static Component whitelistAlready(boolean status) {
        return ServerUtil.formatAll("<prefix><#FBC36F>The whitelist is already <#99EAFE>" + (status ? "on" : "off") + ".", null);
    }
    public static Component whitelistChanged(boolean status) {
        return ServerUtil.formatAll("<prefix><#FBC36F>The whitelist was set to <#99EAFE>" + (status ? "on" : "off"), null);
    }
    public static Component confirmationRequired() {
        return ServerUtil.formatAll("<prefix><#FBC36F>Use <#FF6040>/nwl confirm <#FBC36F>to confirm this action.", null);
    }
    public static Component confirmationNoMore() {
        return ServerUtil.formatAll("<prefix><#8CDEFF>You don't have any pending confirmation.", null);
    }
    public static Component playerAbout(PlayerWhitelisted data) {
        if (data.isSaved()) return ServerUtil.formatAll("<prefix><#FF9CBB>This user was found: <#B490F0>- <#99EAFE>Index<#65A8FF>: <#F07DF0>" + data.getRowId(), null);
        else return ServerUtil.formatAll("<prefix><#FF9CBB>This user was found:", null);
    }
    public static Component playerAboutName(PlayerWhitelisted data) {
        String name;
        if (data.getOptName().isPresent()) name = "<#76F2D6>" + data.getName();
        else name = "<#4E71AD>none";
        return ServerUtil.formatAll("<prefix><#FBC36F>Name: " + name, null);
    }
    public static Component playerAboutUuid(PlayerWhitelisted data) {
        String uuid;
        if (data.getOptUUID().isPresent()) uuid = "<#F3FF8E>" + data.getUUID();
        else uuid = "<#D0845F>none";
        return ServerUtil.formatAll("<prefix><#FBC36F>UUID: " + uuid, null);
    }
    public static Component playerAboutUser(PlayerWhitelisted data) {
        String id;
        if (data.hasDiscord()) id = "<#FC73C6>" + data.getDiscordID();
        else id = "<#C077CA>none";
        return ServerUtil.formatAll("<prefix><#FBC36F>Discord ID: " + id, null);
    }
    public static Component playerAboutJoin(PlayerWhitelisted data) {
        String color;
        if (data.isWhitelisted()) color = "<#90FC4E>";
        else color = "<#F46C4E>";
        return ServerUtil.formatAll("<prefix><#FBC36F>Can join: " + color + data.isWhitelisted(), null);
    }
}
