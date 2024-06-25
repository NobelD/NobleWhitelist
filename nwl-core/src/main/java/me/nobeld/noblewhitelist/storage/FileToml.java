package me.nobeld.noblewhitelist.storage;

import de.leonhard.storage.Toml;
import me.nobeld.noblewhitelist.config.FileManager;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.storage.root.FileFlat;

public class FileToml extends FileFlat<Toml> {
    public FileToml(NWLData data) {
        super(data, "toml", false, p -> FileManager.registerToml(p, (String) null));
    }
}
