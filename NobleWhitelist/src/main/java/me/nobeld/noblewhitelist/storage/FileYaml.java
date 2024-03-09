package me.nobeld.noblewhitelist.storage;

import de.leonhard.storage.Yaml;
import me.nobeld.noblewhitelist.config.FileManager;
import me.nobeld.noblewhitelist.storage.root.FileFlat;

public class FileYaml extends FileFlat<Yaml> {
    public FileYaml() {
        super("yml", true, p -> FileManager.registerYaml(p, (String) null));
    }
    @Override
    public Yaml whitelistFile() {
        return super.whitelistFile();
    }
}
