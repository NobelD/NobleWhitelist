package me.nobeld.noblewhitelist.model.whitelist;

/**
 * <pre>
 * Name - true if the name match (ignoring capitalization)
 * Caps - true if the capitalization of the name match
 * UUID - true if the uuid match
 * </pre>
 */
public enum CheckType {
    /**
     * <pre>
     * Name - true
     * Caps - true
     * UUID - true
     * </pre>
     */
    NORMAL,
    /**
     * <pre>
     * Name - true
     * Caps - skipped
     * UUID - true
     * </pre>
     */
    SKIPPED_NAME,
    /**
     * <pre>
     * Name - false (null)
     * Caps - false
     * UUID - true
     * </pre>
     */
    NO_NAME,
    /**
     * <pre>
     * Name - true
     * Caps - true
     * UUID - false (null)
     * </pre>
     */
    NO_UUID,
    /**
     * <pre>
     * Name - true
     * Caps - false
     * UUID - false (null)
     * </pre>
     */
    NO_UUID_NAME_CAPS,
    /**
     * <pre>
     * Name - false (null)
     * Caps - false
     * UUID - false (null)
     * </pre>
     */
    NOT_MATCH,
    /**
     * <pre>
     * Name - true
     * Caps - false
     * UUID - true
     * </pre>
     */
    NAME_CAPS,
    /**
     * <pre>
     * Name - true
     * Caps - true
     * UUID - false (different)
     * </pre>
     */
    NAME_DIFFERENT_UUID,
    /**
     * <pre>
     * Name - true
     * Caps - false
     * UUID - false (different)
     * </pre>
     */
    NAME_CAPS_DIFFERENT_UUID,
    /**
     * <pre>
     * Name - false (null)
     * Caps - false
     * UUID - true
     * </pre>
     */
    UUID_NO_NAME,
    /**
     * Some of the inputed data were is invalid.
     */
    INVALID
}