package me.nobeld.minecraft.noblewhitelist.config;

import de.leonhard.storage.Json;
import me.nobeld.minecraft.noblewhitelist.model.FileGetter;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.getPlugin;
import static me.nobeld.minecraft.noblewhitelist.config.FileManager.*;

public class WhitelistJson implements FileGetter {
    private List<PlayerWhitelisted> list = null;
    private Json whitelistFile;
    private final AtomicLong count = new AtomicLong(0);
    public void registerWhitelist() {
        Path filePath = Paths.get(getPlugin().getDataFolder().getPath() + separator() + "whitelist.yml");
        whitelistFile = registerJson(filePath, null);
    }
    public Json whitelistFile() {
        if (whitelistFile == null) {
            registerWhitelist();
        }
        return whitelistFile;
    }
    @Override
    public void save(@NotNull PlayerWhitelisted player) {
        if (player.isSaved()){
            whitelistFile().set(String.valueOf(player.getRowId()), player.getSubDataString());
        } else {
            long num = count.getAndIncrement();
            whitelistFile().set(String.valueOf(num), player.getSubDataString());
            player.setRowId(num);
            getAll().add(player);
        }
    }
    @Override
    public boolean clear() {
        if (list == null || list.isEmpty()) return false;
        whitelistFile().clear();
        count.set(0);
        return true;
    }
    @Override
    public List<PlayerWhitelisted> getAll() {
        if (list != null) return list;
        list = new ArrayList<>();
        for (String id : whitelistFile().singleLayerKeySet()) {
            try {
                PlayerWhitelisted p = stringToPlayer(whitelistFile().getString(id));
                long row = Long.parseLong(id);
                p.setRowId(row);
                list.add(p);
                if (row > count.get()) count.set(row);
            } catch (Exception ignored) {}
        }
        return list;
    }
    @Override
    public void reload() {
        list = null;
        getAll();
    }
    @Override
    public void delete(@NotNull PlayerWhitelisted player) {
        getAll().remove(player);
        whitelistFile().remove(String.valueOf(player.getRowId()));
    }
}
