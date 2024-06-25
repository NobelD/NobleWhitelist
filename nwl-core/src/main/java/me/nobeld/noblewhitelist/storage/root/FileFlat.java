package me.nobeld.noblewhitelist.storage.root;

import de.leonhard.storage.internal.FlatFile;
import de.leonhard.storage.sections.FlatFileSection;
import me.nobeld.noblewhitelist.model.PairData;
import me.nobeld.noblewhitelist.model.base.NWLData;
import me.nobeld.noblewhitelist.model.base.PlayerWrapper;
import me.nobeld.noblewhitelist.model.storage.DataGetter;
import me.nobeld.noblewhitelist.model.whitelist.WhitelistEntry;
import me.nobeld.noblewhitelist.util.UUIDUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileFlat<T extends FlatFile> implements DataGetter {
    private List<WhitelistEntry> list = null;
    private T whitelistFile;
    private final AtomicLong count = new AtomicLong(0);
    private final String suffix;
    private final boolean migrate;
    private final Function<Path, T> consumer;
    private final NWLData data;

    public FileFlat(NWLData data, String suffix, boolean migrate, Function<Path, T> consumer) {
        this.data = data;
        this.suffix = suffix;
        this.migrate = migrate;
        this.consumer = consumer;
        whitelistFile();
    }

    public void registerWhitelist() {
        Path filePath = Paths.get(data.configPath() + "whitelist." + suffix);
        whitelistFile = consumer.apply(filePath);
        if (migrate) migrateData();
    }

    public T whitelistFile() {
        if (whitelistFile == null) {
            registerWhitelist();
        }
        return whitelistFile;
    }

    public void migrateData() {
        FlatFileSection sec = whitelistFile().getSection("whitelist");
        if (sec.singleLayerKeySet().isEmpty()) return;
        List<WhitelistEntry> list = new ArrayList<>();
        for (String s : sec.singleLayerKeySet()) {
            String re = sec.getString(s);
            if (re.equalsIgnoreCase("none")) re = null;
            if (s.startsWith("none$")) s = null;
            list.add(new WhitelistEntry(count.getAndIncrement(), s, UUIDUtil.parseUUID(re), -1, true));
        }
        whitelistFile().clear();
        bulkUpdate(list);
    }

    public void bulkUpdate(List<WhitelistEntry> list) {
        list.forEach(this::insertData);
        whitelistFile().set("temp", true);
        whitelistFile().remove("temp");
    }

    private void insertData(WhitelistEntry player) {
        whitelistFile().getFileData().insert(String.valueOf(player.getRowId()), player.getSubDataString());
    }

    public List<WhitelistEntry> getAll() {
        if (list != null) return list;
        list = new ArrayList<>();
        for (String id : whitelistFile().singleLayerKeySet()) {
            try {
                PairData<WhitelistEntry, Boolean> entry = reformatString(whitelistFile().getString(id));
                WhitelistEntry p = entry.getFirst();
                if (entry.getSecond()) {
                    whitelistFile().set(id, entry.getFirst().getSubDataString());
                }
                long row = Long.parseLong(id);
                p.setRowId(row);
                list.add(p);
                if (row >= count.get()) count.set(row + 1);
            } catch (Exception ignored) {
            }
        }
        return list;
    }

    public static PairData<WhitelistEntry, Boolean> reformatString(String string) {
        if (string == null || string.isBlank() || string.equals("null") || string.equals("none")) return null;
        final String[] split = string.split(";");
        String name, uuid = "";
        String id = "-1";
        String whitelisted = "true";
        boolean incomplete = split.length < 4;
        if (split.length == 0) return null;

        name = split[0];
        if (split.length >= 2) uuid = split[1];
        if (split.length >= 3) id = split[2];
        if (split.length >= 4) whitelisted = split[3];

        return PairData.of(new WhitelistEntry(name, UUIDUtil.parseUUID(uuid.trim()), parseLong(id.trim()), Boolean.parseBoolean(whitelisted.trim())), incomplete);
    }

    private static long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException ignored) {
            return -1L;
        }
    }

    @Override
    public void save(@NotNull WhitelistEntry player) {
        if (player.isSaved()) {
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
        list = null;
        count.set(0);
        return true;
    }

    @Override
    public void reload() {
        list = null;
        getAll();
    }

    @Override
    public void delete(@NotNull WhitelistEntry player) {
        getAll().remove(player);
        whitelistFile().remove(String.valueOf(player.getRowId()));
    }

    @Override
    public WhitelistEntry loadPlayer(@NotNull String name) {
        return getAll().stream().filter(p -> p.getOptName().filter(n -> n.equalsIgnoreCase(name)).isPresent()).findFirst().orElse(null);
    }

    @Override
    public WhitelistEntry loadPlayer(@NotNull UUID uuid) {
        return getAll().stream().filter(p -> p.getOptUUID().filter(u -> u.equals(uuid)).isPresent()).findFirst().orElse(null);
    }

    @Override
    public WhitelistEntry loadPlayer(long id) {
        return getAll().stream().filter(p -> p.getDiscordID() == id).findFirst().orElse(null);
    }

    @Override
    public List<WhitelistEntry> loadAccounts(long id) {
        return getAll().stream().filter(p -> p.getDiscordID() == id).toList();
    }

    @Override
    public WhitelistEntry loadPlayer(@NotNull PlayerWrapper player) {
        return getAll().stream().filter(p -> {
            if (p.getOptName().filter(n -> n.equalsIgnoreCase(player.getName())).isPresent()) {
                return true;
            } else return p.getOptUUID().filter(u -> u.equals(player.getUUID())).isPresent();
        }).findFirst().orElse(null);
    }

    @Override
    public List<WhitelistEntry> listIndex(int page) {
        if (page <= 1) return getAll().stream().limit(10).collect(Collectors.toList());
        int amount = 10 * (page - 1);
        return getAll().stream().skip(amount).limit(10).collect(Collectors.toList());
    }

    @Override
    public long getTotal() {
        return getAll().size();
    }
}
