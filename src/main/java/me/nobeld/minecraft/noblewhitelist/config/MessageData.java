//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.nobeld.minecraft.noblewhitelist.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;
import net.kyori.adventure.text.Component;

import static me.nobeld.minecraft.noblewhitelist.util.ServerUtil.convertMsg;

public class MessageData {
    private final NobleWhitelist plugin;
    public MessageData(NobleWhitelist plugin) {
        this.plugin = plugin;
    }
    public Component warningNameConsole(String name) {
        return convertMsg(plugin.fileData().nameChangeConsole(), name);
    }
    public Component warningNamePlayer(String name) {
        return convertMsg(plugin.fileData().nameChangePlayer(), name);
    }
    public Component kickMsg(String name) {
        return convertMsg(plugin.fileData().kickMsg(), name);
    }
    public Component serverEmpty() {
        return convertMsg("<prefix><#F1B65C>No players was added because the server is empty.", null);
    }
    public Component serverActuallyAdded() {
        return convertMsg("<prefix><#F1B65C>No players was added because all players are in the whitelist.", null);
    }
    public Component amountPlayersAdded(int total) {
        return convertMsg("<prefix><#F1B65C>Successfully added <#75CDFF>" + total + " <#F1B65C>players.", null);
    }
    public Component invalidInput() {
        return convertMsg("<prefix><#F1B65C>Invalid input, use <#75CDFF>name <#F1B65C>or <#75CDFF>uuid.", null);
    }
    public Component clearSug1() {
        return convertMsg("<prefix><#F46C4E>Are you sure to clear the whitelist?, this action is irreversible.", null);
    }
    public Component clearSug2() {
        return convertMsg("<prefix><#F1B65C>Use <#75CDFF>/nwl <#F1B65C>forceclear to proceed.", null);
    }
    public Component listAmount(int size) {
        return convertMsg("<prefix><#FF618C>Players whitelisted <#F1B65C>(<#75CDFF>name <#F46C4E>: <#C775FF>uuid<#F1B65C>) (<#75CDFF>Total<#F46C4E>: <#C775FF>" + size + "<#F1B65C>)", null);
    }
    public Component listSkip(int size) {
        return convertMsg("<prefix><#FF618C>Players whitelisted by name <#F46C4E>(<#C775FF>" + size + " <#F1B65C> only uuid<#F46C4E>)", null);
    }
    public Component listExceed(int size) {
        return convertMsg("<prefix><#FF618C>The whitelist is so big, try viewing it from the whitelist file. <#75CDFF>(Total<#F46C4E>: <#C775FF>" + size + "<#75CDFF>)", null);
    }
    public Component listString(String name, String uuid) {
        String uuidColor = "<#C775FF>";
        if (uuid.equalsIgnoreCase("none")) {
            uuidColor = "<#4A8FFF>";
        }
        return convertMsg("<#75CDFF>" + name + " <#F46C4E>: " + uuidColor + uuid, null);
    }
    public Component listName(Map<String, String> map) {
        List<String> total = new ArrayList<>();
        for (Map.Entry<String, String> list : map.entrySet()) {
            String name = list.getKey();
            String uuid = list.getValue();
            String first = uuid.equalsIgnoreCase("none") ? "<#4A8FFF>" : "<#75CDFF>";
            total.add(first + name + "<#F46C4E>");
        }
        return convertMsg("<#C775FF>Players: <#F46C4E>" + total, null);
    }
    public Component whitelistEmpty() {
        return convertMsg("<prefix><#F1B65C>The whitelist is empty.", null);
    }
    public Component reload() {
        return convertMsg("<prefix><#F1B65C>The player whitelist was reload (is not necessary unless editing the whitelist config!)", null);
    }
    public Component whitelistAlreadyEmpty() {
        return convertMsg("<prefix><#F1B65C>The whitelist is already empty.", null);
    }
    public Component whitelistCleared() {
        return convertMsg("<prefix><#F1B65C>The Whitelist was cleared.", null);
    }
    public Component statusHeader() {
        return convertMsg("<prefix><#FF618C>NWhitelist status:", null);
    }
    public Component statusVersion(String version) {
        return convertMsg("<prefix><#F1B65C>Version: <#C775FF>" + version, null);
    }
    public Component statusWhitelistSize(int size) {
        return convertMsg("<prefix><#F1B65C>Whitelist size: <#75CDFF>" + size, null);
    }
    public Component statusWhitelistActive(String fase) {
        return convertMsg("<prefix><#F1B65C>Whitelist: <#75CDFF>" + fase, null);
    }
    public Component statusNameCheck(String check) {
        return convertMsg("<prefix><#F1B65C>Name Check: <#75CDFF>" + check, null);
    }
    public Component statusUuidCheck(String check) {
        return convertMsg("<prefix><#F1B65C>UUID Check: <#75CDFF>" + check, null);
    }
    public Component statusPermCheck(String check) {
        return convertMsg("<prefix><#F1B65C>Perm Check: <#75CDFF>" + check, null);
    }
    public Component invalidPlayerInput(String input) {
        return convertMsg("<prefix><#F1B65C>Invalid input, insert the player's " + input + ".", null);
    }
    public Component playerAdded(String player) {
        return convertMsg("<prefix><#F1B65C>The player <#75CDFF>" + player + " <#F1B65C>was added to the whitelist", null);
    }
    public Component playerRemoved(String player) {
        return convertMsg("<prefix><#F1B65C>The player <#75CDFF>" + player + " <#F1B65C>was removed from the whitelist.", null);
    }
    public Component playerAlready(String player) {
        return convertMsg("<prefix><#F1B65C>The player <#75CDFF>" + player + " <#F1B65C>is already in the whitelist.", null);
    }
    public Component playerNotFound(String player) {
        return convertMsg("<prefix><#F1B65C>The player <#75CDFF>" + player + " <#F1B65C>is not in the whitelist.", null);
    }
    public Component whitelistAlready(String status) {
        return convertMsg("<prefix><#F1B65C>The whitelist is already <#75CDFF>" + status, null);
    }
    public Component whitelistChanged(String status) {
        return convertMsg("<prefix><#F1B65C>The whitelist was set to <#75CDFF>" + status, null);
    }
    public Component helpHeader() {
        return convertMsg("<prefix><#FF618C>Basic commands list:", null);
    }
    public Component helpAdd() {
        return convertMsg("<prefix><#F1B65C>/nwhitelist <#75CDFF>add <#C775FF><type> <value>", null);
    }
    public Component helpRemove() {
        return convertMsg("<prefix><#F1B65C>/nwhitelist <#75CDFF>remove <#C775FF><type> <value>", null);
    }
    public Component helpList() {
        return convertMsg("<prefix><#F1B65C>/nwhitelist <#75CDFF>list <#C775FF><type>", null);
    }
    public Component helpOn() {
        return convertMsg("<prefix><#F1B65C>/nwhitelist <#75CDFF>on", null);
    }
    public Component helpOff() {
        return convertMsg("<prefix><#F1B65C>/nwhitelist <#75CDFF>off", null);
    }
    public Component helpReload() {
        return convertMsg("<prefix><#F1B65C>/nwhitelist <#75CDFF>reload", null);
    }
}
