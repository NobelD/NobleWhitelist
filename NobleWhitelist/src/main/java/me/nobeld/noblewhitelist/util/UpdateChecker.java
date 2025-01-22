package me.nobeld.noblewhitelist.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.model.base.BaseVersioning;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.Nullable;

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
    private final String extraType;
    private long lastCheck = 0;
    private String latest;
    @Nullable
    private String latestExtra = null;

    public UpdateChecker(BaseVersioning data, String name, String subType, @Nullable String extraType) {
        this.data = data;
        this.name = name;
        this.subType = subType;
        this.version = data.version();
        this.extraType = extraType;
    }

    public UpdateChecker(BaseVersioning data, String name, String subType) {
        this(data, name, subType, null);
    }

    public UpdateStatus githubCheck() {
        return githubCheck(true);
    }
    private UpdateStatus readJson(JsonElement element) {
        JsonObject json = element.getAsJsonObject();
        JsonObject type = json.get(name).getAsJsonObject();
        url = type.get("url").getAsString();
        JsonObject lo = type.get("latest").getAsJsonObject();
        latest = lo.has(subType) ? lo.get(subType).getAsString() : null;

        // If no latest is found cannot compare with extra
        if (latest == null) return UpdateStatus.NO_DATA;
        if (extraType != null) {
            latestExtra = lo.has(extraType) ? lo.get(extraType).getAsString() : null;
        }

        // If last equals the version no need to compare more.
        if (latest.equals(version)) {
            return UpdateStatus.SAME_VERSION;
        } else if (version.endsWith("-SNAPSHOT")) { // If version is snapshot compare if latest is a stable version
            String s = version.replace("-SNAPSHOT", "");
            if ((latestExtra != null && latestExtra.equals(s)) || latest.equals(s))
                return UpdateStatus.SAME_SNAPSHOT;
            return UpdateStatus.VERSION_RELEASE_AVAILABLE;
        } else if (latestExtra != null) { // If extra exist and equals this version.
            if (latestExtra.equals(version)) return UpdateStatus.SAME_EXTRA;
            return UpdateStatus.AVAILABLE_EXTRA;
        }
        // If no check match then a version is available.
        return UpdateStatus.VERSION_AVAILABLE;
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
            return readJson(element);
        } catch (NullPointerException e) {
            return UpdateStatus.NO_DATA;
        } catch (Throwable ex) {
            NobleWhitelist.log(Level.WARNING, "An error occurred while checking for updates: " + ex.getClass().getCanonicalName() + " - " + ex.getMessage());
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

        if (!status.canPrint()) return false;

        switch (status) {
            case AVAILABLE_EXTRA -> {
                audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>It seems that you are not using the latest version of <gold>" + name));
                audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Your version: <#FF8B4D>" + version + " <yellow>| <#F1B65C>Latest: <#6FEF22>" + latestExtra));
                audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>It also seems it is the latest available for you server, check FAQ for more info!"));
                audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Download it at: <#75CDFF>" + url));
            }
            case SAME_EXTRA -> {
                audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>It seems there is a new version but is not available for your server. <gold>(<#FF8B4D>" + latestExtra + " - <#6FEF22>" + latest + "<gold>)"));
                audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Nothing to worry but you may be missing some new features, check FAQ for more info!"));
            }
            case VERSION_RELEASE_AVAILABLE -> {
                audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>You are using a experimental version of <gold>" + name + "<#F1B65C>, consider to update to a stable version!"));
                audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Your version: <#FF8B4D>" + version + " <yellow>| <#F1B65C>Latest: <#6FEF22>" + latest));
                audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Download it at: <#75CDFF>" + url));
            }
            default -> {
                audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>It seems that you are not using the latest version of <gold>" + name));
                audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Your version: <#FF8B4D>" + version + " <yellow>| <#F1B65C>Latest: <#6FEF22>" + latest));
                audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Download it at: <#75CDFF>" + url));
            }
        }
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
         * Same extra version but not main version
         */
        SAME_EXTRA,
        /**
         * Same extra version but not main version
         */
        AVAILABLE_EXTRA,
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
            return this == VERSION_AVAILABLE || this == VERSION_RELEASE_AVAILABLE || this == AVAILABLE_EXTRA;
        }

        public boolean canPrint() {
            return canUpdate() || this == SAME_EXTRA;
        }

        public boolean isSame() {
            return this == SAME_VERSION || this == SAME_SNAPSHOT || this == SAME_EXTRA;
        }

        public boolean noExist() {
            return this == NO_DATA || this == CANT_REACH;
        }
    }
}
