package net.craftersland.ctw.server.utils;

import net.craftersland.ctw.server.CTW;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

public class StartupKit {
    private final CTW ctw;

    public StartupKit(final CTW ctw) {
        this.ctw = ctw;
    }

    public void saveStartupKit(final @NotNull Player p) {
        for (int i = 0; i < p.getInventory().getSize(); ++i) {
            final ItemStack item = p.getInventory().getItem(i);
            this.ctw.getMapConfigHandler().saveStartupKit(item, i);
        }
    }

    public void giveStartupKit(final @NotNull Player player) {
        player.setHealth(player.getMaxHealth());
        player.getInventory().setContents(this.ctw.getMapConfigHandler().startupKit);
        setUnbreakableArmor(player);
    }

    public void setRedSuit(final @NotNull Player p) {
        final ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        final LeatherArmorMeta lamHelmet = (LeatherArmorMeta) helmet.getItemMeta();
        lamHelmet.setColor(Color.fromRGB(255, 85, 85));
        helmet.setItemMeta(lamHelmet);
        final ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        final LeatherArmorMeta lamChestplate = (LeatherArmorMeta) chestplate.getItemMeta();
        lamChestplate.setColor(Color.fromRGB(255, 85, 85));
        chestplate.setItemMeta(lamChestplate);
        final ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        final LeatherArmorMeta lamLeggings = (LeatherArmorMeta) leggings.getItemMeta();
        lamLeggings.setColor(Color.fromRGB(255, 85, 85));
        leggings.setItemMeta(lamLeggings);
        final ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        final LeatherArmorMeta lamBoots = (LeatherArmorMeta) boots.getItemMeta();
        lamBoots.setColor(Color.fromRGB(255, 85, 85));
        boots.setItemMeta(lamBoots);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    public void setBlueSuit(final Player p) {
        final ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        final LeatherArmorMeta lamHelmet = (LeatherArmorMeta) helmet.getItemMeta();
        lamHelmet.setColor(Color.fromRGB(85, 85, 255));
        helmet.setItemMeta(lamHelmet);
        final ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        final LeatherArmorMeta lamChestplate = (LeatherArmorMeta) chestplate.getItemMeta();
        lamChestplate.setColor(Color.fromRGB(85, 85, 255));
        chestplate.setItemMeta(lamChestplate);
        final ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        final LeatherArmorMeta lamLeggings = (LeatherArmorMeta) leggings.getItemMeta();
        lamLeggings.setColor(Color.fromRGB(85, 85, 255));
        leggings.setItemMeta(lamLeggings);
        final ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        final LeatherArmorMeta lamBoots = (LeatherArmorMeta) boots.getItemMeta();
        lamBoots.setColor(Color.fromRGB(85, 85, 255));
        boots.setItemMeta(lamBoots);
        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    public static void setUnbreakableArmor(@NotNull Player player) {
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item == null) continue;
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;
            meta.spigot().setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            item.setItemMeta(meta);
        }

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getType().name().contains("SWORD")
                    || item.getType().name().contains("AXE")
                    || item.getType().name().contains("PICKAXE")
                    || item.getType().name().contains("SHOVEL")
                    || item.getType().name().contains("HOE")) {
                ItemMeta meta = item.getItemMeta();
                if (meta == null) continue;
                meta.spigot().setUnbreakable(true);
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                item.setItemMeta(meta);
            }
        }
    }
}
