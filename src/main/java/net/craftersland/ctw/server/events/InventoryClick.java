package net.craftersland.ctw.server.events;

import net.craftersland.ctw.server.CTW;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryClick implements Listener {
    private final CTW ctw;

    public InventoryClick(final CTW ctw) {
        this.ctw = ctw;
    }

    @EventHandler
    public void onFoodChange(@NotNull FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelRemoveArmor(InventoryClickEvent e) {
        /*
        if (e.getSlotType() == InventoryType.SlotType.ARMOR) {
            e.setCancelled(true);
        }
         */
    }

    @EventHandler
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        final Player p = (Player) event.getWhoClicked();
        if (event.getInventory() != null) {
            if (event.getCursor().getType() != null) {
                if (event.getInventory().getTitle().matches(this.ctw.getKitHandler().getMainMenuTitle()) && p.getGameMode() == GameMode.SURVIVAL) {
                    if (event.isShiftClick()) {
                        event.setCancelled(true);
                    }
                    if (event.getSlot() == 2) {
                        event.setCancelled(true);
                        p.closeInventory();
                        this.ctw.getKitHandler().sendKitMenu(p);
                    } else if (event.getSlot() == 4) {
                        event.setCancelled(true);
                        p.closeInventory();
                        this.ctw.getKitHandler().sendTrashMenu(p);
                    } else if (event.getSlot() == 6) {
                        event.setCancelled(true);
                        p.closeInventory();
                        this.ctw.getKitHandler().sendEnchantMenu(p);
                    } else if (event.getSlot() < 9 && event.getSlot() != 2 && event.getSlot() != 4 && event.getSlot() != 6) {
                        event.setCancelled(true);
                    } else if (event.getCursor() != null && event.getCursor().getType() != Material.AIR && event.getSlot() < 27) {
                        event.setCancelled(true);
                    }
                } else if (event.getInventory().equals(this.ctw.getKitHandler().getKitsInventory(p)) && p.getGameMode() == GameMode.SURVIVAL) {
                    if (event.isShiftClick()) {
                        event.setCancelled(true);
                    }
                    if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
                    event.setCancelled(true);
                    this.ctw.getItemKitHandler().sendKit(p, event.getSlot());
                } else if (event.getInventory().equals(this.ctw.getKitHandler().getEnchantsInventory(p)) && p.getGameMode() == GameMode.SURVIVAL) {
                    if (event.isShiftClick()) {
                        event.setCancelled(true);
                    }

                    if (event.getCursor() != null && event.getCursor().getType() != Material.AIR && event.getSlot() < 27) {
                        event.setCancelled(true);
                    }

                    if (event.getSlot() == 0) {
                        event.setCancelled(true);
                        this.ctw.getEnchantSharpnessI().giveKit(p);
                    } else if (event.getSlot() == 1) {
                        event.setCancelled(true);
                        this.ctw.getEnchantSharpnessII().giveKit(p);
                    } else if (event.getSlot() == 7) {
                        event.setCancelled(true);
                        this.ctw.getEnchantPowerI().giveKit(p);
                    } else if (event.getSlot() == 8) {
                        event.setCancelled(true);
                        this.ctw.getEnchantPowerII().giveKit(p);
                    } else if (event.getSlot() == 9) {
                        event.setCancelled(true);
                        this.ctw.getEnchantKnockbackI().giveKit(p);
                    } else if (event.getSlot() == 10) {
                        event.setCancelled(true);
                        this.ctw.getEnchantFireAspectI().giveKit(p);
                    } else if (event.getSlot() == 16) {
                        event.setCancelled(true);
                        this.ctw.getEnchantPunchI().giveKit(p);
                    } else if (event.getSlot() == 17) {
                        event.setCancelled(true);
                        this.ctw.getEnchantInfinityI().giveKit(p);
                    } else if (event.getSlot() == 18) {
                        event.setCancelled(true);
                        this.ctw.getEnchantProtectionI().giveKit(p);
                    } else if (event.getSlot() == 19) {
                        event.setCancelled(true);
                        this.ctw.getEnchantProtectionII().giveKit(p);
                    } else if (event.getSlot() == 25) {
                        event.setCancelled(true);
                        this.ctw.getEnchantFeatherFallingI().giveKit(p);
                    } else if (event.getSlot() == 26) {
                        event.setCancelled(true);
                        this.ctw.getEnchantFeatherFallingII().giveKit(p);
                    } else if (event.getSlot() < 27) {
                        event.setCancelled(true);
                    }

                } else if (event.getInventory().getTitle().matches(this.ctw.getJoinMenu().joinMenuTitle())) {
                    if (event.isShiftClick()) {
                        event.setCancelled(true);
                    }

                    if (event.getSlot() == 4) {
                        this.closeInv(p);
                        this.ctw.getPlayerHandler().autoAddTeam(p);
                    } else if (event.getSlot() == 2) {
                        if (p.hasPermission("CTW.pickupteam")) {

                            this.closeInv(p);
                            this.ctw.getPlayerHandler().addRedTeam(p);

                        } else {
                            this.ctw.getSoundHandler().sendFailedSound(p.getLocation(), p);
                            p.sendMessage(this.ctw.getLanguageHandler().getMessage("ChatMessages.SelectTeamNoPermission").replaceAll("&", "§"));
                        }
                    } else if (event.getSlot() == 6) {
                        if (p.hasPermission("CTW.pickupteam")) {

                            this.closeInv(p);
                            this.ctw.getPlayerHandler().addBlueTeam(p);

                        } else {
                            this.ctw.getSoundHandler().sendFailedSound(p.getLocation(), p);
                            p.sendMessage(this.ctw.getLanguageHandler().getMessage("ChatMessages.SelectTeamNoPermission").replaceAll("&", "§"));
                        }
                    }

                } else if ((event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.WOOL && p.getGameMode() == GameMode.SURVIVAL) || (event.getCursor().getType() == Material.WOOL && p.getGameMode() == GameMode.SURVIVAL)) {
                    final ItemStack item = event.getCurrentItem();
                    final ItemStack cursor = event.getCursor();
                    if (!event.getViewers().isEmpty()) {
                        if (item.getData().getData() == 14) {
                            this.ctw.getTakenWools().redWoolTakenCheck(p);
                        } else if (item.getData().getData() == 6) {
                            this.ctw.getTakenWools().pinkWoolTakenCheck(p);
                        } else if (item.getData().getData() == 11) {
                            this.ctw.getTakenWools().blueWoolTakenCheck(p);
                        } else if (item.getData().getData() == 9) {
                            this.ctw.getTakenWools().cyanWoolTakenCheck(p);
                        } else if (cursor.getData().getData() == 14) {
                            this.ctw.getTakenWools().redWoolTakenCheck(p);
                        } else if (cursor.getData().getData() == 6) {
                            this.ctw.getTakenWools().pinkWoolTakenCheck(p);
                        } else if (cursor.getData().getData() == 11) {
                            this.ctw.getTakenWools().blueWoolTakenCheck(p);
                        } else if (cursor.getData().getData() == 9) {
                            this.ctw.getTakenWools().cyanWoolTakenCheck(p);
                        }
                    }
                }
            }
        }
    }

    private void closeInv(final Player p) {
        Bukkit.getScheduler().runTask(this.ctw, p::closeInventory);
    }
}
