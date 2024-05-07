package me.nobeld.noblewhitelist.model.base;

import me.nobeld.noblewhitelist.api.NobleWhitelistApi;
import me.nobeld.noblewhitelist.config.ConfigData;
import me.nobeld.noblewhitelist.language.MessageData;
import me.nobeld.noblewhitelist.logic.WhitelistChecker;
import me.nobeld.noblewhitelist.logic.WhitelistData;
import me.nobeld.noblewhitelist.model.storage.DataGetter;
import me.nobeld.noblewhitelist.model.storage.StorageType;
import me.nobeld.noblewhitelist.util.UpdateChecker;

public interface NWLData extends BaseVersioning {
    AdvPlatformManager getAdventure();
    ConfigData getConfigD();
    MessageData getMessageD();
    WhitelistData whitelistData();
    WhitelistChecker whitelistChecker();
    DataGetter getStorage();
    StorageType getStorageType();
    UpdateChecker getUptChecker();
    NobleWhitelistApi getApi();
    String configPath();
    void disable();
    void reloadDataBase();
    void closeServer();
    void runCommand(String command);
    void setBlocked(boolean block);
    boolean isBlocked();
}
