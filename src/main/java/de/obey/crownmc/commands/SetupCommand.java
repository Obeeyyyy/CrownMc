package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       03.11.2022 / 16:58

*/

import de.obey.crownmc.handler.CrashHandler;
import de.obey.crownmc.handler.BlockEventHandler;
import de.obey.crownmc.handler.DailyPotHandler;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;

import java.util.UUID;

@RequiredArgsConstructor
@NonNull
public final class SetupCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final CrashHandler crashHandler;
    private final BlockEventHandler blockEventHandler;
    private final DailyPotHandler dailyPotHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "setup", true))
            return false;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("bounty")) {

                final ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);

                armorStand.setCustomName("§5§lReaper");
                armorStand.setCustomNameVisible(true);
                armorStand.setArms(true);
                armorStand.setGravity(false);
                armorStand.setBasePlate(false);

                armorStand.setHelmet(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setTextur("NzEzZDY5YTAxNWRjZmE4YWVhOWZjZjQ1OWJkYmQwZWVlMzkwNmMxOTA2ZGRjYjBlYTA4Yzc2ZjIzMzBhYjhiOSJ9fX0=", UUID.randomUUID()).build());
                armorStand.setChestplate(new ItemBuilder(Material.IRON_CHESTPLATE, 1).build());
                armorStand.setLeggings(new ItemBuilder(Material.IRON_LEGGINGS, 1).build());
                armorStand.setBoots(new ItemBuilder(Material.IRON_BOOTS, 1).build());

                armorStand.setRightArmPose(new EulerAngle(Math.toRadians(-30), Math.toRadians(-30), Math.toRadians(-30)));
                armorStand.setLeftArmPose(new EulerAngle(Math.toRadians(-30), Math.toRadians(30), Math.toRadians(30)));

                armorStand.setItemInHand(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setTextur("MjA5Mjk5YTExN2JlZTg4ZDMyNjJmNmFiOTgyMTFmYmEzNDRlY2FlMzliNDdlYzg0ODEyOTcwNmRlZGM4MWU0ZiJ9fX0=", UUID.randomUUID()).build());

                return false;
            }

            if (args[0].equalsIgnoreCase("crash")) {
                crashHandler.setupArmorStands();
                messageUtil.sendMessage(player, "§a§oCrash setup ...");
                return false;
            }

            if (args[0].equalsIgnoreCase("blockevent")) {
                blockEventHandler.setupArmorStands();
                messageUtil.sendMessage(player, "Blockevent wurde aufgesetzt§8.");
                return false;
            }

            if (args[0].equalsIgnoreCase("dailypot")) {
                dailyPotHandler.setupArmorStands();
                messageUtil.sendMessage(player, "Dailypot wurde aufgesetzt§8.");
                return false;
            }
        }

        messageUtil.sendSyntax(sender, "/setup <bounty, crash, blockevent, dailypot>");
        messageUtil.sendMessage(sender, "Benötigte Locations: crash1, crash2, crash3, crashGraph, blockevent, dailypot");

        return false;
    }
}
