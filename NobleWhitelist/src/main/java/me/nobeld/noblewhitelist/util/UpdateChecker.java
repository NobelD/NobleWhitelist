package me.nobeld.noblewhitelist.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.model.base.BaseVersioning;
import me.nobeld.noblewhitelist.model.base.TriConsumer;
import net.kyori.adventure.audience.Audience;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;

public class UpdateChecker {
    public BaseVersioning data;
    public final String version;
    private String url = "https://github.com/NobelD/NobleWhitelist";
    private final String name;
    private final String subType;
    private long lastCheck = 0;
    private String latest;
    private final TriConsumer<Audience, String, String> suggestConsumer;
    public UpdateChecker(BaseVersioning data, String name, String subType, TriConsumer<Audience, String, String> suggestConsumer) {
        this.data = data;
        this.name = name;
        this.subType = subType;
        this.version = data.version();
        this.suggestConsumer = suggestConsumer;
    }
    public UpdateStatus githubCheck() {
        if (System.currentTimeMillis() < lastCheck + 1800000) return UpdateStatus.COOLDOWN;
        lastCheck = System.currentTimeMillis();
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL("https://raw.githubusercontent.com/nobeld/noblewhitelist/master/versions.json").openConnection();

            int timed_out = 1500;
            con.setRequestProperty("accept", "application/json");
            con.setConnectTimeout(timed_out);
            con.setReadTimeout(timed_out);

            JsonElement element = JsonParser.parseReader(new InputStreamReader(con.getInputStream()));
            JsonObject json = element.getAsJsonObject();
            JsonObject type = json.get(name).getAsJsonObject();
            url = type.get("url").getAsString();
            latest = type.get("latest").getAsJsonObject().get(subType).getAsString();

            if (latest == null) return UpdateStatus.NO_DATA;
            if (latest.contains(version)) return UpdateStatus.SAME_VERSION;
            return UpdateStatus.VERSION_AVAILABLE;
        } catch (NullPointerException e) {
            return UpdateStatus.NO_DATA;
        } catch (Exception ex) {
            NobleWhitelist.log(Level.WARNING, "An error occurred while checking for updates: " + ex.getClass().getCanonicalName());
            return UpdateStatus.CANT_REACH;
        } finally {
            if (con != null) con.disconnect();
        }
    }
    public boolean canUpdate(boolean configUpdate, boolean isPlayer) {
        UpdateStatus status = githubCheck();
        if (!isPlayer && status == UpdateStatus.CANT_REACH) return false;
        if (!configUpdate) return false;
        return status == UpdateStatus.VERSION_AVAILABLE;
    }
    public String getLatest() {
        return latest;
    }
    public String getUrl() {
        return url;
    }
    public enum UpdateStatus {
        SAME_VERSION, VERSION_AVAILABLE, NO_DATA, CANT_REACH, COOLDOWN
    }
    public void sendUpdate(Audience aud) {
        suggestConsumer.accept(aud, getLatest(), getUrl());
    }
}
