package me.nobeld.mc.noblewhitelist.config;

import de.leonhard.storage.Json;
import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.exceptions.SimplixValidationException;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import me.nobeld.mc.noblewhitelist.model.whitelist.WhitelistEntry;
import me.nobeld.mc.noblewhitelist.util.UUIDUtil;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class FileManager {
    public static String separator() {
        return FileSystems.getDefault().getSeparator();
    }
    /**
     * Create a new instance of a Yaml File.
     * @param path where the file will be saved.
     * @param resource the name of the resource from the plugin.
     * @return The yaml file instance.
     * @throws SimplixValidationException if the resource is not founded or another related error.
     */
    public static Yaml registerYaml(Path path, String resource) throws SimplixValidationException {
        if (resource == null) {
            Yaml yaml = SimplixBuilder.fromPath(path)
                    .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                    .setDataType(DataType.SORTED)
                    .createYaml();
            yaml.forceReload();
            return yaml;
        }
        Yaml yaml = SimplixBuilder.fromPath(path)
                .addInputStreamFromResource(resource)
                .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                .setDataType(DataType.SORTED)
                .createYaml()
                .addDefaultsFromInputStream();
        yaml.forceReload();
        return yaml;
    }
    public static Json registerJson(Path path, String resource) throws SimplixValidationException {
        if (resource == null) {
            Json yaml = SimplixBuilder.fromPath(path)
                    .setConfigSettings(ConfigSettings.SKIP_COMMENTS)
                    .setDataType(DataType.SORTED)
                    .createJson();
            yaml.forceReload();
            return yaml;
        }
        Json yaml = SimplixBuilder.fromPath(path)
                .addInputStreamFromResource(resource)
                .setConfigSettings(ConfigSettings.SKIP_COMMENTS)
                .setDataType(DataType.SORTED)
                .createJson();
        yaml.forceReload();
        return yaml;
    }
    public static WhitelistEntry stringToPlayer(String string) {
        if (string == null || string.isBlank() || string.equals("null") || string.equals("none")) return null;
        final String[] split = string.split(";");
        return new WhitelistEntry(split[0], UUIDUtil.parseUUID(split[1].trim()), Long.parseLong(split[2].trim()), Boolean.parseBoolean(split[3].trim()));
    }
}

