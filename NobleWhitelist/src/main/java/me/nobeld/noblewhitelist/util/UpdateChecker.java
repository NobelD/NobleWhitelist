package me.nobeld.noblewhitelist.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.model.base.BaseVersioning;
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

    public UpdateChecker(BaseVersioning data, String name, String subType) {
        this.data = data;
        this.name = name;
        this.subType = subType;
        this.version = data.version();
    }
    public UpdateStatus githubCheck() {
        return githubCheck(true);
    }
    public UpdateStatus githubCheck(boolean cooldown) {
        if (cooldown) {
            if (System.currentTimeMillis() < lastCheck + 1800000)
                return UpdateStatus.COOLDOWN;
            lastCheck = System.currentTimeMillis();
        }
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
            else if (latest.equals(version)) return UpdateStatus.SAME_VERSION;
            else if (version.endsWith("-SNAPSHOT")) {
                String s = version.replace("-SNAPSHOT", "");
                if (latest.equals(s))
                    return UpdateStatus.SAME_SNAPSHOT;
                return UpdateStatus.VERSION_RELEASE_AVAILABLE;
            }
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
    public boolean sendStatus(Audience audience, boolean isPlayer) {
        return sendStatus(audience, "<prefix>", isPlayer);
    }
    public boolean sendStatus(Audience audience, String prefix, boolean isPlayer) {
        UpdateStatus status = githubCheck(isPlayer);

        if (!status.canUpdate()) return false;

        if (status == UpdateStatus.VERSION_RELEASE_AVAILABLE) {
            audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>You are using a experimental version of <gold>" + name + "<#F1B65C>, consider to update to a stable version!"));
        } else {
            audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>It seems that you are not using the latest version of <gold>" + name));
        }
        audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Your version: " + version + " <dark_green>| <#F1B65C>Latest: <#FF8B4D>" + latest));
        audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Download it at: <#75CDFF>" + url));
        return true;
    }
    public String getLatest() {
        return latest;
    }
    public String getUrl() {
        return url;
    }
    public enum UpdateStatus {
        /**
         * Same version
         */
        SAME_VERSION,
        /**
         * Same version but this instance is snapshot
         */
        SAME_SNAPSHOT,
        /**
         * A new version is available
         */
        VERSION_AVAILABLE,
        /**
         * A new version is available and is release from this instance.
         */
        VERSION_RELEASE_AVAILABLE,
        /**
         * No data exist.
         */
        NO_DATA,
        /**
         * Can not reach the data.
         */
        CANT_REACH,
        /**
         * The update checker is on cooldown
         */
        COOLDOWN;

        public boolean canUpdate() {
            return this == VERSION_AVAILABLE || this == VERSION_RELEASE_AVAILABLE;
        }

        public boolean isSame() {
            return this == SAME_VERSION || this == SAME_SNAPSHOT;
        }

        public boolean noExist() {
            return this == NO_DATA || this == CANT_REACH;
        }
    }
}
