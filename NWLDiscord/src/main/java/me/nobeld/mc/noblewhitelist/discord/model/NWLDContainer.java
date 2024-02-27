package me.nobeld.mc.noblewhitelist.discord.model;

import com.alessiodp.libby.Library;
import com.alessiodp.libby.LibraryManager;
import me.nobeld.mc.noblewhitelist.NobleWhitelist;
import me.nobeld.mc.noblewhitelist.discord.JDAManager;
import me.nobeld.mc.noblewhitelist.discord.config.ConfigData;
import me.nobeld.mc.noblewhitelist.discord.config.MessageData;
import me.nobeld.mc.noblewhitelist.discord.util.LibsManager;
import me.nobeld.mc.noblewhitelist.util.AdventureUtil;
import me.nobeld.mc.noblewhitelist.util.UpdateChecker;
import net.kyori.adventure.audience.Audience;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.BiConsumer;

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
    public static Builder builder(NWLDData data) {
        return new Builder(data);
    }
    public static void closeData(NWLDData data) {
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
        private final NWLDData data;
        private ConfigData config;
        private MessageData message = null;
        private UpdateChecker update = null;
        private JDAManager jdaManager = null;
        protected Builder(NWLDData data) {
            this.data = data;
        }
        public Builder loadLibs(LibraryManager manager, @Nullable List<Library> additional) {
            new LibsManager(manager, additional);
            return this;
        }
        public Builder loadFiles() {
            config = new ConfigData();
            config.configFile();
            message = new MessageData();
            message.messageFile();
            return this;
        }
        public Builder loadUpdateChecker(String url, String name, BiConsumer<Audience, String> consumer) {
            update = new UpdateChecker(data, url, name, consumer);
            return this;
        }
        public Builder loadJDA() {
            jdaManager = new JDAManager(data, config);
            return this;
        }
        public Builder printMessage() {
            NobleWhitelist.adv().consoleAudience().sendMessage(AdventureUtil.formatAll("<prefix><green>Loaded Discord integration!"));
            if (update.canUpdate(config.get(ConfigData.notifyUpdate), false))
                update.sendUpdate(NobleWhitelist.adv().consoleAudience());
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
