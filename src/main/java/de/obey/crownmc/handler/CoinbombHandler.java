package de.obey.crownmc.handler;

import de.obey.crownmc.objects.Coinbomb;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CoinbombHandler {

    Coinbomb coinbomb;
    MessageUtil messageUtil;


    public CoinbombHandler(MessageUtil messageUtil) {
        this.coinbomb = new Coinbomb();
        this.messageUtil = messageUtil;
    }

    public void openEditInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Coinbomb Edit");

        int i = 0;
        for (ItemStack itemStack : coinbomb.getItems()) inventory.setItem(i++, itemStack);

        player.openInventory(inventory);
    }

    public void closeEditInventory(Player player, InventoryView inventoryView) {
        List<ItemStack> newItems = Arrays.stream(inventoryView.getTopInventory().getContents()).collect(Collectors.toList());
        this.coinbomb.saveItems(newItems);
        player.closeInventory();
        this.messageUtil.sendMessage(player, "Du hast die Coinbombe editiert§8.");
    }

    public ItemStack giveCoinbomb() {
        return new ItemBuilder(Material.getMaterial(175)).setDisplayname("§6§lCoinbombe").setLore(
                "§r",
                "§6§lDrop",
                "§r §7Droppe das Item um die Bombe zu aktivieren§8.",
                "§r"
        ).build();
    }
}
