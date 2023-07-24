package de.obey.crownmc.handler;

import com.intellectualcrafters.plot.config.C;
import de.obey.crownmc.objects.CNPC;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

@RequiredArgsConstructor @NonNull
public class NpcHandler {
    private final MessageUtil messageUtil;
    private final File file = FileUtil.getFile("npcs.yml");
    private final YamlConfiguration cfg = FileUtil.getCfg(file);

    @Getter
    private final HashMap<String, CNPC> npcs = new HashMap<>();

    public void loadStands() {
        shutdown();

        if(!cfg.contains("npc"))
            return;

        final Set<String> list = cfg.getConfigurationSection("npc").getKeys(false);

        messageUtil.log("Loading " + list.size() + " npcs.");

        for (final String name : list) {
            npcs.put(name, new CNPC(name, cfg));
            messageUtil.log("Loaded " + name + " (NPC)");
        }
    }

    public CNPC getNpcFromInteract(final ArmorStand armorStand) {
        for (CNPC value : npcs.values()) {
            if(value.getArmorStand() == armorStand)
                return value;
        }

        return null;
    }

    public boolean createNewNPC(final String name, final Location location) {
        if(npcs.containsKey(name))
            return false;

        final CNPC cnpc = new CNPC(name, cfg);

        cnpc.setLocation(location);
        cnpc.spawnStand();

        npcs.put(name, cnpc);

        return true;
    }

    public boolean deleteNPC(final String name) {
        if(!npcs.containsKey(name))
            return false;

        final CNPC npc = npcs.get(name);

        cfg.set("npc." + name, null);
        npc.removeStand();
        npcs.remove(name);

        return true;
    }

    public void shutdown() {
        save();
        npcs.clear();
        for (final World world : Bukkit.getWorlds()) {
            for (final Entity entity : world.getEntities()) {
                if(!(entity instanceof ArmorStand))
                    continue;

                if(entity.getCustomName() == null)
                    continue;

                if(ChatColor.stripColor(entity.getCustomName()).startsWith("001"))
                    entity.remove();
            }
        }
    }

    public void save() {
        if(npcs.isEmpty())
            return;

        for (final CNPC value : npcs.values())
            value.save();

        FileUtil.saveToFile(file, cfg);
    }

}
