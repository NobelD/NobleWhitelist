package me.nobeld.noblewhitelist.model.checking;

public enum CheckingOption {
    REQUIRED("<#F46C4E>required"),
    OPTIONAL("<#75CDFF>optional"),
    DISABLED("<#969FA5>disabled");
    private final String msg;
    CheckingOption(String msg) {
        this.msg = msg;
    }
    public String msg() {
        return msg;
    }
    public boolean isRequired() {
        return this == REQUIRED;
    }
    public boolean isOptional() {
        return this == OPTIONAL;
    }
    public boolean isDisabled() {
        return this == DISABLED;
    }
}
