package me.nobeld.noblewhitelist.model.whitelist;

/**
 * <pre>
 * Name - true if the name match
 * Caps - true if the capitalization of the name match
 * UUID - true if the uuid match
 * </pre>
 */
public enum CheckType {
    /**
     * Some of the inputted data were invalid.
     */
    INVALID,
    /**
     * <pre>
     * No name nor uuid matches. (they may have been skipped, or they are empty)
     * > Name - false
     * > Caps - false
     * > UUID - false
     * </pre>
     */
    NONE,
    /**
     * <pre>
     * Name and uuid matches.
     * > Name - true
     * > Caps - true
     * > UUID - true
     * </pre>
     */
    FINE,
    /**
     * <pre>
     * Name and uuid matches, name capitalization does not match.
     * > Name - true
     * > Caps - false
     * > UUID - true
     * </pre>
     */
    CAPITALIZATION,
    /**
     * <pre>
     * Name is not present, uuid matches.
     * > Name - false (null)
     * > Caps - false
     * > UUID - true
     * </pre>
     */
    MISSING_NAME,
    /**
     * <pre>
     * Name check skipped, uuid matches.
     * > Name - skipped
     * > Caps - skipped
     * > UUID - true
     * </pre>
     */
    SKIPPED_NAME,
    /**
     * <pre>
     * Name does not match, uuid matches.
     * > Name - false
     * > Caps - false
     * > UUID - true
     * </pre>
     */
    DIFFERENT_NAME,
    /**
     * <pre>
     * Name matches, uuid is not present. (or skipped)
     * > Name - true
     * > Caps - true
     * > UUID - false (null)
     * </pre>
     */
    MISSING_UUID,
    /**
     * <pre>
     * Name matches but not their capitalization, uuid is not present. (or skipped)
     * > Name - true
     * > Caps - false
     * > UUID - false (null)
     * </pre>
     */
    MISSING_UUID_AND_CAPS,
    /**
     * <pre>
     * Name matches, uuid is present but different.
     * > Name - true
     * > Caps - true
     * > UUID - false (different)
     * </pre>
     */
    DIFFERENT_UUID,
    /**
     * <pre>
     * Name matches but not their capitalization, uuid is present but different.
     * > Name - true
     * > Caps - false
     * > UUID - false (different)
     * </pre>
     */
    DIFFERENT_UUID_AND_CAPS
    ;
    public boolean isFine() {
        return this == FINE || this == CAPITALIZATION || this == SKIPPED_NAME;
    }

    public boolean isValid() {
        return isFine() || this == MISSING_NAME || this == MISSING_UUID || this == MISSING_UUID_AND_CAPS || this == DIFFERENT_NAME;
    }

    public boolean noCaps() {
        return this == CAPITALIZATION || this == MISSING_UUID_AND_CAPS || this == DIFFERENT_UUID_AND_CAPS;
    }

    public boolean hasMissing() {
        return this == MISSING_NAME || this == MISSING_UUID || this == MISSING_UUID_AND_CAPS;
    }

    public boolean hasSoftIssues() {
        return this == DIFFERENT_UUID || this == DIFFERENT_UUID_AND_CAPS;
    }

    public boolean hasHardIssues() {
        return hasSoftIssues() || this == DIFFERENT_NAME;
    }
}