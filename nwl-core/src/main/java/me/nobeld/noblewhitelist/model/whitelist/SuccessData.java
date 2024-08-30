package me.nobeld.noblewhitelist.model.whitelist;

import me.nobeld.noblewhitelist.model.base.PlayerWrapper;
import me.nobeld.noblewhitelist.model.checking.CheckingOption;

public record SuccessData(PlayerWrapper player, boolean name, boolean uuid, boolean perm) {
    /**
     * @return true if any of the values are true
     */
    public boolean hasAny() {
        return name || uuid || perm;
    }

    /**
     * @return true if all the values are true
     */
    public boolean hasAll() {
        return name && uuid && perm;
    }

    /**
     * @return true if name and uuid are true
     */
    public boolean isNormal() {
        return name && uuid;
    }

    /**
     * @return true if name or uuid are true
     */
    public boolean isWhitelisted() {
        return name || uuid;
    }

    /**
     * @return true if perm is true
     */
    public boolean isBypass() {
        return perm;
    }

    /**
     * @return true if only name is true and the others false
     */
    public boolean onlyName() {
        return name && !(uuid || perm);
    }

    /**
     * @return true if only uuid is true and the others false
     */
    public boolean onlyUuid() {
        return uuid && !(name || perm);
    }

    /**
     * @return true if only perm is true and the others false
     */
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
                u = this.name;
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
                p = this.name;
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
