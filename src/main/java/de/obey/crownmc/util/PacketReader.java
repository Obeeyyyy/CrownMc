package de.obey.crownmc.util;
/*

    Author - Obey -> SkySlayer-v4
       17.10.2022 / 14:44

*/

import de.obey.crownmc.CrownMain;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInCustomPayload;
import net.minecraft.server.v1_8_R3.PacketPlayInTabComplete;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;

public final class PacketReader {

    private final MessageUtil messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();

    private final Player player;
    private Channel channel;

    @Getter
    @Setter
    private boolean logger = false;

    public PacketReader(final Player player) {
        this.player = player;

        inject();
    }

    private void inject() {
        final CraftPlayer craftPlayer = (CraftPlayer) this.player;
        channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
        channel.pipeline().addAfter("decoder", "ObeyStyle", new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext arg0, Packet<?> packet, List<Object> arg2) throws Exception {
                arg2.add(packet);
                readPacket(packet);
            }
        });
    }

    public void uninject() {
        if (channel != null) {
            if (channel.pipeline().get("ObeyStyle") != null) {
                channel.pipeline().remove("ObeyStyle");
            }
        }
    }

    private void readPacket(final Packet<?> thePacket) {

        if (logger) {

            if (!thePacket.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInFlying") &&
                    !thePacket.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInKeepAlive") &&
                    !thePacket.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInPlayInPosition") &&
                    !thePacket.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInLook") &&
                    !thePacket.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInPositionLook") &&
                    !thePacket.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInChat") &&
                    !thePacket.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInArmAnimation") &&
                    !thePacket.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInPosition")) {

                messageUtil.log(player.getName() + " -> " + thePacket.getClass().getSimpleName());

                Bukkit.getOnlinePlayers().forEach(admin -> {
                    if (!PermissionUtil.hasPermission(admin, "*", false))
                        return;

                    messageUtil.sendMessage(admin, "§8[§9§lPACKET§8] §a§o" + player.getName() + "§8 -> §f§o" + thePacket.getClass().getSimpleName());
                });
            }
        }

        if (thePacket.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInTabComplete")) {
            final PacketPlayInTabComplete packet = (PacketPlayInTabComplete) thePacket;
            final String cmd = packet.a().toLowerCase();

            if (cmd.equalsIgnoreCase("/ ") ||
                    cmd.length() == 1 ||
                    cmd.startsWith("/?") ||
                    cmd.startsWith("/ver") ||
                    cmd.startsWith("/icanhas") ||
                    cmd.contains(":") ||
                    cmd.startsWith("/about")) {
                setValue(thePacket, "a", "CrownMc.de");
            }

            //System.out.println(getValue(thePacket, "a"));
        }
        //System.out.println(packet.getClass().getSimpleName());
    }

    public void setValue(final Object obj, final String name, final Object value) {
        try {
            final Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getValue(final Object obj, final String name) {
        try {
            final Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

}
