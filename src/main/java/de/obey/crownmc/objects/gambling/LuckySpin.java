package de.obey.crownmc.objects.gambling;
/*

    Author - Obey -> CrownMc
       15.06.2023 / 02:20

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.handler.LuckySpinHandler;
import de.obey.crownmc.util.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public final class LuckySpin {

    private final String identifier = "§6";

    private final LuckySpinHandler luckySpinHandler;

    @Getter
    private int state = 0; // 0 = free - 1 = spinning

    private final ArrayList<ArmorStand> armorStands = new ArrayList<>();

    @Setter
    private Location location;

    public LuckySpin(final LuckySpinHandler luckySpinHandler, final Location location) {
        this.luckySpinHandler = luckySpinHandler;
        this.location = location;

        for (Entity entity : location.getChunk().getEntities()) {
            if(entity instanceof ArmorStand && entity.isCustomNameVisible())
                entity.remove();
        }

        setup();
    }

    public void setup() {
        killAllStands();

        state = 0;

        // spawning marker
        final ArmorStand marker1 = location.getWorld().spawn(location.clone().add(0, 2.25, 0), ArmorStand.class);
        marker1.setVisible(false);
        marker1.setGravity(false);
        marker1.setCustomName("§6§a§l♦♦♦");
        marker1.setCustomNameVisible(true);

        final ArmorStand marker2 = location.getWorld().spawn(location.clone().add(0, 0.5, 0), ArmorStand.class);
        marker2.setVisible(false);
        marker2.setGravity(false);
        marker2.setCustomName("§6§a§l♦♦♦");
        marker2.setCustomNameVisible(true);
        // done

        /*

            ❑❑❑
           ❑    ❑
           ❑    ❑
           ❑    ❑
            ❑❑❑
         */

        // line 1
        spawnStand(location.clone().add(0, 2, 0), new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("M2VkNmU0YmQ4MTNmZGViNGNiNTQzZjgxOTk0Y2NiYzI2YjhlNDYwMjIxMjM5MTFmZDdlZWYzMjJmMGQ3ZDNlNyJ9fX0=", UUID.randomUUID())
                .setDisplayname("§8§l?")
                .build());

        spawnStand(location.clone().add(1, 2, 0), new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("ZDIzZWFlZmJkNTgxMTU5Mzg0Mjc0Y2RiYmQ1NzZjZWQ4MmViNzI0MjNmMmVhODg3MTI0ZjllZDMzYTY4NzJjIn19fQ==", UUID.randomUUID())
                .setDisplayname("§8§l?")
                .build());

        spawnStand(location.clone().add(-1, 2, 0), new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("ZDIzZWFlZmJkNTgxMTU5Mzg0Mjc0Y2RiYmQ1NzZjZWQ4MmViNzI0MjNmMmVhODg3MTI0ZjllZDMzYTY4NzJjIn19fQ==", UUID.randomUUID())
                .setDisplayname("§8§l?")
                .build());

        //line 2
        spawnStand(location.clone().add(-2, 1, 0), new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("ZDIzZWFlZmJkNTgxMTU5Mzg0Mjc0Y2RiYmQ1NzZjZWQ4MmViNzI0MjNmMmVhODg3MTI0ZjllZDMzYTY4NzJjIn19fQ==", UUID.randomUUID())
                .setDisplayname("§8§l?")
                .build());

        spawnStand(location.clone().add(2, 1, 0), new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("ZDIzZWFlZmJkNTgxMTU5Mzg0Mjc0Y2RiYmQ1NzZjZWQ4MmViNzI0MjNmMmVhODg3MTI0ZjllZDMzYTY4NzJjIn19fQ==", UUID.randomUUID())
                .setDisplayname("§8§l?")
                .build());

        // line 3
        spawnStand(location.clone().add(-2, 0, 0), new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("ZDIzZWFlZmJkNTgxMTU5Mzg0Mjc0Y2RiYmQ1NzZjZWQ4MmViNzI0MjNmMmVhODg3MTI0ZjllZDMzYTY4NzJjIn19fQ==", UUID.randomUUID())
                .setDisplayname("§8§l?")
                .build());

        spawnStand(location.clone().add(2, 0, 0), new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("ZDIzZWFlZmJkNTgxMTU5Mzg0Mjc0Y2RiYmQ1NzZjZWQ4MmViNzI0MjNmMmVhODg3MTI0ZjllZDMzYTY4NzJjIn19fQ==", UUID.randomUUID())
                .setDisplayname("§8§l?")
                .build());

        // line 4
        spawnStand(location.clone().add(-2, -1, 0), new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("ZDIzZWFlZmJkNTgxMTU5Mzg0Mjc0Y2RiYmQ1NzZjZWQ4MmViNzI0MjNmMmVhODg3MTI0ZjllZDMzYTY4NzJjIn19fQ==", UUID.randomUUID())
                .setDisplayname("§8§l?")
                .build());

        spawnStand(location.clone().add(2, -1, 0), new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("ZDIzZWFlZmJkNTgxMTU5Mzg0Mjc0Y2RiYmQ1NzZjZWQ4MmViNzI0MjNmMmVhODg3MTI0ZjllZDMzYTY4NzJjIn19fQ==", UUID.randomUUID())
                .setDisplayname("§8§l?")
                .build());

        // line 5
        spawnStand(location.clone().add(-1, -2, 0), new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("ZDIzZWFlZmJkNTgxMTU5Mzg0Mjc0Y2RiYmQ1NzZjZWQ4MmViNzI0MjNmMmVhODg3MTI0ZjllZDMzYTY4NzJjIn19fQ==", UUID.randomUUID())
                .setDisplayname("§8§l?")
                .build());

        spawnStand(location.clone().add(0, -2, 0), new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("ZDIzZWFlZmJkNTgxMTU5Mzg0Mjc0Y2RiYmQ1NzZjZWQ4MmViNzI0MjNmMmVhODg3MTI0ZjllZDMzYTY4NzJjIn19fQ==", UUID.randomUUID())
                .setDisplayname("§8§l?")
                .build());

        spawnStand(location.clone().add(1, -2, 0), new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("ZDIzZWFlZmJkNTgxMTU5Mzg0Mjc0Y2RiYmQ1NzZjZWQ4MmViNzI0MjNmMmVhODg3MTI0ZjllZDMzYTY4NzJjIn19fQ==", UUID.randomUUID())
                .setDisplayname("§8§l?")
                .build());
    }

    private void spawnStand(final Location location, final ItemStack item) {
        final ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);

        stand.setVisible(false);
        stand.setGravity(false);
        stand.setMarker(true);
        stand.setCustomName(identifier);
        stand.setCustomNameVisible(false);

        stand.setHelmet(item);
        armorStands.add(stand);
    }

    public void killAllStands() {
        if(armorStands.size() > 0) {
            for (ArmorStand temp : armorStands) {
                if(temp == null)
                    continue;

                temp.setCustomNameVisible(false);
                temp.remove();
            }

            armorStands.clear();
        }

        for (final World world : Bukkit.getWorlds()) {
            for (final Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStand && entity.getCustomName() != null) {
                    if (entity.getCustomName().startsWith(identifier))
                        entity.remove();
                }
            }
        }
    }

}
