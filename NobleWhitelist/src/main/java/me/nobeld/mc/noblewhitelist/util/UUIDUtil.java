package me.nobeld.mc.noblewhitelist.util;

import java.util.UUID;

public class UUIDUtil {
    public static String convertUUID(String uuid) {
        if (uuid == null) return null;
        StringBuilder builder = new StringBuilder(uuid.trim().replace("-", ""));
        try {
            builder.insert(20, "-");
            builder.insert(16, "-");
            builder.insert(12, "-");
            builder.insert(8, "-");
        } catch (StringIndexOutOfBoundsException e){
            return null;
        }
        return builder.toString();
    }
    public static UUID parseUUID(String uuid) {
        if (uuid == null || uuid.isEmpty() || uuid.isBlank() || uuid.equalsIgnoreCase("none$") || uuid.equalsIgnoreCase("null")) return null;
        UUID id;
        try {
            id = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            try {
                String ui = convertUUID(uuid.replace("-", ""));
                if (ui == null) return null;
                id = UUID.fromString(ui);
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
        return id;
    }
    public static String noDashUUID(UUID uuid) {
        return uuid.toString().trim().replace("-", "");
    }
}
