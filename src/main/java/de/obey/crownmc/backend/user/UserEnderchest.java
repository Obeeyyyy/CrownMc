package de.obey.crownmc.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       02.11.2022 / 20:21

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public final class UserEnderchest {

    @Getter(AccessLevel.NONE)
    private final MessageUtil messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();

    @Getter(AccessLevel.NONE)
    private final User user;

    private final YamlConfiguration cfg;

    private int sites = 1;
    private final HashMap<String, Integer> enderchestInformation = new HashMap<>();

    /*

        enderchest:
            sites: 1
            1:
                rows: 1
                locked:
                    all: 0
                    trusted: 0

        "1.rows" -> 1
        "1.locked.all" -> 0
        "1.locked.trusted" -> 0

     */


    private final ArrayList<Inventory> enderchestSites = new ArrayList<>();
    private ArrayList<String> enderchestTrusted = new ArrayList<>();

    public UserEnderchest(final User user) {
        this.user = user;
        cfg = user.getCfg();
        loadEnderchest();
    }

    public void checkIfUsernameChanged(final Player player) {
        if (enderchestSites.isEmpty())
            return;

        final String knownName = enderchestSites.get(0).getTitle().split(" ")[1];

        if (player.getName().equalsIgnoreCase(knownName))
            return;

        save();
        loadEnderchest();
    }

    private void loadEnderchest() {

        // Setting default values
        if (!cfg.contains("enderchest")) {
            cfg.set("enderchest.sites", 1);
            cfg.set("enderchest.trusted", new ArrayList<>());
            cfg.set("enderchest.1.rows", 1);
            cfg.set("enderchest.1.locked.all", 0);
            cfg.set("enderchest.1.locked.trusted", 0);
            cfg.set("enderchest.1.items", new ArrayList<>());
        }

        sites = cfg.getInt("enderchest.sites");

        if (sites == 0)
            sites = 1;

        for (int i = 1; i <= sites; i++) {
            enderchestInformation.put(i + ".rows", cfg.getInt("enderchest." + i + ".rows"));
            enderchestInformation.put(i + ".locked.all", cfg.getInt("enderchest." + i + ".locked.all"));
            enderchestInformation.put(i + ".locked.trusted", cfg.getInt("enderchest." + i + ".locked.trusted"));
        }

        enderchestTrusted = (ArrayList<String>) cfg.getStringList("enderchest.trusted");

        enderchestSites.clear();

        for (int i = 1; i <= sites; i++) {
            final Inventory inventory = Bukkit.createInventory(null, 9 * 6, "§5§lEC§7 " + user.getOfflinePlayer().getName() + " " + i);
            enderchestSites.add(inventory);

            int finalSite = i;
            CrownMain.getInstance().getInitializer().getExecutorService().submit(() -> {
                final ArrayList<ItemStack> contents = cfg.contains("enderchest." + finalSite + ".items") ? (ArrayList<ItemStack>) cfg.getList("enderchest." + finalSite + ".items") : new ArrayList<>();

                final AtomicInteger slot = new AtomicInteger();

                if (contents.size() > 0) {
                    contents.forEach(item -> {
                        if (item == null) {
                            inventory.setItem(slot.get(), new ItemStack(Material.AIR));
                        } else {
                            inventory.setItem(slot.get(), item);
                        }

                        slot.getAndAdd(1);
                    });
                }

                setLastEcRow(inventory, finalSite);
            });
        }
    }

    public void openEnderchestSite(final Player player, final int site) {
        Inventory inventory = enderchestSites.get(site - 1);

        if (!player.getUniqueId().toString().equalsIgnoreCase(user.getOfflinePlayer().getUniqueId().toString()) && !PermissionUtil.hasPermission(player, "*", false)) {

            if (enderchestTrusted.contains(player.getUniqueId().toString())) {
                final boolean trustedLocked = enderchestInformation.get(site + ".locked.trusted") != 0;

                if (trustedLocked) {
                    inventory = Bukkit.createInventory(null, 9 * 7, "§5§lEC§7 " + user.getOfflinePlayer().getName() + " " + site);
                    InventoryUtil.fillFromTo(inventory, new ItemBuilder(Material.BARRIER, 1).setDisplayname("§c§odiese Seite ist privat").build(), 0, 44);
                    setLastEcRow(inventory, site);
                }

            } else {
                final boolean allLocked = enderchestInformation.get(site + ".locked.all") != 0;

                if (allLocked) {
                    inventory = Bukkit.createInventory(null, 9 * 7, "§5§lEC§7 " + user.getOfflinePlayer().getName() + " " + site);
                    InventoryUtil.fillFromTo(inventory, new ItemBuilder(Material.BARRIER, 1).setDisplayname("§c§odiese Seite ist privat").build(), 0, 44);
                    setLastEcRow(inventory, site);
                }
            }
        }

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);
    }

    public void setLastEcRow(final Inventory inventory, final long site) {
        final int rows = enderchestInformation.get(site + ".rows");

        final boolean trustedLocked = enderchestInformation.get(site + ".locked.trusted") != 0;
        final boolean allLocked = enderchestInformation.get(site + ".locked.all") != 0;

        InventoryUtil.fillFromTo(inventory, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 10).setDisplayname("§d§onicht freigeschaltet").build(), 9 * rows, 53);
        InventoryUtil.fillFromTo(inventory, new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build(), 45, 53);

        String textur = "YzQ1ODY0NzYxNzZhMjU2NDczODE4YTE4NDkxNTdlODQ0YzU2Mzg3MjRiY2Y4MzA5ZjBkOWRmMjdjYTk1OTE2MCJ9fX0=";

        if (allLocked && !trustedLocked)
            textur = "YzM4NmI0NDhjYmU5ZjFkNDE2ZGM1N2Y5YTMwODg1MDg5ODE1YzFmNjcxOGE2NzI1YjkyMTU0MzBjMjIzZWE5ZiJ9fX0=";

        if (!allLocked && trustedLocked)
            textur = "YzkzM2VlMjM2NTg4OGZkOTZhZDg4MzNlMTNjMjI0NzVmMGVmNjEzYWRkNzU0MWI3MmMyYWMzYzVmZTQyOGUyMSJ9fX0=";

        if (allLocked && trustedLocked)
            textur = "Mzk5MzY2YTNmMjMzNTZkNDRjYjNhNzIyZjgxODdjN2QwN2JhOTc1MDFmNzZkMTVmMmIzMTFlN2ZmZTVhNGRhYyJ9fX0=";

        inventory.setItem(48, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur(textur, UUID.randomUUID())
                .setDisplayname("§8» §5§lSichtbarkeit §8× §7Seite§d " + site)
                .setLore("", "§8┃> §7Öffentlich§8: " + (allLocked ? "§c§onicht sichtbar" : "§a§osichtbar"),
                        "§8┃> §7Freunde§8: " + (trustedLocked ? "§c§onicht sichtbar" : "§a§osichtbar"),
                        "",
                        "§8┃>§7 Linksklick §8× §7Sichtbarkeit für Spieler umschalten",
                        "§8┃>§7 Rechtsklick §8× §7Sichtbarkeit für Freunde umschalten")
                .build());

        inventory.setItem(49, new ItemBuilder(Material.EYE_OF_ENDER)
                .setDisplayname("§8» §5§lEnderchest §8× §7Seite§d " + site)
                .setLore("", "§8┃> §7Du hast §d" + sites + "§7 " + (sites > 1 ? "Seiten" : "Seite") + "§8.",
                        "§8┃> §7Diese Seite hat §d" + rows + "§8/§55§7 Zeilen§8.")
                .build());

        if (site < sites) {
            inventory.setItem(52, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setDisplayname("§8» §7Nächste Seite")
                    .setLore("", "§8┃> §7Klicke hier um die nächste Seite zu öffnen.")
                    .setTextur("MTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19", UUID.randomUUID())
                    .build());
        }

        if (site > 1) {
            inventory.setItem(46, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                    .setDisplayname("§8« §7Vorherige Seite")
                    .setLore("", "§8┃> §7Klicke hier um die vorherige Seite zu öffnen.")
                    .setTextur("YmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==", UUID.randomUUID())
                    .build());
        }
    }

    public void addEnderchestSite() {
        if (sites >= 10) {
            if (user.getPlayer() != null) {
                user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.EXPLODE, 0.5f, 1);

                messageUtil.sendMessage(user.getPlayer(), "Du hast das Limit von §510 Seiten §7bereits erreicht.");
            }
            return;
        }

        sites++;

        if (user.getPlayer() != null) {
            user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.LEVEL_UP, 0.5f, 1);

            messageUtil.sendMessage(user.getPlayer(), "Du hast eine extra Enderchestseite erhalten.");
            InventoryUtil.removeItemInHand(user.getPlayer(), 1);
        }

        enderchestInformation.put(sites + ".rows", 1);
        enderchestInformation.put(sites + ".locked.all", 0);
        enderchestInformation.put(sites + ".locked.trusted", 0);

        enderchestSites.add(Bukkit.createInventory(null, 9 * 6, "§5§lEC§7 " + user.getOfflinePlayer().getName() + " " + sites));

        enderchestSites.forEach(inv -> {
            setLastEcRow(inv, Long.parseLong(inv.getTitle().split(" ")[2]));
        });
    }

    public void addEnderchestZeile() {

        int addingToSite = 1;

        for (int i = 1; i <= sites; i++) {
            if (enderchestInformation.get(i + ".rows") < 5) {
                addingToSite = i;
                break;
            }
        }

        final int seitenRows = enderchestInformation.get(addingToSite + ".rows");

        if (seitenRows >= 5) {
            if (user.getPlayer() != null) {
                user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.EXPLODE, 0.5f, 1);

                messageUtil.sendMessage(user.getPlayer(), "Du hast das Limit von §55 Zeilen §7bereits erreicht§8.§7 Schalte eine neue Seite frei um weitere Zeilen einlösen zu können§8.");
            }
            return;
        }

        if (user.getPlayer() != null) {
            user.getPlayer().playSound(user.getPlayer().getLocation(), Sound.LEVEL_UP, 0.5f, 1);

            messageUtil.sendMessage(user.getPlayer(), "Du hast eine extra Enderchestzeile erhalten.");
            InventoryUtil.removeItemInHand(user.getPlayer(), 1);
        }

        enderchestInformation.put(addingToSite + ".rows", seitenRows + 1);
        InventoryUtil.fillFromTo(enderchestSites.get(addingToSite - 1), new ItemStack(Material.AIR), seitenRows * 9, 44);
        setLastEcRow(enderchestSites.get(addingToSite - 1), addingToSite);

    }

    public void save() {
        cfg.set("enderchest.sites", sites);

        enderchestInformation.keySet().forEach(key -> {
            cfg.set("enderchest." + key, enderchestInformation.get(key));
        });

        if (enderchestSites.size() > 0) {
            for (final Inventory siteInventory : enderchestSites) {
                final int site = Integer.parseInt(siteInventory.getTitle().split(" ")[2]);
                final ArrayList<ItemStack> items = new ArrayList<>();

                for (int i = 0; i < siteInventory.getSize(); i++) {
                    if (i < enderchestInformation.get(site + ".rows") * 9)
                        items.add(siteInventory.getItem(i));
                }

                cfg.set("enderchest." + site + ".items", items);
            }
        }

        cfg.set("enderchest.trusted", enderchestTrusted);
    }

}
