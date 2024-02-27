package me.nobeld.mc.noblewhitelist.discord.model;

import me.nobeld.mc.noblewhitelist.discord.JDAManager;
import me.nobeld.mc.noblewhitelist.discord.config.ConfigData;
import me.nobeld.mc.noblewhitelist.discord.config.MessageData;
import me.nobeld.mc.noblewhitelist.model.base.BaseVersioning;
import me.nobeld.mc.noblewhitelist.util.UpdateChecker;

public interface NWLDData extends BaseVersioning {
    ConfigData getConfigD();
    MessageData getMessageD();
    JDAManager getJDAManager();
    UpdateChecker getUptChecker();
    void disable();
    void enableMsg(Runnable runnable);
}
