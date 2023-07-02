package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       03.11.2022 / 16:58

*/

import de.obey.crownmc.handler.CrashHandler;
import de.obey.crownmc.handler.BlockEventHandler;
import de.obey.crownmc.handler.DailyPotHandler;
import de.obey.crownmc.handler.LuckySpinHandler;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.DyeColor;
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
    private final LuckySpinHandler luckySpinHandler;

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
                armorStand.setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setColor(DyeColor.PURPLE).build());
                armorStand.setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setColor(DyeColor.PURPLE).build());
                armorStand.setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setColor(DyeColor.PURPLE).build());

                armorStand.setRightArmPose(new EulerAngle(Math.toRadians(-30), Math.toRadians(-30), Math.toRadians(-30)));
                armorStand.setLeftArmPose(new EulerAngle(Math.toRadians(-30), Math.toRadians(30), Math.toRadians(30)));

                armorStand.setItemInHand(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setTextur("MjA5Mjk5YTExN2JlZTg4ZDMyNjJmNmFiOTgyMTFmYmEzNDRlY2FlMzliNDdlYzg0ODEyOTcwNmRlZGM4MWU0ZiJ9fX0=", UUID.randomUUID()).build());

                return false;
            }

            if (args[0].equalsIgnoreCase("portal")) {

                final ArmorStand armorStand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);

                armorStand.setCustomName("§5§lPortal §7Meister");
                armorStand.setCustomNameVisible(true);
                armorStand.setArms(true);
                armorStand.setGravity(false);
                armorStand.setBasePlate(false);

                armorStand.setHelmet(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setTextur("ODM3ZjE2MTdjMDE0OWE1NjA3NmIzMDI1Y2RhMjU3YjAwMTJmMmEyZmI1ZWFkYTNmZDczMDRiMmZkZGE4OWZjMCJ9fX0=", UUID.randomUUID()).build());
                armorStand.setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).setColor(DyeColor.BLACK).build());
                armorStand.setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).setColor(DyeColor.BLACK).build());
                armorStand.setBoots(new ItemBuilder(Material.LEATHER_BOOTS).setColor(DyeColor.BLACK).build());

                armorStand.setRightArmPose(new EulerAngle(Math.toRadians(338), Math.toRadians(335), Math.toRadians(0)));

                armorStand.setItemInHand(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3).setTextur("Mzc3ZDRhMjA2ZDc3NTdmNDc5ZjMzMmVjMWEyYmJiZWU1N2NlZjk3NTY4ZGQ4OGRmODFmNDg2NGFlZTdkM2Q5OCJ9fX0=", UUID.randomUUID()).build());

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

            if (args[0].equalsIgnoreCase("lw")) {
                luckySpinHandler.shutdown();
                luckySpinHandler.setup();
                messageUtil.sendMessage(player, "LuckySpin wurde aufgesetzt§8.");
                return false;
            }
        }

        messageUtil.sendSyntax(sender, "/setup <bounty, crash, blockevent, dailypot, lw, portal>");
        messageUtil.sendMessage(sender, "Benötigte Locations: crash1, crash2, crash3, crashGraph, blockevent, dailypot, luckyspin, luckyspinwall");

        return false;
    }
}
