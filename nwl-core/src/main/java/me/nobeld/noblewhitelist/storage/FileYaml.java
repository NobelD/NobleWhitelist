package me.nobeld.noblewhitelist.storage;

import de.leonhard.storage.Yaml;
import me.nobeld.noblewhitelist.config.FileManager;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.storage.root.FileFlat;

public class FileYaml extends FileFlat<Yaml> {
    public FileYaml(NWLData data) {
        super(data, "yml", true, p -> FileManager.registerYaml(p, (String) null));
    }
}
