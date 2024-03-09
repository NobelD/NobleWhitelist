package me.nobeld.noblewhitelist.storage;

import de.leonhard.storage.Toml;
import me.nobeld.noblewhitelist.config.FileManager;
import me.nobeld.noblewhitelist.storage.root.FileFlat;

public class FileToml extends FileFlat<Toml> {
    public FileToml() {
        super("toml", false, p -> FileManager.registerToml(p, (String) null));
    }
    @Override
    public Toml whitelistFile() {
        return super.whitelistFile();
    }
}
