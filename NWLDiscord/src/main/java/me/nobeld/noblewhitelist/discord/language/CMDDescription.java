package me.nobeld.noblewhitelist.discord.language;

import org.incendo.cloud.description.Description;

public class CMDDescription {
    // #TODO resource bundle
    public static Description generalAdmin() {
        return Description.of("Command for admins to manage the whitelist.");
    }
    public static Description generalUser() {
        return Description.of("Command for user to manage their data from the whitelist.");
    }
    public static Description find() {
        return Description.of("Find an user whitelisted by some data.");
    }
    public static Description findUser() {
        return Description.of("Find an user whitelisted by their discord user.");
    }
    public static Description addUser() {
        return Description.of("Add an user to the minecraft whitelist.");
    }
    public static Description removeUser() {
        return Description.of("Remove an user from the minecraft server whitelist.");
    }
    public static Description linkUser() {
        return Description.of("Link some data to a whitelisted user.");
    }
    public static Description unlinkUser() {
        return Description.of("Unlink some data of a whitelisted user.");
    }
    public static Description toggleUser() {
        return Description.of("Toggle if a whitelisted user can join or not.");
    }
    public static Description entryList() {
        return Description.of("Get a list of the users whitelisted.");
    }
    public static Description whitelistOn() {
        return Description.of("Enable the minecraft server whitelist.");
    }
    public static Description whitelistOff() {
        return Description.of("Disable the minecraft server whitelist.");
    }
    public static Description permStatus() {
        return Description.of("Get the actual config of the perm pass.");
    }
    public static Description permSet() {
        return Description.of("Changes the minimum of the perm pass.");
    }
    public static Description checkingStatus() {
        return Description.of("Get the checking status of the whitelist.");
    }
    public static Description checkingSet() {
        return Description.of("Set the checking to a specific type.");
    }
    public static Description selfAccounts() {
        return Description.of("Get the accounts who are you linked from the minecraft server whitelist");
    }
    public static Description selfAdd() {
        return Description.of("Add your account to the minecraft server whitelist.");
    }
    public static Description selfRemove() {
        return Description.of("Remove your account from the minecraft server whitelist.");
    }
    public static Description selfLink() {
        return Description.of("Link your account to a whitelist entry.");
    }
}
