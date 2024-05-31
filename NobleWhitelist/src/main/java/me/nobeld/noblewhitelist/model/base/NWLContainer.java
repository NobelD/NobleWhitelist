package me.nobeld.noblewhitelist.model.base;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import me.nobeld.noblewhitelist.config.ConfigData;
import me.nobeld.noblewhitelist.config.FileManager;
import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.logic.StorageLoader;
import me.nobeld.noblewhitelist.logic.WhitelistChecker;
import me.nobeld.noblewhitelist.logic.WhitelistData;
import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.model.storage.DataGetter;
import me.nobeld.noblewhitelist.model.storage.StorageType;
import me.nobeld.noblewhitelist.storage.root.DatabaseSQL;
import me.nobeld.noblewhitelist.util.AdventureUtil;
import me.nobeld.noblewhitelist.util.LibsManager;
import me.nobeld.noblewhitelist.util.UpdateChecker;
import net.kyori.adventure.audience.Audience;

import javax.annotation.Nullable;
import java.util.List;

public class NWLContainer {
    private final ConfigData config;
    private final MessageData message;
    private final UpdateChecker update;
    private final DataGetter storage;
    private final StorageType type;
    private final WhitelistData wlData;
    private final WhitelistChecker wlChecker;
    protected NWLContainer(ConfigData config, MessageData message, UpdateChecker update, DataGetter storage, StorageType type, WhitelistData wlData, WhitelistChecker wlChecker) {
        this.config = config;
        this.message = message;
        this.update = update;
        this.storage = storage;
        this.type = type;
        this.wlData = wlData;
        this.wlChecker = wlChecker;
    }
    public static Builder builder(NWLData data) {
        return new Builder(data);
    }
    public static void closeData(NWLData data) {
        if (data.getConfigD() != null) data.getConfigD().reloadConfig();
        if (data.getStorageType().isDatabase()) {
            if (data.getStorage() != null) ((DatabaseSQL)data.getStorage()).close();
        }
        if (data.getAdventure() != null) data.getAdventure().closeAdventure();
    }
    public ConfigData getConfig() {
        return config;
    }
    public MessageData getMessage() {
        return message;
    }
    public UpdateChecker getUpdate() {
        return update;
    }
    public DataGetter getStorage() {
        return storage;
    }
    public StorageType getType() {
        return type;
    }
    public WhitelistData getWlData() {
        return wlData;
    }
    public WhitelistChecker getWlChecker() {
        return wlChecker;
    }

    /**
     * Order of the builder, internal use only.
     * <pre>
     * 1 Libs
     * 2 Files
     * 3 Adventure
     * 4 Storage
     * 5 Data
     * 6 Build
     * </pre>
     */
    public static class Builder {
        private final NWLData data;
        private ConfigData config;
        private MessageData message = null;
        private UpdateChecker update = null;
        private DataGetter storage = null;
        private StorageType type = StorageType.NONE;
        private WhitelistData wlData = null;
        private WhitelistChecker wlChecker = null;
        protected Builder(NWLData data) {
            this.data = data;
        }
        public Builder loadLibs(LibraryManager manager, @Nullable List<Library> additional) {
            new LibsManager(manager, additional);
            return this;
        }
        public Builder loadFiles(String path, PairData<String, FileManager.FileType> config) {
            this.config = new ConfigData(path, config.getFirst(), config.getSecond());
            AdventureUtil.replaceData(() -> this.config.usePrefix(), () -> this.config.getPrefix());
            this.message = new MessageData(data);
            this.config.refreshData();
            return this;
        }
        public Builder loadAdventure() {
            data.getAdventure();
            return this;
        }
        public Builder loadUpdateChecker(String name, String subType, TriConsumer<Audience, String, String> consumer) {
            update = new UpdateChecker(data, name, subType, consumer);
            return this;
        }
        public Builder loadStorage() {
            PairData<DataGetter, StorageType> st = StorageLoader.setupStorage(data, config);
            storage = st.getFirst();
            type = st.getSecond();
            return this;
        }
        public Builder loadData() {
            wlData = new WhitelistData(data);
            wlChecker = new WhitelistChecker(data);
            return this;
        }
        public Builder printMessage() {
            long total = storage.getTotal();
            data.getAdventure().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>Plugin successfully activated!"));
            data.getAdventure().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>Loaded <yellow>" + total + " <green>whitelist entries."));
            if (!type.isDatabase() && total >= 100) data.getAdventure().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>Mind in use database as storage type since there is a lot of entries."));

            if (update.canUpdate(config.get(ConfigData.ServerCF.notifyUpdate), false)) {
                update.sendUpdate(data.getAdventure().consoleAudience());
            }
            return this;
        }
        public Builder load(Runnable runnable) {
            runnable.run();
            return this;
        }
        public NWLContainer build() {
            return new NWLContainer(config, message, update, storage, type, wlData, wlChecker);
        }
    }
}
