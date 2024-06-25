package me.nobeld.noblewhitelist.model.whitelist;

import me.nobeld.noblewhitelist.model.base.PlayerWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record CheckType(NameCheck name, UUIDCheck uuid) {
    public static final CheckType INVALID = new CheckType(NameCheck.INVALID, UUIDCheck.INVALID);

    /**
     * Name > Yes - UUID > Yes
     *
     * @return if the name and the uuid match
     */
    public boolean match() {
        return name.match() && uuid.match();
    }

    /**
     * Name > Yes|Caps - UUID > Yes
     *
     * @return if the name even with caps and the uuid match
     */
    public boolean matchCaps() {
        return name.matchCaps() && uuid.match();
    }

    /**
     * Name > Empty - UUID > Empty
     *
     * @return if the name and the uuid are empty
     */
    public boolean isEmpty() {
        return name.isEmpty() && uuid.isEmpty();
    }

    /**
     * Name > Not - UUID > Not
     *
     * @return if the name and the uuid not match
     */
    public boolean allNotMatch() {
        return name == NameCheck.NOT_MATCH && uuid == UUIDCheck.NOT_MATCH;
    }

    /**
     * Name > Not || UUID > Not
     *
     * @return if the name or the uuid not match
     */
    public boolean noneMatch() {
        return name == NameCheck.NOT_MATCH || uuid == UUIDCheck.NOT_MATCH;
    }

    /**
     * @return if the player match by name
     */
    public boolean allowedByName() {
        return name.matchCaps() && (uuid.isEmpty() || uuid.match());
    }

    /**
     * Most of the time the uuid will match but the name is not necessary
     * <br>Is better to ignore the name in that case
     *
     * @return if the player match by uuid
     * @see CheckType#allowedOnlyUUID()
     */
    public boolean allowedByUUID() {
        return uuid.match() && (name.isEmpty() || name.matchCaps());
    }

    /**
     * @return if the player match by uuid ignoring the name
     */
    public boolean allowedOnlyUUID() {
        return uuid.match();
    }

    /**
     * @return if the player match or the name or the uuid
     */
    public boolean allowed() {
        return allowedByName() || allowedByUUID();
    }

    /**
     * @return if the player match or the name or the uuid, this last ignoring the name
     */
    public boolean allowedIgnore() {
        return allowedByName() || allowedOnlyUUID();
    }

    /**
     * Used for internal check
     *
     * @return if the name is empty or not match but the uuid match
     */
    public boolean emptyNameYesUUID() {
        return name.isEmpty() && uuid.match();
    }

    /**
     * Used for internal check
     *
     * @return if the name match with caps and the uuid match
     */
    public boolean nameWithCapsYesUUID() {
        return name.matchCaps() && uuid.match();
    }

    /**
     * Used for internal check
     *
     * @return if the name match with caps but the uuid is empty
     */
    public boolean emptyUUIDYesNameCaps() {
        return !uuid.match() && name == NameCheck.CAPITALIZATION;
    }

    /**
     * Used for internal check
     *
     * @return if the name match exact but the uuid is empty
     */
    public boolean emptyUUIDYesName() {
        return uuid.isEmpty() && name.match();
    }

    /**
     * Used for internal check
     *
     * @return if the name match even with caps but the uuid is empty
     */
    public boolean emptyUUIDYesNameOrCaps() {
        return uuid.isEmpty() && name.matchCaps();
    }

    /**
     * Used for internal check
     *
     * @return if the name match but the uuid not match (different)
     */
    public boolean diffUUIDYesName() {
        return name == NameCheck.NORMAL && uuid == UUIDCheck.NOT_MATCH;
    }

    /**
     * Used for internal check
     *
     * @return if the name with caps match but the uuid not match (different)
     */
    public boolean diffUUIDYesNameCaps() {
        return name == NameCheck.CAPITALIZATION && uuid == UUIDCheck.NOT_MATCH;
    }

    /**
     * Used for internal check
     *
     * @return if the name even with caps match but the uuid not match (different)
     */
    public boolean diffUUIDYesNameOrCaps() {
        return (name == NameCheck.NORMAL || name == NameCheck.CAPITALIZATION) && uuid == UUIDCheck.NOT_MATCH;
    }

    public enum NameCheck {
        /**
         * The name match exact.
         */
        NORMAL,
        /**
         * The name was semi skipped.
         */
        SKIPPED,
        /**
         * The name match but the capitalization of the name does not match
         */
        CAPITALIZATION,
        /**
         * No name was found.
         */
        NOT_FOUND,
        /**
         * Name was found but not match.
         */
        NOT_MATCH,
        /**
         * Invalid name (in normal cases never)
         */
        INVALID;

        /**
         * @return If the name is not present
         */
        public boolean isEmpty() {
            return this == NOT_FOUND;
        }

        /**
         * @return If the name is present but not match
         */
        public boolean notMatch() {
            return this == NOT_MATCH;
        }

        /**
         * @return If the name was semi skipped
         */
        public boolean skipped() {
            return this == SKIPPED;
        }

        /**
         * @return If the name match exactly
         */
        public boolean match() {
            return this == NORMAL;
        }

        /**
         * @return If the name match even with caps
         */
        public boolean matchCaps() {
            return this == NORMAL || this == CAPITALIZATION;
        }

        /**
         * @return If the name match even with caps or was skipped
         */
        public boolean matchSkipCaps() {
            return this == NORMAL || this == CAPITALIZATION || this == SKIPPED;
        }

        /**
         * Name can differ but uuid will always be the same (expected behaviour)
         */
        public boolean noNameAlt() {
            return this == NOT_FOUND || this == NOT_MATCH;
        }
    }

    public enum UUIDCheck {
        /**
         * The uuid match exact.
         */
        NORMAL,
        /**
         * The uuid was completely skipped.
         */
        SKIPPED,
        /**
         * No uuid was found.
         */
        NOT_FOUND,
        /**
         * Uuid was found but not match
         */
        NOT_MATCH,
        /**
         * Invalid uuid (in normal cases never)
         */
        INVALID;

        /**
         * @return if the uuid is not present
         */
        public boolean isEmpty() {
            return this == NOT_FOUND;
        }

        /**
         * @return if the uuid is present but not match
         */
        public boolean notMatch() {
            return this == NOT_MATCH;
        }

        /**
         * @return if the uuid was skipped
         */
        public boolean skipped() {
            return this == SKIPPED;
        }

        /**
         * @return if the uuid match
         */
        public boolean match() {
            return this == NORMAL;
        }

        /**
         * @return if the uuid match or was skipped
         */
        public boolean matchSkip() {
            return this == NORMAL || this == SKIPPED;
        }
    }

    /**
     * Parses an entry and a player to get a check type.
     *
     * @param entry    the entry to use
     * @param player   the player to compare
     * @param skipName if skip name check
     * @param skipUUID if skip uuid check
     * @return the result check type
     */
    public static CheckType getFromPlayer(@NotNull WhitelistEntry entry, @NotNull PlayerWrapper player, boolean skipName, boolean skipUUID) {
        String name0 = entry.getName(), name1 = player.getName();
        UUID uuid0 = entry.getUUID(), uuid1 = player.getUUID();
        if (name0 == null && uuid0 == null) return new CheckType(NameCheck.NOT_FOUND, UUIDCheck.NOT_FOUND);

        NameCheck nameR = skipName ? NameCheck.SKIPPED :
                name0 == null ? NameCheck.NOT_FOUND :
                        name0.equals(name1) ? NameCheck.NORMAL :
                                name0.equalsIgnoreCase(name1) ? NameCheck.CAPITALIZATION :
                                        NameCheck.NOT_MATCH;

        UUIDCheck uuidR = skipUUID ? UUIDCheck.SKIPPED :
                uuid0 == null ? UUIDCheck.NOT_FOUND :
                        uuid0.equals(uuid1) ? UUIDCheck.NORMAL :
                                UUIDCheck.NOT_MATCH;

        return new CheckType(nameR, uuidR);
    }
}