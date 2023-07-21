package de.obey.crownmc.objects;

import de.obey.crownmc.util.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

@Getter @Setter
public class CNPC {

    private final YamlConfiguration cfg;
    private final String name, path;

    private Location location;
    private String prefix;
    private ItemStack helmet, chestPlate, leggings, boots, hand;

    private boolean showName, showArms, visible, small;
    private EulerAngle leftArm, rightArm;

    private ArmorStand armorStand;

    public CNPC(final String name, final YamlConfiguration cfg) {
        this.cfg = cfg;
        this.name = name;
        this.path = "npc." + name +".";

        loadData();
    }

    public void loadData() {

        if(cfg.contains(path + "location"))
            location = LocationUtil.decode(cfg.getString(path + "location"));

        prefix = ChatColor.translateAlternateColorCodes('&', cfg.getString(path + "prefix", "Change me"));
        helmet = cfg.getItemStack(path + "helmet");
        chestPlate = cfg.getItemStack(path + "chestPlate");
        leggings = cfg.getItemStack(path + "leggings");
        boots = cfg.getItemStack(path + "boots");
        hand = cfg.getItemStack(path + "hand");

        showName = cfg.getBoolean(path + "showName", false);
        showArms = cfg.getBoolean(path + "showArms", false);
        visible = cfg.getBoolean(path + "visible", false);
        small = cfg.getBoolean(path + "small", false);

        leftArm = LocationUtil.decodeEuler(cfg.getString(path + "leftArm", "#0#0#0"));
        rightArm = LocationUtil.decodeEuler(cfg.getString(path + "rightArm", "#0#0#0"));

        if(location != null)
            spawnStand();
    }

    public void spawnStand() {
        armorStand = location.getWorld().spawn(location, ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setCustomName("§0§0§1" + prefix);
        armorStand.setCustomNameVisible(showName);
        armorStand.setVisible(visible);
        armorStand.setArms(showArms);
        armorStand.setSmall(small);
        armorStand.setLeftArmPose(leftArm);
        armorStand.setRightArmPose(rightArm);

        if(helmet != null)
            armorStand.setHelmet(helmet);

        if(chestPlate != null)
            armorStand.setChestplate(chestPlate);

        if(leggings != null)
            armorStand.setLeggings(leggings);

        if(boots != null)
            armorStand.setBoots(boots);

        if(hand != null)
            armorStand.setItemInHand(hand);
    }

    public void removeStand() {
        if(armorStand == null)
            return;

        armorStand.remove();
    }

    public void save() {
        cfg.set("npc." + name + ".prefix", prefix);
        cfg.set("npc." + name + ".location", LocationUtil.encode(location));
        cfg.set("npc." + name + ".showName", showName);
        cfg.set("npc." + name + ".showArms", showArms);
        cfg.set("npc." + name + ".visible", visible);
        cfg.set("npc." + name + ".small", small);
        cfg.set("npc." + name + ".helmet", helmet);
        cfg.set("npc." + name + ".chestPlate", chestPlate);
        cfg.set("npc." + name + ".leggings", leggings);
        cfg.set("npc." + name + ".boots", boots);
        cfg.set("npc." + name + ".hand", hand);

        cfg.set(path + "leftArm", LocationUtil.encodeEuler(leftArm));
        cfg.set(path + "rightArm", LocationUtil.encodeEuler(rightArm));
    }

}
