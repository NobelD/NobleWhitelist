package me.nobeld.noblewhitelist.storage;

import de.leonhard.storage.Json;
import me.nobeld.noblewhitelist.config.FileManager;
import me.nobeld.noblewhitelist.storage.root.FileFlat;

public class FileJson extends FileFlat<Json> {
    public FileJson() {
        super("json", false, p -> FileManager.registerJson(p , (String) null));
    }
    @Override
    public Json whitelistFile() {
        return super.whitelistFile();
    }
}
