package net.craftersland.ctw.server.events.protection;

import net.craftersland.ctw.server.CTW;
import net.craftersland.ctw.server.database.CTWPlayer;
import net.craftersland.ctw.server.game.GameEngine;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockPlace implements Listener {
    private final CTW ctw;

    public BlockPlace(final CTW ctw) {
        this.ctw = ctw;
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (this.ctw.getGameEngine().gameStage == GameEngine.GameStages.RUNNING) {
            final Location l = event.getBlock().getLocation();
            if (event.getBlock().getLocation().equals(this.ctw.getMapConfigHandler().redWool)) {
                final Player p = event.getPlayer();
                if (!event.getBlock().getState().getData().toItemStack().isSimilar(new ItemStack(Material.WOOL, 1, (short) 14))) {
                    event.setCancelled(true);
                } else {

                    if (p.isOp() || p.hasPermission("*")) {
                        this.redWoolPlaced(p);
                        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4! &c" + p.getName() + " &7ha colocado la lana &cRoja")));

                    } else if ((ctw.getTeamHandler().countBlueTeam() + ctw.getTeamHandler().countRedTeam()) > 3) {
                        this.redWoolPlaced(p);
                        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4! &c" + p.getName() + " &7ha colocado la lana &cRoja")));
                        Bukkit.getScheduler().scheduleSyncDelayedTask(ctw, () -> Bukkit.dispatchCommand(ctw.getServer().getConsoleSender(), "mysterydust add " + p.getName() + " " + 4));
                    } else {
                        p.sendMessage(ChatColor.RED + "No puedes colocar la lana porque no hay los jugadores suficientes.");
                        event.setCancelled(true);
                    }
                }
            } else if (event.getBlock().getLocation().equals(this.ctw.getMapConfigHandler().pinkWool)) {
                final Player p = event.getPlayer();
                if (!event.getBlock().getState().getData().toItemStack().isSimilar(new ItemStack(Material.WOOL, 1, (short) 6))) {
                    event.setCancelled(true);
                } else {
                    if (p.isOp() || p.hasPermission("*")) {
                        this.pinkWoolPlaced(p);

                        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4! &c" + p.getName() + " &7ha colocado la lana &dRosa")));
                    } else if ((ctw.getTeamHandler().countBlueTeam() + ctw.getTeamHandler().countRedTeam()) > 3) {
                        this.pinkWoolPlaced(p);

                        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4! &c" + p.getName() + " &7ha colocado la lana &dRosa")));

                        Bukkit.getScheduler().scheduleSyncDelayedTask(ctw, () -> Bukkit.dispatchCommand(ctw.getServer().getConsoleSender(), "mysterydust add " + p.getName() + " " + 4));
                    } else {
                        p.sendMessage(ChatColor.RED + "No puedes colocar la lana porque no hay los jugadores suficientes.");
                        event.setCancelled(true);
                    }
                }
            } else if (event.getBlock().getLocation().equals(this.ctw.getMapConfigHandler().blueWool)) {
                final Player p = event.getPlayer();
                if (!event.getBlock().getState().getData().toItemStack().isSimilar(new ItemStack(Material.WOOL, 1, (short) 11))) {
                    event.setCancelled(true);
                } else {
                    if (p.isOp() || p.hasPermission("*")) {
                        this.blueWoolPlaced(p);
                        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4! &9" + p.getName() + " &7ha colocado la lana &9Azul")));
                    } else if ((ctw.getTeamHandler().countBlueTeam() + ctw.getTeamHandler().countRedTeam()) > 3) {
                        this.blueWoolPlaced(p);
                        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4! &9" + p.getName() + " &7ha colocado la lana &9Azul")));

                        Bukkit.getScheduler().scheduleSyncDelayedTask(ctw, () -> Bukkit.dispatchCommand(ctw.getServer().getConsoleSender(), "mysterydust add " + p.getName() + " " + 4));
                    } else {
                        p.sendMessage(ChatColor.RED + "No puedes colocar la lana porque no hay los jugadores suficientes.");
                        event.setCancelled(true);
                    }
                }
            } else if (event.getBlock().getLocation().equals(this.ctw.getMapConfigHandler().cyanWool)) {
                final Player p = event.getPlayer();
                if (!event.getBlock().getState().getData().toItemStack().isSimilar(new ItemStack(Material.WOOL, 1, (short) 9))) {
                    event.setCancelled(true);
                } else {
                    if (p.isOp() || p.hasPermission("*")) {
                        this.cyanWoolPlaced(p);
                        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4! &9" + p.getName() + " &7ha colocado la lana &3Cyan")));
                    } else if ((ctw.getTeamHandler().countBlueTeam() + ctw.getTeamHandler().countRedTeam()) > 3) {
                        this.cyanWoolPlaced(p);

                        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4! &9" + p.getName() + " &7ha colocado la lana &3Cyan")));
                        Bukkit.getScheduler().scheduleSyncDelayedTask(ctw, () -> Bukkit.dispatchCommand(ctw.getServer().getConsoleSender(), "mysterydust add " + p.getName() + " " + 4));
                    } else {
                        p.sendMessage(ChatColor.RED + "No puedes colocar la lana porque no hay los jugadores suficientes.");
                        event.setCancelled(true);
                    }
                }
            } else if (this.ctw.getProtectionHandler().isAreaProtected(l)) {
                if (event.getPlayer() != null) {
                    if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        } else {
            event.setCancelled(true);
        }
    }

    private void redWoolPlaced(final Player p) {
        CTWPlayer ctwPlayer = ctw.getCTWPlayerRepository().get(p.getUniqueId());
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.ctw, new Runnable() {
            @Override
            public void run() {
                if (p.getWorld().getBlockAt(BlockPlace.this.ctw.getMapConfigHandler().redWool).getType() == Material.WOOL) {
                    BlockPlace.this.ctw.getWoolHandler().setRedPlaced(p);
                    BlockPlace.this.ctw.getFireworks().spawnRedFirework(BlockPlace.this.ctw.getMapConfigHandler().redSpawn);
                    ctwPlayer.setWoolPlacements(ctwPlayer.getWoolPlacements() + 1);
                    if (BlockPlace.this.ctw.getWoolHandler().isPinkPlaced()) {
                        if (p == BlockPlace.this.ctw.getWoolHandler().getWhoPlacedPinkWool()) {
                            final String title = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.RedDouble.title").replace("%PlayerName%", p.getName());
                            final String subtitle = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.RedDouble.subtitle").replace("%PlayerName%", p.getName());
                            BlockPlace.this.ctw.getMessageUtils().broadcastTitleMessage(title.replaceAll("&", "§"), subtitle.replaceAll("&", "§"));
                            BlockPlace.this.ctw.getSoundHandler().sendCompleteSound(p.getLocation(), p);
                            ctwPlayer.setScore(ctwPlayer.getScore() + BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.doubleBonus"));
                            //BlockPlace.this.ctw.getPlayerScoreHandler().addScore(p, BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.doubleBonus"));
                            BlockPlace.this.ctw.getEconomyHandler().addCoins(p, (double) BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.doubleBonus"));
                            BlockPlace.this.ctw.getMessageUtils().sendScoreMessage(p, "+" + BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.doubleBonus"), BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.doubleBonus"));
                        }
                    } else {
                        final String title = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.RedWool.title").replace("%PlayerName%", p.getName());
                        final String subtitle = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.RedWool.subtitle").replace("%PlayerName%", p.getName());
                        BlockPlace.this.ctw.getMessageUtils().broadcastTitleMessage(title.replaceAll("&", "§"), subtitle.replaceAll("&", "§"));
                        BlockPlace.this.ctw.getSoundHandler().sendCompleteSound(p.getLocation(), p);
                        ctwPlayer.setScore(ctwPlayer.getScore() + BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.placeWool"));
                        //BlockPlace.this.ctw.getPlayerScoreHandler().addScore(p, BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.placeWool"));
                        BlockPlace.this.ctw.getEconomyHandler().addCoins(p, (double) BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.placeWool"));
                        BlockPlace.this.ctw.getMessageUtils().sendScoreMessage(p, "+" + BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.placeWool"), BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.placeWool"));
                    }
                    final Location l = new Location(BlockPlace.this.ctw.getMapConfigHandler().redWool.getWorld(), BlockPlace.this.ctw.getMapConfigHandler().redWool.getX() + 0.5, BlockPlace.this.ctw.getMapConfigHandler().redWool.getY(), BlockPlace.this.ctw.getMapConfigHandler().redWool.getZ() + 0.5, 0.0f, -90.0f);
                    BlockPlace.this.ctw.getEffectUtils().woolPlacedEffect(l);
                    BlockPlace.this.ctw.getWoolAchievementHandler().checkForAchievements(p);
                }
            }
        }, 1L);
    }

    private void pinkWoolPlaced(final Player p) {
        CTWPlayer ctwPlayer = ctw.getCTWPlayerRepository().get(p.getUniqueId());
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.ctw, new Runnable() {
            @Override
            public void run() {
                if (p.getWorld().getBlockAt(BlockPlace.this.ctw.getMapConfigHandler().pinkWool).getType() == Material.WOOL) {
                    BlockPlace.this.ctw.getWoolHandler().setPinkPlaced(p);
                    BlockPlace.this.ctw.getFireworks().spawnRedFirework(BlockPlace.this.ctw.getMapConfigHandler().redSpawn);
                    ctwPlayer.setWoolPlacements(ctwPlayer.getWoolPlacements() + 1);
                    if (BlockPlace.this.ctw.getWoolHandler().isRedPlaced()) {
                        if (p == BlockPlace.this.ctw.getWoolHandler().getWhoPlacedRedWool()) {
                            final String title = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.RedDouble.title").replace("%PlayerName%", p.getName());
                            final String subtitle = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.RedDouble.subtitle").replace("%PlayerName%", p.getName());
                            BlockPlace.this.ctw.getMessageUtils().broadcastTitleMessage(title.replaceAll("&", "§"), subtitle.replaceAll("&", "§"));
                            BlockPlace.this.ctw.getSoundHandler().sendCompleteSound(p.getLocation(), p);
                            //BlockPlace.this.ctw.getPlayerScoreHandler().addScore(p, BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.doubleBonus"));
                            BlockPlace.this.ctw.getEconomyHandler().addCoins(p, (double) BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.doubleBonus"));
                            BlockPlace.this.ctw.getMessageUtils().sendScoreMessage(p, "+" + BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.doubleBonus"), BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.doubleBonus"));
                        }
                    } else {
                        final String title = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.PinkWool.title").replace("%PlayerName%", p.getName());
                        final String subtitle = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.PinkWool.subtitle").replace("%PlayerName%", p.getName());
                        BlockPlace.this.ctw.getMessageUtils().broadcastTitleMessage(title.replaceAll("&", "§"), subtitle.replaceAll("&", "§"));
                        BlockPlace.this.ctw.getSoundHandler().sendCompleteSound(p.getLocation(), p);
                        ctwPlayer.setScore(ctwPlayer.getScore() + BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.placeWool"));
                        //BlockPlace.this.ctw.getPlayerScoreHandler().addScore(p, BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.placeWool"));
                        BlockPlace.this.ctw.getEconomyHandler().addCoins(p, (double) BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.placeWool"));
                        BlockPlace.this.ctw.getMessageUtils().sendScoreMessage(p, "+" + BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.placeWool"), BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.placeWool"));
                    }
                    final Location l = new Location(BlockPlace.this.ctw.getMapConfigHandler().pinkWool.getWorld(), BlockPlace.this.ctw.getMapConfigHandler().pinkWool.getX() + 0.5, BlockPlace.this.ctw.getMapConfigHandler().pinkWool.getY(), BlockPlace.this.ctw.getMapConfigHandler().pinkWool.getZ() + 0.5, 0.0f, -90.0f);
                    BlockPlace.this.ctw.getEffectUtils().woolPlacedEffect(l);
                    BlockPlace.this.ctw.getWoolAchievementHandler().checkForAchievements(p);
                }
            }
        }, 1L);
    }

    private void blueWoolPlaced(final Player p) {
        CTWPlayer ctwPlayer = ctw.getCTWPlayerRepository().get(p.getUniqueId());
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.ctw, new Runnable() {
            @Override
            public void run() {
                if (p.getWorld().getBlockAt(BlockPlace.this.ctw.getMapConfigHandler().blueWool).getType() == Material.WOOL) {
                    BlockPlace.this.ctw.getWoolHandler().setBluePlaced(p);
                    BlockPlace.this.ctw.getFireworks().spawnBlueFirework(BlockPlace.this.ctw.getMapConfigHandler().blueSpawn);
                    ctwPlayer.setWoolPlacements(ctwPlayer.getWoolPlacements() + 1);
                    if (BlockPlace.this.ctw.getWoolHandler().isCyanPlaced()) {
                        if (p == BlockPlace.this.ctw.getWoolHandler().getWhoPlacedCyanWool()) {
                            final String title = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.BlueDouble.title").replace("%PlayerName%", p.getName());
                            final String subtitle = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.BlueDouble.subtitle").replace("%PlayerName%", p.getName());
                            BlockPlace.this.ctw.getMessageUtils().broadcastTitleMessage(title.replaceAll("&", "§"), subtitle.replaceAll("&", "§"));
                            BlockPlace.this.ctw.getSoundHandler().sendCompleteSound(p.getLocation(), p);
                            ctwPlayer.setScore(ctwPlayer.getScore() + BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.doubleBonus"));
                           // BlockPlace.this.ctw.getPlayerScoreHandler().addScore(p, BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.doubleBonus"));
                            BlockPlace.this.ctw.getEconomyHandler().addCoins(p, (double) BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.doubleBonus"));
                            BlockPlace.this.ctw.getMessageUtils().sendScoreMessage(p, "+" + BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.doubleBonus"), BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.doubleBonus"));
                        }
                    } else {
                        final String title = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.BlueWool.title").replace("%PlayerName%", p.getName());
                        final String subtitle = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.BlueWool.subtitle").replace("%PlayerName%", p.getName());
                        BlockPlace.this.ctw.getMessageUtils().broadcastTitleMessage(title.replaceAll("&", "§"), subtitle.replaceAll("&", "§"));
                        BlockPlace.this.ctw.getSoundHandler().sendCompleteSound(p.getLocation(), p);
                        ctwPlayer.setScore(ctwPlayer.getScore() + BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.placeWool"));
                        //BlockPlace.this.ctw.getPlayerScoreHandler().addScore(p, BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.placeWool"));
                        BlockPlace.this.ctw.getEconomyHandler().addCoins(p, (double) BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.placeWool"));
                        BlockPlace.this.ctw.getMessageUtils().sendScoreMessage(p, "+" + BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.placeWool"), BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.placeWool"));
                    }
                    final Location l = new Location(BlockPlace.this.ctw.getMapConfigHandler().blueWool.getWorld(), BlockPlace.this.ctw.getMapConfigHandler().blueWool.getX() + 0.5, BlockPlace.this.ctw.getMapConfigHandler().blueWool.getY(), BlockPlace.this.ctw.getMapConfigHandler().blueWool.getZ() + 0.5, 0.0f, -90.0f);
                    BlockPlace.this.ctw.getEffectUtils().woolPlacedEffect(l);
                    BlockPlace.this.ctw.getWoolAchievementHandler().checkForAchievements(p);
                }
            }
        }, 1L);
    }

    private void cyanWoolPlaced(final Player p) {
        CTWPlayer ctwPlayer = ctw.getCTWPlayerRepository().get(p.getUniqueId());
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.ctw, new Runnable() {
            @Override
            public void run() {
                if (p.getWorld().getBlockAt(BlockPlace.this.ctw.getMapConfigHandler().cyanWool).getType() == Material.WOOL) {
                    BlockPlace.this.ctw.getWoolHandler().setCyanPlaced(p);
                    BlockPlace.this.ctw.getFireworks().spawnBlueFirework(BlockPlace.this.ctw.getMapConfigHandler().blueSpawn);
                    ctwPlayer.setWoolPlacements(ctwPlayer.getWoolPlacements() + 1);
                    if (BlockPlace.this.ctw.getWoolHandler().isBluePlaced()) {
                        if (p == BlockPlace.this.ctw.getWoolHandler().getWhoPlacedBlueWool()) {
                            final String title = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.BlueDouble.title").replace("%PlayerName%", p.getName());
                            final String subtitle = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.BlueDouble.subtitle").replace("%PlayerName%", p.getName());
                            BlockPlace.this.ctw.getMessageUtils().broadcastTitleMessage(title.replaceAll("&", "§"), subtitle.replaceAll("&", "§"));
                            BlockPlace.this.ctw.getSoundHandler().sendCompleteSound(p.getLocation(), p);
                            ctwPlayer.setScore(ctwPlayer.getScore() + BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.doubleBonus"));
                            //BlockPlace.this.ctw.getPlayerScoreHandler().addScore(p, BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.doubleBonus"));
                            BlockPlace.this.ctw.getEconomyHandler().addCoins(p, (double) BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.doubleBonus"));
                            BlockPlace.this.ctw.getMessageUtils().sendScoreMessage(p, "+" + BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.doubleBonus"), BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.doubleBonus"));
                        }
                    } else {
                        final String title = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.CyanWool.title").replace("%PlayerName%", p.getName());
                        final String subtitle = BlockPlace.this.ctw.getLanguageHandler().getMessage("TitleMessages.CyanWool.subtitle").replace("%PlayerName%", p.getName());
                        BlockPlace.this.ctw.getMessageUtils().broadcastTitleMessage(title.replaceAll("&", "§"), subtitle.replaceAll("&", "§"));
                        BlockPlace.this.ctw.getSoundHandler().sendCompleteSound(p.getLocation(), p);
                        ctwPlayer.setScore(ctwPlayer.getScore() + BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.placeWool"));
                        //BlockPlace.this.ctw.getPlayerScoreHandler().addScore(p, BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.placeWool"));
                        BlockPlace.this.ctw.getEconomyHandler().addCoins(p, (double) BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.placeWool"));
                        BlockPlace.this.ctw.getMessageUtils().sendScoreMessage(p, "+" + BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Score.placeWool"), BlockPlace.this.ctw.getConfigHandler().getInteger("Rewards.Coins.placeWool"));
                    }
                    final Location l = new Location(BlockPlace.this.ctw.getMapConfigHandler().cyanWool.getWorld(), BlockPlace.this.ctw.getMapConfigHandler().cyanWool.getX() + 0.5, BlockPlace.this.ctw.getMapConfigHandler().cyanWool.getY(), BlockPlace.this.ctw.getMapConfigHandler().cyanWool.getZ() + 0.5, 0.0f, -90.0f);
                    BlockPlace.this.ctw.getEffectUtils().woolPlacedEffect(l);
                    BlockPlace.this.ctw.getWoolAchievementHandler().checkForAchievements(p);
                }
            }
        }, 1L);
    }
}
