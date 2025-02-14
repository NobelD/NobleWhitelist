package me.nobeld.noblewhitelist.discord.model;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import me.nobeld.noblewhitelist.NobleWhitelist;
import me.nobeld.noblewhitelist.config.FileManager;
import me.nobeld.noblewhitelist.discord.JDAManager;
import me.nobeld.noblewhitelist.discord.config.ConfigData;
import me.nobeld.noblewhitelist.discord.config.MessageData;
import me.nobeld.noblewhitelist.discord.util.LibsManager;
import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.util.AdventureUtil;
import me.nobeld.noblewhitelist.util.UpdateChecker;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NWLDContainer {
    private final ConfigData config;
    private final MessageData message;
    private final UpdateChecker update;
    private final JDAManager jdaManager;
    protected NWLDContainer(ConfigData config, MessageData message, UpdateChecker update, JDAManager manager) {
        this.config = config;
        this.message = message;
        this.update = update;
        this.jdaManager = manager;
    }
    public static Builder builder(NWLDsData data) {
        return new Builder(data);
    }
    public static void closeData(NWLDsData data) {
        if (data.getJDAManager() != null) {
            data.getJDAManager().disable();
        }
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
    public JDAManager getJDAManager() {
        return jdaManager;
    }

    /**
     * Order of the builder, internal use only.
     * <pre>
     * 1 Libs
     * 2 Files
     * 3 JDA
     * 4 Build
     * </pre>
     */
    public static class Builder {
        private final NWLDsData data;
        private ConfigData config;
        private MessageData message = null;
        private UpdateChecker update = null;
        private JDAManager jdaManager = null;
        protected Builder(NWLDsData data) {
            this.data = data;
        }
        public Builder loadLibs(LibraryManager manager, @Nullable List<Library> additional) {
            new LibsManager(manager, additional);
            return this;
        }
        public Builder loadFiles(String path, PairData<String, FileManager.FileType> config, PairData<String, FileManager.FileType> message) {
            this.config = new ConfigData(data, path, config.getFirst(), config.getSecond());
            this.config.configFile();
            this.message = new MessageData(data, path, message.getFirst(), message.getSecond());
            this.message.messageFile();
            return this;
        }
        public Builder loadUpdateChecker(String name, String subType, @Nullable String extraType) {
            update = new UpdateChecker(data, name, subType, extraType);
            return this;
        }
        public Builder loadJDA() {
            jdaManager = new JDAManager(data, config);
            return this;
        }
        public Builder printMessage() {
            NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>Loaded Discord integration Plugin!"));
            if (config.get(ConfigData.notifyUpdate))
                update.sendStatus(NobleWhitelist.adv().consoleAudience(), true);
            return this;
        }
        public Builder load(Runnable runnable) {
            runnable.run();
            return this;
        }
        public NWLDContainer build() {
            return new NWLDContainer(config, message, update, jdaManager);
        }
    }
}
