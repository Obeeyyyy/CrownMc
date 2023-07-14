package de.obey.crownmc.handler;
/*

    Author - Obey -> SkySlayer-v4
       26.11.2022 / 16:51

*/

import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.commands.BuildCommand;
import de.obey.crownmc.objects.WorldProtection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

@RequiredArgsConstructor
public final class WorldProtectionHandler {

    @NonNull
    private final ServerConfig serverConfig;

    private final HashMap<World, WorldProtection> worldWorldProtectionMap = new HashMap<>();

    public void loadWorlds() {
        final YamlConfiguration cfg = serverConfig.getCfg();

        if (!cfg.contains("worlds"))
            return;

        cfg.getConfigurationSection("worlds").getKeys(false).forEach(worldName ->
                worldWorldProtectionMap.put(Bukkit.getWorld(worldName), new WorldProtection(worldName, cfg)));
    }

    public void save() {
        final YamlConfiguration cfg = serverConfig.getCfg();

        worldWorldProtectionMap.values().forEach(worldProtection -> worldProtection.saveWorldProtection(cfg));
    }

    public void toggle(final String worldName, final String what) {
        if (Bukkit.getWorld(worldName) == null)
            return;

        WorldProtection worldProtection = worldWorldProtectionMap.get(Bukkit.getWorld(worldName));

        if (worldProtection == null) {
            worldWorldProtectionMap.put(Bukkit.getWorld(worldName), new WorldProtection(worldName, serverConfig.getCfg()));
            worldProtection = worldWorldProtectionMap.get(Bukkit.getWorld(worldName));
        }

        if (what.equalsIgnoreCase("pvp"))
            worldProtection.setPvp(!worldProtection.isPvp());

        if (what.equalsIgnoreCase("build"))
            worldProtection.setBuild(!worldProtection.isBuild());

        if (what.equalsIgnoreCase("mobspawn"))
            worldProtection.setMobspawn(!worldProtection.isMobspawn());

        if (what.equalsIgnoreCase("blockexplosion"))
            worldProtection.setBlockexplosion(!worldProtection.isBlockexplosion());

        if (what.equalsIgnoreCase("ep"))
            worldProtection.setEnderpearl(!worldProtection.isEnderpearl());

        if (what.equalsIgnoreCase("interact"))
            worldProtection.setInteract(!worldProtection.isInteract());

        if (what.equalsIgnoreCase("fly"))
            worldProtection.setFly(!worldProtection.isFly());

        if (what.equalsIgnoreCase("pve"))
            worldProtection.setPve(!worldProtection.isPve());

        if (what.equalsIgnoreCase("homes"))
            worldProtection.setHomes(!worldProtection.isHomes());

        if (what.equalsIgnoreCase("projectiles"))
            worldProtection.setProjectiles(!worldProtection.isProjectiles());

        if (what.equalsIgnoreCase("itemdrops"))
            worldProtection.setItemDrops(!worldProtection.isItemDrops());
    }

    public boolean canBuild(final Player player) {
        final WorldProtection protection = getWorldProtection(player.getWorld());

        return protection == null || protection.isBuild() || BuildCommand.buildMode.contains(player.getUniqueId());
    }

    public boolean canBuild(final World world) {
        final WorldProtection protection = getWorldProtection(world);

        return protection == null || protection.isBuild();
    }

    public boolean canFightPlayers(final Player player) {
        final WorldProtection protection = getWorldProtection(player.getWorld());

        return protection == null || protection.isPvp();
    }

    public boolean canProjectile(final World world)  {
        final WorldProtection protection = getWorldProtection(world);

        return protection == null || protection.isProjectiles();
    }

    public boolean canFightMobs(final World world) {
        final WorldProtection protection = getWorldProtection(world);

        return protection == null || protection.isPve();
    }

    public boolean canInteract(final Player player) {
        final WorldProtection protection = getWorldProtection(player.getWorld());

        return protection == null || protection.isInteract() || BuildCommand.buildMode.contains(player.getUniqueId());
    }

    public boolean canEp(final Player player) {
        final WorldProtection protection = getWorldProtection(player.getWorld());

        return protection == null || protection.isEnderpearl();
    }

    public boolean canDrop(final Player player) {
        final WorldProtection protection = getWorldProtection(player.getWorld());

        return protection == null || protection.isItemDrops();
    }

    public WorldProtection getWorldProtection(final World world) {
        return worldWorldProtectionMap.get(world);
    }

}
