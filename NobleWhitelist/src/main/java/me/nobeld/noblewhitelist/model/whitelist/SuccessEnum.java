package me.nobeld.noblewhitelist.model.whitelist;

public enum SuccessEnum {
    /**
     * Matches all data. (name, uuid, permission)
     */
    ALL,
    /**
     * Matches entry data. (name, uuid, enabled)
     */
    ENTRY,
    /**
     * Only uuid matches.
     */
    ONLY_UUID,
    /**
     * Only name matches.
     */
    ONLY_NAME,
    /**
     * Permission and uuid matches.
     */
    UUID_PERMISSION,
    /**
     * Permission and name matches.
     */
    NAME_PERMISSION,
    /**
     * Only permission matches.
     */
    ONLY_PERMISSION,
    /**
     * None of the data matches.
     */
    NONE,
    /**
     * The entry is marked as disabled.
     */
    NOT_ACTIVE,
    /**
     * Unknown result.
     */
    UNKNOWN
    ;
    public boolean isWhitelisted() {
        return this == ONLY_NAME || this == NAME_PERMISSION || this == ONLY_UUID || this == UUID_PERMISSION || this == ENTRY || this == ALL;
    }
    public boolean isPermission() {
        return this == ONLY_PERMISSION || this == UUID_PERMISSION || this == NAME_PERMISSION;
    }
    public boolean optionalJoin() {
        return this != NONE && this != UNKNOWN && this != NOT_ACTIVE;
    }
}
