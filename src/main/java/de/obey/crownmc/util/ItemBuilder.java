package de.obey.crownmc.util;
/*

    Author - Obey -> SkySlayer-v4
       20.10.2022 / 03:55

*/

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ItemBuilder {

    private final ItemStack itemStack;
    private FireworkEffectMeta fireworkMeta;
    private SkullMeta skullMeta;
    private final ItemMeta meta;

    public ItemBuilder(final Material material) {
        itemStack = new ItemStack(material);
        meta = itemStack.getItemMeta();

        if (material == Material.FIREWORK_CHARGE)
            fireworkMeta = (FireworkEffectMeta) itemStack.getItemMeta();

        if (material == Material.SKULL_ITEM)
            skullMeta = (SkullMeta) itemStack.getItemMeta();
    }

    public ItemBuilder(final Material material, final int amount) {
        itemStack = new ItemStack(material, amount);
        meta = itemStack.getItemMeta();

        if (material == Material.FIREWORK_CHARGE)
            fireworkMeta = (FireworkEffectMeta) itemStack.getItemMeta();

        if (material == Material.SKULL_ITEM)
            skullMeta = (SkullMeta) itemStack.getItemMeta();
    }

    public ItemBuilder(final Material material, final int amount, final byte subid) {
        itemStack = new ItemStack(material, amount, subid);
        meta = itemStack.getItemMeta();

        if (material == Material.FIREWORK_CHARGE)
            fireworkMeta = (FireworkEffectMeta) itemStack.getItemMeta();

        if (material == Material.SKULL_ITEM)
            skullMeta = (SkullMeta) itemStack.getItemMeta();
    }

    public ItemBuilder setDisplayname(final String name) {
        meta.setDisplayName(name);

        if (fireworkMeta != null)
            fireworkMeta.setDisplayName(name);

        if (skullMeta != null)
            skullMeta.setDisplayName(name);

        return this;
    }

    public ItemBuilder setLore(final String... lore) {
        final List<String> list = new ArrayList<>();

        Collections.addAll(list, lore);

        meta.setLore(list);

        if (fireworkMeta != null)
            fireworkMeta.setLore(list);

        if (skullMeta != null)
            skullMeta.setLore(list);

        return this;
    }

    public ItemBuilder setLore(final List<String> list) {
        meta.setLore(list);

        if (fireworkMeta != null)
            fireworkMeta.setLore(list);

        if (skullMeta != null)
            skullMeta.setLore(list);

        return this;
    }

    public ItemBuilder addLore(final String... lore) {
        final List<String> list = meta.getLore();

        Collections.addAll(list, lore);

        meta.setLore(list);

        if (fireworkMeta != null)
            fireworkMeta.setLore(list);

        if (skullMeta != null)
            skullMeta.setLore(list);

        return this;
    }

    public ItemBuilder setFireWorkColor(final Color color) {
        final FireworkEffect effect = FireworkEffect.builder().withColor(color).build();

        fireworkMeta = (FireworkEffectMeta) meta;
        fireworkMeta.setEffect(effect);
        fireworkMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

        return this;
    }

    public ItemBuilder setSkullOwner(final String name) {

        if (skullMeta != null)
            skullMeta.setOwner(name);

        return this;
    }

    public ItemBuilder setTextur(final String textur, final UUID uuid) {

        if (skullMeta == null)
            return this;

        final String texturPrefix = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv";
        final GameProfile profile = new GameProfile(uuid, null);

        profile.getProperties().put("textures", new Property("textures", texturPrefix + textur));

        Field profileField = null;

        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException ignored) {
        }

        return this;
    }

    public ItemBuilder addEnchantment(final Enchantment enchantment, final int level) {
        meta.addEnchant(enchantment, level, true);

        if (fireworkMeta != null)
            fireworkMeta.addEnchant(enchantment, level, true);

        if (skullMeta != null)
            skullMeta.addEnchant(enchantment, level, true);

        return this;
    }

    public ItemBuilder addEnchantment(final Enchantment enchantment) {
        meta.addEnchant(enchantment, 1, true);

        if (fireworkMeta != null)
            fireworkMeta.addEnchant(enchantment, 1, true);

        if (skullMeta != null)
            skullMeta.addEnchant(enchantment, 1, true);

        return this;
    }

    public ItemBuilder setEnchantments(final Map<Enchantment, Integer> enchantments) {
        enchantments.keySet().forEach(enchantment -> {
            meta.addEnchant(enchantment, enchantments.get(enchantment), true);
        });

        return this;
    }

    public ItemBuilder addItemFlags(final ItemFlag... flags) {
        meta.addItemFlags(flags);

        if (fireworkMeta != null)
            meta.addItemFlags(flags);

        if (skullMeta != null)
            meta.addItemFlags(flags);

        return this;
    }

    public ItemStack build() {
        itemStack.setItemMeta(meta);

        if (fireworkMeta != null)
            itemStack.setItemMeta(fireworkMeta);

        if (skullMeta != null)
            itemStack.setItemMeta(skullMeta);

        return itemStack;
    }

}
