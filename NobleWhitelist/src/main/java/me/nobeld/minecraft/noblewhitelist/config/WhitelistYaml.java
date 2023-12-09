package me.nobeld.minecraft.noblewhitelist.config;

import de.leonhard.storage.Yaml;
import de.leonhard.storage.sections.FlatFileSection;
import me.nobeld.minecraft.noblewhitelist.model.FileGetter;
import me.nobeld.minecraft.noblewhitelist.model.whitelist.PlayerWhitelisted;
import me.nobeld.minecraft.noblewhitelist.util.UUIDUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static me.nobeld.minecraft.noblewhitelist.NobleWhitelist.getPlugin;
import static me.nobeld.minecraft.noblewhitelist.config.FileManager.*;

public class WhitelistYaml implements FileGetter {
    private List<PlayerWhitelisted> list = null;
    private Yaml whitelistFile;
    private final AtomicLong count = new AtomicLong(0);
    public void registerWhitelist() {
        Path filePath = Paths.get(getPlugin().getDataFolder().getPath() + separator() + "whitelist.yml");
        whitelistFile = registerYaml(filePath, null);
        migrateData();
    }
    public Yaml whitelistFile() {
        if (whitelistFile == null) {
            registerWhitelist();
        }
        return whitelistFile;
    }
    public void migrateData() {
        FlatFileSection sec = whitelistFile().getSection("whitelist");
        if (sec.singleLayerKeySet().isEmpty()) return;
        List<PlayerWhitelisted> list = new ArrayList<>();
        for (String s : sec.singleLayerKeySet()) {
            String re = sec.getString(s);
            if (re.equalsIgnoreCase("none")) re = null;
            if (s.startsWith("none$")) s = null;
            list.add(new PlayerWhitelisted(count.getAndIncrement(), s, UUIDUtil.parseUUID(re), -1, true));
        }
        whitelistFile().clear();
        bulkUpdate(list);
    }
    public void bulkUpdate(List<PlayerWhitelisted> list) {
        list.forEach(this::insertData);
        whitelistFile().set("temp", true);
        whitelistFile().remove("temp");
    }
    private void insertData(PlayerWhitelisted player) {
        whitelistFile().getFileData().insert(String.valueOf(player.getRowId()), player.getSubDataString());
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
        list.remove(player);
        whitelistFile().remove(String.valueOf(player.getRowId()));
    }
}
