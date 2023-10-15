package me.nobeld.minecraft.noblewhitelist.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.nobeld.minecraft.noblewhitelist.NobleWhitelist;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static me.nobeld.minecraft.noblewhitelist.util.ServerUtil.convertMsg;

public class UpdateChecker {
    private final NobleWhitelist plugin;
    private final String version;
    private String latest;
    public UpdateChecker(NobleWhitelist plugin ,String version){
        this.plugin = plugin;
        this.version = version;
    }
    public UpdateStatus check() {
        try {
            HttpURLConnection github = (HttpURLConnection) new URL(
                    "https://api.github.com/repos/NobelD/NobleWhitelist/releases/latest").openConnection();

            int timed_out = 1500;
            github.setRequestProperty("accept", "application/vnd.github+json");
            github.setConnectTimeout(timed_out);
            github.setReadTimeout(timed_out);

            JsonElement element = JsonParser.parseReader(new InputStreamReader(github.getInputStream()));
            JsonObject json = element.getAsJsonObject();
            latest = json.get("tag_name").getAsString();
            if (latest == null) return UpdateStatus.NO_DATA;
            if (latest.contains(version)) return UpdateStatus.SAME_VERSION;
            return UpdateStatus.VERSION_AVAILABLE;
        } catch (Exception ex) {
            return UpdateStatus.CANT_REACH;
        }
    }
    public boolean canUpdate(boolean player) {
        UpdateStatus status = check();
        if (!player && status == UpdateStatus.CANT_REACH) consoleError();
        if (!plugin.fileData().notifyUpdate()) return false;
        return status == UpdateStatus.VERSION_AVAILABLE;
    }
    public void consoleError() {
        plugin.consoleMsg().sendMessage(convertMsg("<prefix><#F46C4E>An error occurred while checking available updates.", null));
    }
    public String getLatest() {
        return latest;
    }
    public enum UpdateStatus {
        SAME_VERSION, VERSION_AVAILABLE, NO_DATA, CANT_REACH
    }
}
