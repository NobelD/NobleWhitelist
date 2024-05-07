package me.nobeld.noblewhitelist.discord.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.nobeld.noblewhitelist.util.UUIDUtil;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerChecker {
    public UUID checkPlayer(String name, int tries) {
        if (!isValid(name)) return null;
        int intent = 0;
        UUID id = null;
        while (intent < tries) {
            id = checkPlayer(name);
            if (id != null) break;
            intent++;
        }
        return id;
    }
    public String checkPlayer(UUID id, int tries) {
        int intent = 0;
        String name = null;
        while (intent < tries) {
            name = checkPlayer(id);
            if (id != null) break;
            intent++;
        }
        return name;
    }
    public UUID checkPlayer(String name) {
        UUID uuid = null;
        try {
            HttpURLConnection mojang = (HttpURLConnection) new URL(
                    "https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();

            int timed_out = 1500;
            mojang.setRequestMethod("GET");
            mojang.setConnectTimeout(timed_out);
            mojang.setReadTimeout(timed_out);

            if (mojang.getResponseCode() == HttpURLConnection.HTTP_OK) {
                JsonElement element = JsonParser.parseReader(new InputStreamReader(mojang.getInputStream()));
                JsonObject json = element.getAsJsonObject();
                uuid = UUIDUtil.parseUUID(json.get("id").getAsString());
            }
        } catch (Exception ignored) {
        }
        return uuid;
    }
    public String checkPlayer(UUID uuid) {
        String name = null;
        try {
            HttpURLConnection mojang = (HttpURLConnection) new URL(
                    "https://api.mojang.com/users/profiles/minecraft/" + uuid.toString()).openConnection();

            int timed_out = 1500;
            mojang.setRequestMethod("GET");
            mojang.setConnectTimeout(timed_out);
            mojang.setReadTimeout(timed_out);

            if (mojang.getResponseCode() == HttpURLConnection.HTTP_OK) {
                JsonElement element = JsonParser.parseReader(new InputStreamReader(mojang.getInputStream()));
                JsonObject json = element.getAsJsonObject();
                name = json.get("name").getAsString();
            }
        } catch (Exception ignored) {
        }
        return name;
    }
    public static boolean isValid(String string) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_]*");
        Matcher matcher = pattern.matcher(string);
        return !matcher.matches();
    }
    public static boolean isValidGeyser(String string) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_.]*");
        Matcher matcher = pattern.matcher(string);
        return !matcher.matches();
    }
}
