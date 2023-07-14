package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       26.11.2022 / 17:04

*/

import de.obey.crownmc.handler.WorldProtectionHandler;
import de.obey.crownmc.objects.WorldProtection;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class ProtectionCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final WorldProtectionHandler worldProtectionHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "protection", true))
            return false;

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                final World world = Bukkit.getWorld(args[1]);

                if (world == null) {
                    messageUtil.sendMessage(player, "Diese Welt existiert nicht§8.");
                    return false;
                }

                final WorldProtection worldProtection = worldProtectionHandler.getWorldProtection(world);

                if (worldProtection == null) {
                    messageUtil.sendMessage(player, "Die Welt " + world.getName() + " ist ungeschützt§8.");
                    return false;
                }

                messageUtil.sendMessage(player, "Infos über " + args[1]);
                player.sendMessage("§8- §7PvP§8: §f" + worldProtection.isPvp());
                player.sendMessage("§8- §7Build§8: §f" + worldProtection.isBuild());
                player.sendMessage("§8- §7Mobspawn§8: §f" + worldProtection.isMobspawn());
                player.sendMessage("§8- §7Blockexplosion§8: §f" + worldProtection.isBlockexplosion());
                player.sendMessage("§8- §7Enderperlen§8: §f" + worldProtection.isEnderpearl());
                player.sendMessage("§8- §7Interact§8: §f" + worldProtection.isInteract());
                player.sendMessage("§8- §7Fly§8: §f" + worldProtection.isFly());
                player.sendMessage("§8- §7PvE§8: §f" + worldProtection.isPve());
                player.sendMessage("§8- §7Homes§8: §f" + worldProtection.isHomes());
                player.sendMessage("§8- §7Projectiles§8: §f" + worldProtection.isProjectiles());
                player.sendMessage("§8- §7ItemDrops§8: §f" + worldProtection.isItemDrops());

                return false;
            }

            if (args[0].equalsIgnoreCase("kill")) {
                try {
                    final EntityType type = EntityType.valueOf(args[1].toUpperCase());

                    int count = 0;

                    for (Entity entity : player.getWorld().getEntities()) {
                        if (entity.getType() == type) {
                            entity.remove();
                            count++;
                        }
                    }

                    messageUtil.sendMessage(player, "Es wurden x" + count + " " + type.name() + " getötet§8.");
                } catch (final IllegalArgumentException exception) {
                    messageUtil.sendMessage(player, "Invalid Type, all Type§8:");
                    for (EntityType value : EntityType.values()) {
                        player.sendMessage("§8 - §7" + value.name());
                    }
                }
                return false;
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("toggle")) {
                if (args[2].equalsIgnoreCase("pvp") ||
                        args[2].equalsIgnoreCase("build") ||
                        args[2].equalsIgnoreCase("blockexplosion") ||
                        args[2].equalsIgnoreCase("ep") ||
                        args[2].equalsIgnoreCase("interact") ||
                        args[2].equalsIgnoreCase("fly") ||
                        args[2].equalsIgnoreCase("pve") ||
                        args[2].equalsIgnoreCase("homes") ||
                        args[2].equalsIgnoreCase("projectiles") ||
                        args[2].equalsIgnoreCase("itemdrops") ||
                        args[2].equalsIgnoreCase("mobspawn")) {
                    worldProtectionHandler.toggle(args[1], args[2]);
                    messageUtil.sendMessage(player, args[2] + " in der Welt " + args[1] + " getoggled§8.");

                    return false;
                }
            }
        }

        messageUtil.sendSyntax(player, "/protection kill <type>",
                "/protection info <world>",
                "/protection toggle <world> <pvp, build, mobspawn, blockexplosion, interact, ep, fly, pve, homes, projectiles, itemdrops>");

        return false;
    }
}
