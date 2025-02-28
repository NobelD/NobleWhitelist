package me.nobeld.noblewhitelist.model.whitelist;

import me.nobeld.noblewhitelist.model.base.PlayerWrapper;
import me.nobeld.noblewhitelist.model.checking.CheckingOption;

public record SuccessData(PlayerWrapper player, boolean name, boolean uuid, boolean perm) {
    public boolean hasAny() {
        return name || uuid || perm;
    }
    public boolean hasAll() {
        return name && uuid && perm;
    }
    public boolean isNormal() {
        return name && uuid;
    }
    public boolean isWhitelisted() {
        return name || uuid;
    }
    public boolean isBypass() {
        return perm;
    }
    public boolean onlyName() {
        return name && !(uuid || perm);
    }
    public boolean onlyUuid() {
        return uuid && !(name || perm);
    }
    public boolean onlyPerm() {
        return perm && !(name || uuid);
    }

    /**
     *
     * @param name the check for the name
     * @param uuid the check for the uuid
     * @param perm the check for the perm
     * @return case 1 - one or more options are required, so their value should exist, otherwise returns false.<br>
     * case 2 - one or more options are optional, so at least one of these should exist, otherwise returns false.<br>
     * case 3 - one or more options that are disabled will not be considered for checking.<br>
     * case 4 - if all the options are disabled always will return true.
     */
    public boolean forCheck(CheckingOption name, CheckingOption uuid, CheckingOption perm) {
        boolean optional = false;
        final boolean n;
        switch (name) {
            case REQUIRED -> {
                if (!this.name) return false;
                n = true;
            }
            case OPTIONAL -> {
                n = this.name;
                if (n)
                    optional = true;
            }
            case DISABLED -> n = true;
            default -> n = this.name;
        }
        final boolean u;
        switch (uuid) {
            case REQUIRED -> {
                if (!this.uuid) return false;
                u = true;
            }
            case OPTIONAL -> {
                u = this.uuid;
                if (u && !optional)
                    optional = true;
            }
            case DISABLED -> u = true;
            default -> u = this.uuid;
        }
        final boolean p;
        switch (perm) {
            case REQUIRED -> {
                if (!this.perm) return false;
                p = true;
            }
            case OPTIONAL -> {
                p = this.perm;
                if (p && !optional)
                    optional = true;
            }
            case DISABLED -> p = true;
            default -> p = this.perm;
        }
        if ((name.isOptional() || uuid.isOptional() || perm.isOptional()) && optional) {
            return true;
        }
        return n && u && p;
    }

    public SuccessEnum successEnum() {
        if (hasAll()) return SuccessEnum.ALL;
        if (isNormal()) return SuccessEnum.NORMAL;
        if (onlyName()) return SuccessEnum.ONLY_NAME;
        if (onlyUuid()) return SuccessEnum.ONLY_UUID;
        if (onlyPerm()) return SuccessEnum.BYPASS;
        return SuccessEnum.NONE;
    }
    public static SuccessData allFalse(PlayerWrapper player) {
        return new SuccessData(player, false, false, false);
    }
}
