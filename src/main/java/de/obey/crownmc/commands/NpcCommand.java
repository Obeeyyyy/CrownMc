package de.obey.crownmc.commands;

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.handler.NpcHandler;
import de.obey.crownmc.objects.CNPC;
import de.obey.crownmc.util.LocationUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor @NonNull
public class NpcCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final NpcHandler npcHandler;

    private final HashMap<UUID, CNPC> selected = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        final Player player = (Player) sender;

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("list")) {
                if (npcHandler.getNpcs().isEmpty()) {
                    messageUtil.sendMessage(sender, "§c§oEs existieren noch keine NPCS§8.");
                    return false;
                }

                for (final CNPC npc : npcHandler.getNpcs().values()) {

                    messageUtil.sendMessage(player, " -> "+ npc.getName());
                    messageUtil.sendMessage(player, "    Command: " + npc.getCommand());
                    player.sendMessage("");
                }

                return false;
            }
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("select")) {

                if(!npcHandler.getNpcs().containsKey(args[1])) {
                    messageUtil.sendMessage(sender, "Es existiert kein NPC mit dem Namen " + args[1] + "§8.");
                    return false;
                }

                selected.put(player.getUniqueId(), npcHandler.getNpcs().get(args[1]));
                messageUtil.sendMessage(sender, args[1] + " ausgewählt§8");

                return false;
            }

            if(args[0].equalsIgnoreCase("create")) {

                if(!npcHandler.createNewNPC(args[1], player.getLocation())) {
                    messageUtil.sendMessage(sender, "Es exisiert bereits ein NPC mit dem Namen " + args[1] + "§8.");
                    return false;
                }

                messageUtil.sendMessage(sender,args[1] + " wurde erstellt§8.");
                selected.put(player.getUniqueId(), npcHandler.getNpcs().get(args[1]));

                return false;
            }

            if(args[0].equalsIgnoreCase("delete")) {

                if(!npcHandler.deleteNPC(args[1])) {
                    messageUtil.sendMessage(sender, "Es exisiert kein NPC mit dem Namen " + args[1] + "§8.");
                    return false;
                }

                messageUtil.sendMessage(sender,args[1] + " wurde gelöscht§8.");

                return false;
            }
        }

        if(selected.containsKey(player.getUniqueId())) {
            final CNPC npc = selected.get(player.getUniqueId());

            if (args.length == 1) {

                if(args[0].equalsIgnoreCase("location")) {
                    npc.setLocation(player.getLocation());
                    return false;
                }

                if (args[0].equalsIgnoreCase("helm")) {
                    npc.setHelmet(player.getItemInHand());
                    return false;
                }

                if (args[0].equalsIgnoreCase("brust")) {
                    npc.setChestPlate(player.getItemInHand());
                    return false;
                }

                if (args[0].equalsIgnoreCase("hose")) {
                    npc.setLeggings(player.getItemInHand());
                    return false;
                }

                if (args[0].equalsIgnoreCase("boots")) {
                    npc.setBoots(player.getItemInHand());
                    return false;
                }

                if (args[0].equalsIgnoreCase("hand")) {
                    npc.setHand(player.getItemInHand());
                    return false;
                }
            }

            if (args.length == 2) {

                if (args[0].equalsIgnoreCase("larm") ||
                        args[0].equalsIgnoreCase("rarm")) {

                    final EulerAngle angle = LocationUtil.decodeEuler(args[1]);

                    if (angle == null) {
                        messageUtil.sendMessage(sender, "Nutze #0#0#0 um die angle zu beschreiben.");
                        return false;
                    }

                    if (args[0].equalsIgnoreCase("larm")) {
                        npc.setLeftArm(angle);
                        messageUtil.sendMessage(sender, "Left Arm Angle gesetzt§8.");

                        return false;
                    }

                    if (args[0].equalsIgnoreCase("rarm")) {
                        npc.setRightArm(angle);
                        messageUtil.sendMessage(sender, "Right Arm Angle gesetzt§8.");

                        return false;
                    }

                    return false;
                }

                final boolean state = Boolean.parseBoolean(args[1]);

                if (args[0].equalsIgnoreCase("small")) {
                    npc.setSmall(state);
                    messageUtil.sendMessage(sender, "small -> " + state);
                    return false;
                }

                if (args[0].equalsIgnoreCase("arms")) {
                    npc.setShowArms(state);
                    messageUtil.sendMessage(sender, "arms -> " + state);
                    return false;
                }
                if (args[0].equalsIgnoreCase("visible")) {
                    npc.setVisible(state);
                    messageUtil.sendMessage(sender, "visible -> " + state);
                    return false;
                }
                if (args[0].equalsIgnoreCase("namevisible")) {
                    npc.setShowName(state);
                    messageUtil.sendMessage(sender, "namevisible -> " + state);
                    return false;
                }
            }

            if (args.length >= 2) {
                if (args[0].equalsIgnoreCase("prefix")) {
                    String text = args[1];

                    if (args.length > 2) {
                        for (int i = 2; i < args.length; i++)
                            text = text + " " + args[i];
                    }

                    text = ChatColor.translateAlternateColorCodes('&', text);

                    npc.setPrefix(text);
                    messageUtil.sendMessage(sender, "prefix -> " + text);

                    return false;
                }

                if (args[0].equalsIgnoreCase("command")) {
                    String text = args[1];

                    if (args.length > 2) {
                        for (int i = 2; i < args.length; i++)
                            text = text + " " + args[i];
                    }

                    text = ChatColor.translateAlternateColorCodes('&', text);

                    npc.setCommand(text);
                    messageUtil.sendMessage(sender, "command -> " + text);

                    return false;
                }
            }
        }

        messageUtil.sendSyntax(sender,
                "/npc list",
                "/npc select <name>",
                "/npc create <name>",
                "/npc delete <name>",
                "/npc prefix <value>",
                "/npc command </spawn>",
                "/npc location",
                "/npc helm",
                "/npc brust",
                "/npc hose",
                "/npc boots",
                "/npc hand",
                "/npc larm <#0#0#0>",
                "/npc rarm <#0#0#0>",
                "/npc visible <true/false>",
                "/npc namevisible <true/false>",
                "/npc arms <true/false>",
                "/npc small <true/false>"
        );

        return false;
    }

    @EventHandler
    public void on(final PlayerInteractAtEntityEvent event) {
        if(!(event.getRightClicked() instanceof ArmorStand))
            return;

        final CNPC npc = npcHandler.getNpcFromInteract((ArmorStand) event.getRightClicked());

        if(npc == null)
            return;

        if(npc.getCommand() == null)
            return;

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(event.getPlayer(), npc.getCommand());
            }
        }.runTask(CrownMain.getInstance());
    }
}
