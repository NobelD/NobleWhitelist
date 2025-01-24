package me.nobeld.noblewhitelist.util;

import de.leonhard.storage.internal.provider.SimplixProviders;
import de.leonhard.storage.logger.JavaLogger;
import de.leonhard.storage.shaded.esotericsoftware.yamlbeans.YamlConfig;

import java.util.logging.Logger;

public class SimplixCommon {
    public static void setupCommon(Logger logger) {
        SimplixProviders.logger(new JavaLogger(logger));
        final YamlConfig config = new YamlConfig();
        config.writeConfig.setIndentSize(3); // temporary because it breaks files
        config.writeConfig.setEscapeUnicode(false);
        config.writeConfig.setAutoAnchor(false);
        config.writeConfig.setWriteClassname(YamlConfig.WriteClassName.NEVER);
        SimplixProviders.yamlConfig(config);
    }
}
