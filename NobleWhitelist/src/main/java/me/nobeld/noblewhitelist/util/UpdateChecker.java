package me.nobeld.noblewhitelist.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.model.base.BaseVersioning;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

public class UpdateChecker {
    public BaseVersioning data;
    public final Version version;
    private static UsefulLinks links = null;

    private String downloadUrl = "https://github.com/NobelD/NobleWhitelist";
    private final String name;
    private final String subType;
    private final String extraType;
    private long lastCheck = 0;
    private boolean isCritical = false;
    private Version latest;
    @Nullable
    private Version latestExtra = null;

    public UpdateChecker(BaseVersioning data, String name, String subType, @Nullable String extraType) {
        this.data = data;
        this.name = name;
        this.subType = subType;
        this.version = Version.create(data.version());
        this.extraType = extraType;
    }

    public UpdateChecker(BaseVersioning data, String name, String subType) {
        this(data, name, subType, null);
    }

    public UpdateStatus githubCheck() {
        return githubCheck(true);
    }
    @Nullable
    private static String containsToString(JsonObject obj, String name) {
        return obj.has(name) ? obj.get(name).getAsString() : null;
    }
    private UpdateStatus readJson(JsonElement element) {
        JsonObject json = element.getAsJsonObject();
        if (links == null) {
            links = UsefulLinks.create(json);
        }
        if (!json.has(name)) {
            return UpdateStatus.NO_DATA;
        }
        JsonObject type = json.get(name).getAsJsonObject();
        String url = containsToString(type, "url");
        if (url != null) {
            this.downloadUrl = url;
        }
        try {
            Set<String> versions = new HashSet<>();
            for (JsonElement e : type.get("critical").getAsJsonArray()) {
                versions.add(e.getAsString());
            }
            if (versions.contains(version.asString())) {
                isCritical = true;
            }
        } catch (Throwable e) {
            data.logger().log(Level.WARNING, "Unable to parse the critical versions, security concerns may exist, be sure to check the wiki or updates!");
        }
        if (!type.has("latest")) {
            return UpdateStatus.NO_DATA;
        }
        JsonObject lo = type.get("latest").getAsJsonObject();
        latest = lo.has(subType) ? Version.create(lo.get(subType).getAsString()) : null;

        // If no latest is found cannot compare with extra
        if (latest == null || version == null) {
            return UpdateStatus.NO_DATA;
        }
        if (extraType != null) {
            latestExtra = lo.has(extraType) ? Version.create(lo.get(extraType).getAsString()) : null;
        }

        if (latestExtra != null && !latestExtra.matchExact(latest)) {
            UpdateStatus status = compare(version, latestExtra);
            if (status == UpdateStatus.AVAILABLE) return UpdateStatus.AVAILABLE_EXTRA;
            else if (status == UpdateStatus.SAME) return UpdateStatus.SAME_EXTRA;
            else return status;
        } else {
            return compare(version, latest);
        }
    }
    private UpdateStatus compare(@NotNull Version version, @NotNull Version other) {
        if (version.isSnapshot()) {
            return switch (version.resolve(other)) {
                case 0 -> UpdateStatus.LESSER;
                case 1 -> other.isSnapshot() ? UpdateStatus.SAME_SNAPSHOT : UpdateStatus.AVAILABLE_RELEASE;
                default -> UpdateStatus.AVAILABLE;
            };
        } else if (version.hasExtra()) {
            return switch (version.resolve(other)) {
                case 0 -> UpdateStatus.LESSER;
                case 1 -> other.isSnapshot() ? UpdateStatus.LESSER : UpdateStatus.AVAILABLE_OTHER;
                default -> UpdateStatus.AVAILABLE_OTHER;
            };
        } else {
            return switch (version.resolve(other)) {
                case 0 -> UpdateStatus.LESSER;
                case 1 -> other.isSnapshot() ? UpdateStatus.LESSER : UpdateStatus.SAME;
                default -> UpdateStatus.AVAILABLE;
            };
        }
    }
    public UpdateStatus githubCheck(boolean cooldown) {
        if (version == null) {
            return UpdateStatus.NO_DATA;
        }
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
            NobleWhitelist.log(Level.WARNING, "Skip this error if you have poor or no internet connection, if the update service is not down check the source for updates!");
            return UpdateStatus.CANT_REACH;
        } finally {
            if (con != null) con.disconnect();
        }
    }
    public boolean sendStatus(Audience audience, boolean softSkip, boolean cooldown) {
        return sendStatus(audience, "<prefix>", softSkip, cooldown);
    }
    public boolean sendStatus(Audience audience, String prefix, boolean softSkip, boolean cooldown) {
        UpdateStatus status = githubCheck(cooldown);

        if (isCritical) {
            audience.sendMessage(AdventureUtil.formatAll(prefix + "<bold><#FF2414>\\<!> <#FF6176>Your version was marked as critical, you may want to update to a safer version asap!</bold>"));
            audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>There is a new update for <gold>" + name));
            audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Your version: <#FF8B4D>" + version + " <yellow>| <#F1B65C>Latest: <#6FEF22>" + latest));
            audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Download it at: <#75CDFF>" + downloadUrl));
        } else if (status.shouldPrint(softSkip)) {
            String version = this.version.asString();
            String latest = this.latest.asString();
            switch (status) {
                case AVAILABLE_EXTRA -> {
                    String latestExtra = Objects.requireNonNull(this.latestExtra, "invalid").asString();
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>There is a new update for <gold>" + name));
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Your version: <#FF8B4D>" + version + " <yellow>| <#F1B65C>Latest: <#6FEF22>" + latestExtra));
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>It also seems it is the latest available for you server, check FAQ for more info!"));
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Download it at: <#75CDFF>" + downloadUrl));
                }
                case SAME_EXTRA -> {
                    String latestExtra = Objects.requireNonNull(this.latestExtra, "invalid").asString();
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>It seems there is a new version but is not available for your server. <gold>(<#FF8B4D>" + latestExtra + " - <#6FEF22>" + latest + "<gold>)"));
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Nothing to worry but you may be missing some new features, check FAQ for more info!"));
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Wiki link: <#75CDFF>" + getUsefulLinks().wiki));
                }
                case AVAILABLE_RELEASE -> {
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>There is a new update for <gold>" + name + "<#F1B65C>"));
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>You are using an experimental version, consider to update to the stable version!"));
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Your version: <#FF8B4D>" + version + " <yellow>| <#F1B65C>Latest: <#6FEF22>" + latest));
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Download it at: <#75CDFF>" + downloadUrl));
                }
                case AVAILABLE_OTHER -> {
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>It seems that you are not using the latest version of <gold>" + name));
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Your version: <#FF8B4D>" + version + " <yellow>| <#F1B65C>Latest: <#6FEF22>" + latest));
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Download it at: <#75CDFF>" + downloadUrl));
                }
                default -> {
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>There is a new update for <gold>" + name));
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Your version: <#FF8B4D>" + version + " <yellow>| <#F1B65C>Latest: <#6FEF22>" + latest));
                    audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Download it at: <#75CDFF>" + downloadUrl));
                }
            }
        } else {
            return false;
        }
        return true;
    }
    public void sendSupport(Audience audience) {
        sendSupport(audience, "<prefix>");
    }
    public void sendSupport(Audience audience, String prefix) {
        getUsefulLinks();
        audience.sendMessage(AdventureUtil.formatAll(prefix + "<#FF9CBB><bold>Useful links about the plugin:"));
        audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Repository: <#F07DF0>" + links.repository));
        audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Issues: <#F07DF0>" + links.issues));
        audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Wiki: <#F07DF0>" + links.wiki));
        if (links.support != null && !links.support.equals(links.discord)) {
            audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Support: " + links.support));
        }
        if (links.discord != null) {
            audience.sendMessage(AdventureUtil.formatAll(prefix + "<#F1B65C>Discord Support Server: " + links.discord));
        }
    }
    public String getName() {
        return name;
    }
    public String getSubType() {
        return subType;
    }
    public Optional<String> getExtraType() {
        return Optional.ofNullable(extraType);
    }
    public Version getLatest() {
        return latest;
    }
    public Optional<Version> getLatestExtra() {
        return Optional.ofNullable(latestExtra);
    }
    public String getDownloadUrl() {
        return downloadUrl;
    }
    public static UsefulLinks getUsefulLinks() {
        if (links == null) {
            links = UsefulLinks.empty();
        }
        return links;
    }
    public enum UpdateStatus {
        /**
         * Same extra version.
         */
        SAME_EXTRA,
        /**
         * Same extra version and latest is available.
         */
        AVAILABLE_EXTRA,
        /**
         * The newer version is lesser that this version.
         */
        LESSER,
        /**
         * Same version.
         */
        SAME,
        /**
         * Same snapshot versions.
         */
        SAME_SNAPSHOT,
        /**
         * A new version is available.
         */
        AVAILABLE,
        /**
         * A new version is available.
         */
        AVAILABLE_OTHER,
        /**
         * This version is snapshot and the stable version is available.
         */
        AVAILABLE_RELEASE,
        /**
         * No data exist.
         */
        NO_DATA,
        /**
         * Cannot reach the data.
         */
        CANT_REACH,
        /**
         * The update checker is on cooldown.
         */
        COOLDOWN;

        public boolean canUpdate() {
            return this == AVAILABLE || this == AVAILABLE_RELEASE || this == AVAILABLE_EXTRA || this == AVAILABLE_OTHER;
        }

        public boolean canPrint() {
            return canUpdate() || this == SAME_EXTRA;
        }

        public boolean shouldPrint(boolean softSkip) {
            return !softSkip && canPrint();
        }

        public boolean isSame() {
            return this == SAME || this == SAME_SNAPSHOT || this == SAME_EXTRA;
        }

        public boolean noExist() {
            return this == NO_DATA || this == CANT_REACH;
        }
    }

    public record Version(@NotNull Integer[] ver, @Nullable String extra) {
        @Nullable
        @Contract("null -> null")
        public static Version create(String str) {
            if (str == null || str.isBlank()) {
                return null;
            }
            String[] type = str.trim().split("-", 2);
            String extra = type.length == 2 ? type[1] : null;
            String[] ver = type[0].split("\\.");
            ArrayList<Integer> list = new ArrayList<>();
            try {
                for (String s : ver) {
                    int i = Integer.parseInt(s);
                    if (i < 0) {
                        throw new NumberFormatException("negative number");
                    }
                    list.add(i);
                }
            } catch (NumberFormatException ex) {
                return null;
            }
            Integer[] array = list.toArray(Integer[]::new);
            return new Version(array, extra);
        }

        public boolean hasExtra() {
            return extra != null;
        }

        public boolean isSnapshot() {
            return extra != null && extra.equalsIgnoreCase("snapshot");
        }

        public boolean isStable() {
            return extra == null;
        }

        public boolean isNotStable() {
            if (this.extra != null) {
                String extra = this.extra.toLowerCase();
                return extra.startsWith("snapshot") || extra.startsWith("beta") || extra.startsWith("alpha")
                        || extra.startsWith("rc");
            }
            return false;
        }

        @Contract("null -> false")
        public boolean isGreater(@Nullable Version other) {
            return other != null && resolve(other) == 2;
        }

        @Contract("null -> false")
        public boolean isSame(@Nullable Version other) {
            return other != null && resolve(other) == 1;
        }

        @Contract("null -> false")
        public boolean isLesser(@Nullable Version other) {
            return other != null && resolve(other) == 0;
        }

        @Contract("null -> false")
        public boolean matchExtra(@Nullable Version other) {
            if (other == null) {
                return false;
            }
            if (this.extra() == null) {
                return other.extra() == null;
            } else {
                return this.extra().equalsIgnoreCase(other.extra());
            }
        }

        @Contract("null -> false")
        public boolean matchExact(@Nullable Version other) {
            return isSame(other) && matchExtra(other);
        }

        /**
         * @return 2 if the other is major, 1 if the version is same, 0 if lesser
         */
        public int resolve(@NotNull Version other) {
            return resolve(this.ver(), other.ver());
        }

        /**
         * @return 2 if the other is major, 1 if the version is same, 0 if lesser
         */
        public static int resolve(@NotNull Version version, @NotNull Version other) {
            return resolve(version.ver(), other.ver());
        }

        /**
         * @return 2 if the other is major, 1 if the version is same, 0 if lesser
         */
        public static int resolve(@NotNull Integer[] array, @NotNull Integer[] other) {
            return subResolve(array, other, 0);
        }

        /**
         * @return 2 if the other is major, 1 if the version is same, 0 if lesser
         */
        private static int subResolve(@NotNull Integer[] array, @NotNull Integer[] other, int index) {
            int plus = index + 1;
            if (array.length < plus) {
                if (other.length < plus) {
                    return 1;
                } else {
                    return 2;
                }
            } else if (other.length < plus) {
                return 0;
            }
            int val1 = array[index];
            int val2 = other[index];
            if (val1 == val2) {
                return subResolve(array, other, plus);
            } else if (val1 < val2) {
                return 2;
            } else {
                return 0;
            }
        }

        public String asString() {
            StringBuilder builder = new StringBuilder();
            for (Integer i : ver) {
                if (builder.isEmpty()) {
                    builder.append(i);
                } else {
                    builder.append(".").append(i);
                }
            }
            if (hasExtra()) {
                builder.append("-").append(extra());
            }
            return builder.toString();
        }
    }

    public record UsefulLinks(String repository, String issues, String wiki, String support, String discord) {
        private static UsefulLinks empty() {
            final String repoUrl = "https://github.com/NobelD/NobleWhitelist";
            final String issuesUrl = "https://github.com/NobelD/NobleWhitelist/issues";
            final String wikiUrl = "https://github.com/NobelD/NobleWhitelist/wiki";
            return new UsefulLinks(repoUrl, issuesUrl, wikiUrl, null, null);
        }
        private static UsefulLinks create(JsonObject json) {
            String repoUrl = "https://github.com/NobelD/NobleWhitelist";
            String issuesUrl = "https://github.com/NobelD/NobleWhitelist/issues";
            String wikiUrl = "https://github.com/NobelD/NobleWhitelist/wiki";
            String discordUrl = null;
            String supportUrl = null;

            String repo = containsToString(json, "repository");
            if (repo != null) {
                repoUrl = repo;
            }
            String is = containsToString(json, "issues");
            if (is != null) {
                issuesUrl = is;
            }
            String wiki = containsToString(json, "wiki");
            if (wiki != null) {
                wikiUrl = wiki;
            }
            String sup = containsToString(json, "support");
            if (sup != null) {
                supportUrl = sup;
            }
            String ds = containsToString(json, "discord-support");
            if (ds != null) {
                discordUrl = ds;
            }
            return new UsefulLinks(repoUrl, issuesUrl, wikiUrl, supportUrl, discordUrl);
        }
    }
}
