package me.nobeld.noblewhitelist.model.whitelist;

public enum VanillaWhitelistType {
    DISABLED,
    //OVERRIDE, TODO for v2.0.0
    IGNORE,
    NONE;

    public boolean shouldIgnore() {
        return this == DISABLED || this == IGNORE;
    }
}
