package net.craftersland.ctw.server.kits;

import net.craftersland.ctw.server.CTW;
import net.craftersland.ctw.server.game.GameEngine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class KitHandler {
    private final CTW ctw;
    private Inventory mainMenu;
    private final Map<Player, Inventory> kitMenu;
    private final Map<Player, Inventory> enchantMenu;

    public KitHandler(final CTW ctw) {
        this.mainMenu = null;
        this.kitMenu = new HashMap<Player, Inventory>();
        this.enchantMenu = new HashMap<Player, Inventory>();
        this.ctw = ctw;
    }

    private void createMainMenu(final Player p) {
        if (this.mainMenu == null) {
            final Inventory inv = Bukkit.createInventory(p, 9, this.ctw.getLanguageHandler().getMessage("MenuGUI.MenuTitles.MainMenu").replaceAll("&", "§"));
            this.mainMenu = inv;
        }
    }

    private ItemStack setKitMenuItem() {
        final ItemStack item = new ItemStack(Material.CHEST);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(this.ctw.getLanguageHandler().getMessage("MenuGUI.ItemNames.KitMenuItem").replaceAll("&", "§"));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack setTrashMenuItem() {
        final ItemStack item = new ItemStack(Material.FLOWER_POT_ITEM);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(this.ctw.getLanguageHandler().getMessage("MenuGUI.ItemNames.TrashMenuItem").replaceAll("&", "§"));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack setEnchantsMenuItem() {
        final ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(this.ctw.getLanguageHandler().getMessage("MenuGUI.ItemNames.EnchantsMenuItem").replaceAll("&", "§"));
        item.setItemMeta(meta);
        return item;
    }

    public void sendMainMenu(final Player p) {
        if (this.ctw.getTeamHandler().isSpectator(p) || this.ctw.getGameEngine().gameStage != GameEngine.GameStages.RUNNING) {
            this.ctw.getSoundHandler().sendFailedSound(p.getLocation(), p);
            p.sendMessage(this.ctw.getLanguageHandler().getMessage("ChatMessages.CantGetKit").replaceAll("&", "§"));
        } else {
            this.createMainMenu(p);
            this.mainMenu.clear();
            this.mainMenu.setItem(2, this.setKitMenuItem());
            this.mainMenu.setItem(4, this.setTrashMenuItem());
            this.mainMenu.setItem(6, this.setEnchantsMenuItem());
            p.openInventory(this.mainMenu);
            this.ctw.getSoundHandler().sendChestOpenSound(p.getLocation(), p);
        }
    }

    public void sendKitMenu(final Player p) {
        if (this.kitMenu.containsKey(p)) {
            final Inventory inv = this.kitMenu.get(p);
            inv.clear();
            this.populateKitsMenu(inv, p);
            p.openInventory(inv);
            this.ctw.getSoundHandler().sendChestOpenSound(p.getLocation(), p);
        } else {
            final Inventory inv = this.createKitsMenu(p);
            this.kitMenu.put(p, inv);
            this.populateKitsMenu(inv, p);
            p.openInventory(inv);
            this.ctw.getSoundHandler().sendChestOpenSound(p.getLocation(), p);
        }
    }

    public void sendEnchantMenu(final Player p) {
        if (this.enchantMenu.containsKey(p)) {
            final Inventory inv = this.enchantMenu.get(p);
            inv.clear();
            this.populateEnchantMenu(inv, p);
            p.openInventory(inv);
            this.ctw.getSoundHandler().sendChestOpenSound(p.getLocation(), p);
        } else {
            final Inventory inv = this.createEnchantsMenu(p);
            this.enchantMenu.put(p, inv);
            this.populateEnchantMenu(inv, p);
            p.openInventory(inv);
            this.ctw.getSoundHandler().sendChestOpenSound(p.getLocation(), p);
        }
    }

    public void sendTrashMenu(final Player p) {
        final Inventory inv = Bukkit.createInventory(p, 27, this.ctw.getLanguageHandler().getMessage("MenuGUI.MenuTitles.TrashMenu").replaceAll("&", "§"));
        p.openInventory(inv);
        this.ctw.getSoundHandler().sendChestOpenSound(p.getLocation(), p);
    }

    private Inventory createKitsMenu(final Player p) {
        final Double bal = CTW.economy.getBalance(p);
        final Inventory inv = Bukkit.createInventory(p, 45, this.ctw.getLanguageHandler().getMessage("MenuGUI.MenuTitles.KitsMenu").replaceAll("&", "§") + ChatColor.GRAY + " - " + ChatColor.YELLOW + bal.intValue() + " \u26c0");
        return inv;
    }

    private Inventory createEnchantsMenu(final Player p) {
        final Double bal = CTW.economy.getBalance(p);
        final Inventory inv = Bukkit.createInventory(p, 27, this.ctw.getLanguageHandler().getMessage("MenuGUI.MenuTitles.EnchantsMenu").replaceAll("&", "§") + ChatColor.GRAY + " - " + ChatColor.YELLOW + bal.intValue() + " \u26c0");
        return inv;
    }

    public void removeInventory(final Player p) {
        this.kitMenu.remove(p);
        this.enchantMenu.remove(p);
    }

    public String getMainMenuTitle() {
        return this.ctw.getLanguageHandler().getMessage("MenuGUI.MenuTitles.MainMenu").replaceAll("&", "§");
    }

    public String getTrashMenuTitle() {
        return this.ctw.getLanguageHandler().getMessage("MenuGUI.MenuTitles.TrashMenu").replaceAll("&", "§");
    }

    private void populateKitsMenu(final Inventory inv, final Player p) {
        this.ctw.getItemKitHandler().addItemsToMenu(inv, p);
    }

    private void populateEnchantMenu(final Inventory inv, final Player p) {
        inv.setItem(0, this.ctw.getEnchantSharpnessI().setKitItem(p));
        inv.setItem(1, this.ctw.getEnchantSharpnessII().setKitItem(p));
        inv.setItem(2, this.ctw.getEnchantSharpnessIII().setKitItem(p));
        inv.setItem(3, this.ctw.getEnchantSharpnessIV().setKitItem(p));
        inv.setItem(5, this.ctw.getEnchantPowerI().setKitItem(p));
        inv.setItem(6, this.ctw.getEnchantPowerII().setKitItem(p));
        inv.setItem(7, this.ctw.getEnchantPowerIII().setKitItem(p));
        inv.setItem(8, this.ctw.getEnchantPowerIV().setKitItem(p));
        inv.setItem(9, this.ctw.getEnchantKnockbackI().setKitItem(p));
        inv.setItem(10, this.ctw.getEnchantKnockbackII().setKitItem(p));
        inv.setItem(11, this.ctw.getEnchantFireAspectI().setKitItem(p));
        inv.setItem(12, this.ctw.getEnchantFireAspectII().setKitItem(p));
        inv.setItem(14, this.ctw.getEnchantPunchI().setKitItem(p));
        inv.setItem(15, this.ctw.getEnchantPunchII().setKitItem(p));
        inv.setItem(16, this.ctw.getEnchantFlameI().setKitItem(p));
        inv.setItem(17, this.ctw.getEnchantInfinityI().setKitItem(p));
        inv.setItem(18, this.ctw.getEnchantProtectionI().setKitItem(p));
        inv.setItem(19, this.ctw.getEnchantProtectionII().setKitItem(p));
        inv.setItem(20, this.ctw.getEnchantProtectionIII().setKitItem(p));
        inv.setItem(21, this.ctw.getEnchantProtectionIV().setKitItem(p));
        inv.setItem(23, this.ctw.getEnchantThornsI().setKitItem(p));
        inv.setItem(24, this.ctw.getEnchantThornsII().setKitItem(p));
        inv.setItem(25, this.ctw.getEnchantFeatherFallingI().setKitItem(p));
        inv.setItem(26, this.ctw.getEnchantFeatherFallingII().setKitItem(p));
    }

    public Inventory getKitsInventory(final Player p) {
        final Inventory inv = this.kitMenu.get(p);
        return inv;
    }

    public Inventory getEnchantsInventory(final Player p) {
        final Inventory inv = this.enchantMenu.get(p);
        return inv;
    }
}
