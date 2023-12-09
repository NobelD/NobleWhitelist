package me.nobeld.minecraft.noblewhitelist.model.whitelist;

public enum CheckType {
    NORMAL, NO_UUID, NAME_CAPS, NO_NAME, NAME_SKIP, NO_UUID_NAME_CAPS, UUID_NO_NAME, NAME_DIFFERENT_UUID, WHITELISTED_EXCLUDED;
    public boolean nameDiffUuid() {
        return this == NAME_DIFFERENT_UUID;
    }
}