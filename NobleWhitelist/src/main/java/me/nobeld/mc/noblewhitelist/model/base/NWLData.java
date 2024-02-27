package me.nobeld.mc.noblewhitelist.model.base;

import me.nobeld.mc.noblewhitelist.config.ConfigData;
import me.nobeld.mc.noblewhitelist.config.MessageData;
import me.nobeld.mc.noblewhitelist.logic.WhitelistChecker;
import me.nobeld.mc.noblewhitelist.logic.WhitelistData;
import me.nobeld.mc.noblewhitelist.model.storage.DataGetter;
import me.nobeld.mc.noblewhitelist.model.storage.StorageType;
import me.nobeld.mc.noblewhitelist.util.UpdateChecker;

public interface NWLData extends BaseVersioning {
    AdvPlatformManager getAdventure();
    ConfigData getConfigD();
    MessageData getMessageD();
    WhitelistData whitelistData();
    WhitelistChecker whitelistChecker();
    DataGetter getStorage();
    StorageType getStorageType();
    UpdateChecker getUptChecker();
    void disable();
}
