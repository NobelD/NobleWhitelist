package me.nobeld.noblewhitelist.discord.model;

import me.nobeld.noblewhitelist.discord.JDAManager;
import me.nobeld.noblewhitelist.discord.config.ConfigData;
import me.nobeld.noblewhitelist.discord.config.MessageData;
import me.nobeld.noblewhitelist.model.base.BaseVersioning;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.util.UpdateChecker;

import java.io.InputStream;

public interface NWLDsData extends BaseVersioning {
    ConfigData getConfigD();
    MessageData getMessageD();
    JDAManager getJDAManager();
    UpdateChecker getUptChecker();
    void disable();
    void enableMsg(Runnable runnable);
    InputStream resourceStream(String name);
    NWLData getNWL();
}
