package me.nobeld.noblewhitelist.config;

import de.leonhard.storage.Json;
import de.leonhard.storage.SimplixBuilder;
import de.leonhard.storage.Toml;
import de.leonhard.storage.Yaml;
import de.leonhard.storage.internal.FlatFile;
import de.leonhard.storage.internal.exceptions.SimplixValidationException;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import me.nobeld.noblewhitelist.util.UUIDUtil;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class FileManager {
    public static String separator() {
        return FileSystems.getDefault().getSeparator();
    }

    /**
     * Create a new instance of a Yaml File.
     *
     * @param path     where the file will be saved.
     * @param resource the name of the resource from the plugin.
     * @return The yaml file instance.
     * @throws SimplixValidationException if the resource is not founded or another related error.
     */
    public static Yaml registerYaml(Path path, String resource) throws SimplixValidationException {
        Yaml yaml = createBuilder(path, resource, null, ConfigSettings.PRESERVE_COMMENTS, DataType.SORTED).createYaml().addDefaultsFromInputStream();
        yaml.forceReload();
        return yaml;
    }

    /**
     * Create a new instance of a Json File.
     *
     * @param path     where the file will be saved.
     * @param resource the name of the resource from the plugin.
     * @return The json file instance.
     * @throws SimplixValidationException if the resource is not founded or another related error.
     */
    public static Json registerJson(Path path, String resource) throws SimplixValidationException {
        Json json = createBuilder(path, resource, null, ConfigSettings.SKIP_COMMENTS, DataType.SORTED).createJson();
        json.forceReload();
        return json;
    }

    /**
     * Create a new instance of a Toml File.
     *
     * @param path     where the file will be saved.
     * @param resource the name of the resource from the plugin.
     * @return The toml file instance.
     * @throws SimplixValidationException if the resource is not founded or another related error.
     */
    public static Toml registerToml(Path path, String resource) throws SimplixValidationException {
        Toml toml = createBuilder(path, resource, null, ConfigSettings.SKIP_COMMENTS, DataType.SORTED).createToml();
        toml.forceReload();
        return toml;
    }

    /**
     * Create a new instance of a Yaml File.
     *
     * @param path   where the file will be saved.
     * @param stream input stream of the resource
     * @return The yaml file instance.
     * @throws SimplixValidationException if the resource is not founded or another related error.
     */
    public static Yaml registerYaml(Path path, InputStream stream) throws SimplixValidationException {
        Yaml yaml = createBuilder(path, null, stream, ConfigSettings.PRESERVE_COMMENTS, DataType.SORTED).createYaml();
        yaml.addDefaultsFromInputStream();
        yaml.forceReload();
        return yaml;
    }

    /**
     * Create a new instance of a Json File.
     *
     * @param path   where the file will be saved.
     * @param stream input stream of the resource
     * @return The json file instance.
     * @throws SimplixValidationException if the resource is not founded or another related error.
     */
    public static Json registerJson(Path path, InputStream stream) throws SimplixValidationException {
        Json json = createBuilder(path, null, stream, ConfigSettings.SKIP_COMMENTS, DataType.SORTED).createJson();
        json.forceReload();
        return json;
    }

    /**
     * Create a new instance of a Toml File.
     *
     * @param path   where the file will be saved.
     * @param stream input stream of the resource
     * @return The toml file instance.
     * @throws SimplixValidationException if the resource is not founded or another related error.
     */
    public static Toml registerToml(Path path, InputStream stream) throws SimplixValidationException {
        Toml toml = createBuilder(path, null, stream, ConfigSettings.SKIP_COMMENTS, DataType.SORTED).createToml();
        toml.forceReload();
        return toml;
    }

    public static FlatFile registerFile(FileType type, Path path, InputStream stream) {
        return switch (type) {
            case JSON -> registerJson(path, stream);
            case YAML -> registerYaml(path, stream);
            case TOML -> registerToml(path, stream);
        };
    }

    public static FlatFile registerFile(FileType type, Path path, String resource) {
        return switch (type) {
            case JSON -> registerJson(path, resource);
            case YAML -> registerYaml(path, resource);
            case TOML -> registerToml(path, resource);
        };
    }

    public static FlatFile registerFile(FileType type, Path path, @Nullable String resource, @Nullable InputStream stream, ConfigSettings settings, DataType dataType) {
        return switch (type) {
            case JSON -> createBuilder(path, resource, stream, settings, dataType).createJson();
            case YAML ->
                    createBuilder(path, resource, stream, settings, dataType).createYaml().addDefaultsFromInputStream();
            case TOML -> createBuilder(path, resource, stream, settings, dataType).createToml();
        };
    }

    private static SimplixBuilder createBuilder(Path path, @Nullable String resource, @Nullable InputStream stream, ConfigSettings settings, DataType type) {
        SimplixBuilder b = SimplixBuilder.fromPath(path);
        if (resource != null) {
            b.addInputStreamFromResource(resource);
        } else if (stream != null) {
            b.addInputStream(stream);
        }
        return b.setConfigSettings(settings).setDataType(type);
    }

    public static WhitelistEntry stringToPlayer(String string) {
        if (string == null || string.isBlank() || string.equals("null") || string.equals("none")) return null;
        final String[] split = string.split(";");
        return new WhitelistEntry(split[0], UUIDUtil.parseUUID(split[1].trim()), Long.parseLong(split[2].trim()), Boolean.parseBoolean(split[3].trim()));
    }

    public enum FileType {
        JSON,
        YAML,
        TOML
    }
}

