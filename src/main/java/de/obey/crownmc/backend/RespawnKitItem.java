package de.obey.crownmc.backend;
/*

    Author - Obey -> SkySlayer-v4
       11.11.2022 / 17:54

*/

import de.obey.crownmc.CrownMain;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public final class RespawnKitItem {

    @Getter(AccessLevel.NONE)
    private final ServerConfig serverConfig = CrownMain.getInstance().getInitializer().getServerConfig();

    private final int type;

    @Getter
    private int slot;

    private final Map<Integer, ItemStack> levelItems = new HashMap<>();
    private final Map<Integer, Long> levelPrices = new HashMap<>();

    public RespawnKitItem(final int type) {
        this.type = type;

        switch (type) {
            case 1:
                slot = 11;
                break;
            case 2:
                slot = 20;
                break;
            case 3:
                slot = 29;
                break;
            case 4:
                slot = 38;
                break;
            case 5:
                slot = 15;
                break;
            case 6:
                slot = 24;
                break;
            case 7:
                slot = 33;
                break;
            case 8:
                slot = 42;
                break;
        }

        loadData();
    }

    private void loadData() {
        final YamlConfiguration cfg = serverConfig.getCfg();

        if (!cfg.contains("respawnkit." + type))
            return;

        final Set<String> levels = cfg.getConfigurationSection("respawnkit." + type).getKeys(false);

        levels.forEach(levelString -> {
            try {
                final int level = Integer.parseInt(levelString);

                if (cfg.contains("respawnkit." + type + "." + level + ".item"))
                    levelItems.put(level, cfg.getItemStack("respawnkit." + type + "." + level + ".item"));

                if (cfg.contains("respawnkit." + type + "." + level + ".price"))
                    levelPrices.put(level, cfg.getLong("respawnkit." + type + "." + level + ".price"));

            } catch (NumberFormatException exception) {
                exception.printStackTrace();
            }
        });
    }

    public long getPriceForLevel(final int level) {
        if (!levelPrices.containsKey(level))
            return 10000000000L;

        return levelPrices.get(level);
    }

    public ItemStack getItemForLevel(final int level) {
        if (!levelItems.containsKey(level))
            return new ItemStack(Material.BARRIER);

        return levelItems.get(level);
    }

    public int getMaxLevel() {
        return levelItems.size() - 1;
    }

}
