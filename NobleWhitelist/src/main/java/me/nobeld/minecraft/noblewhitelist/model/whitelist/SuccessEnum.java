package me.nobeld.minecraft.noblewhitelist.model.whitelist;

public enum SuccessEnum {
    ALL("all"),
    NORMAL("normal"),
    ONLY_UUID("uuid"),
    ONLY_NAME("name"),
    BYPASS("bypass"),
    NONE("none"),
    NOT_ACTIVE("inactive"),
    UNKNOWN("unknown"),;
    private final String string;
    SuccessEnum(String string) {
        this.string = string;
    }
    public String string() {
        return string;
    }
    public boolean isWhitelisted() {
        return this != NONE && this != BYPASS;
    }
    public boolean isByPass() {
        return this == BYPASS;
    }
    public boolean optionalJoin() {
        return this != NONE;
    }
}
