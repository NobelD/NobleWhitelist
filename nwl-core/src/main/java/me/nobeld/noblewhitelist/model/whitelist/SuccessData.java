package me.nobeld.noblewhitelist.model.whitelist;

import me.nobeld.noblewhitelist.model.base.PlayerWrapper;

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

    public SuccessEnum successEnum() {
        if (hasAll()) return SuccessEnum.ALL;
        if (isNormal()) return SuccessEnum.NORMAL;
        if (onlyName()) return SuccessEnum.ONLY_NAME;
        if (onlyUuid()) return SuccessEnum.ONLY_UUID;
        if (onlyPerm()) return SuccessEnum.BYPASS;
        return SuccessEnum.NONE;
    }
}
