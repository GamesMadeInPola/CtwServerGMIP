package net.craftersland.ctw.server.events;

import net.craftersland.ctw.server.CTW;
import net.craftersland.ctw.server.database.CTWPlayer;
import net.craftersland.ctw.server.game.GameEngine;
import net.craftersland.ctw.server.game.PlayerProjectile;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Death implements Listener {
    private final CTW ctw;

    public Death(final CTW ctw) {
        this.ctw = ctw;
    }


    @EventHandler
    public void onSplash(@NotNull PotionSplashEvent e) {
        if (e.getEntity().getShooter() instanceof Player) {
            Player player = ((Player) e.getEntity().getShooter()).getPlayer();

            e.getAffectedEntities().forEach(livingEntity -> {

                if (livingEntity instanceof Player) {
                    if (this.ctw.getTeamHandler().getTeam(player) == this.ctw.getTeamHandler().getTeam(((Player) livingEntity).getPlayer())) {

                        e.getPotion().getEffects().forEach(potionEffect -> {

                            if (potionEffect.getType().equals(PotionEffectType.POISON) || potionEffect.getType().equals(PotionEffectType.BLINDNESS) || potionEffect.getType().equals(PotionEffectType.SLOW) ||
                                    potionEffect.getType().equals(PotionEffectType.WEAKNESS) || potionEffect.getType().equals(PotionEffectType.CONFUSION) || potionEffect.getType().equals(PotionEffectType.WITHER) ||
                                    potionEffect.getType().equals(PotionEffectType.HARM)) {
                                e.setIntensity(livingEntity, 0);
                            }
                        });
                    }
                }
            });
        }
    }

    @EventHandler
    public void onMoveEvent(final @NotNull PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location loc = player.getLocation();
        final int y = (int) loc.getY();

        if (y < this.ctw.getMapConfigHandler().pinkWool.getY() - 40 && player.getGameMode() != GameMode.CREATIVE) {
            if (!player.isDead()) {
                player.setLastDamageCause(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.VOID, player.getHealth()));
                player.damage(player.getHealth(), null);
                Bukkit.getScheduler().runTask(this.ctw, () -> player.spigot().respawn());
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(final @NotNull PlayerDeathEvent event) {
        event.setDeathMessage("");

        if (this.ctw.getGameEngine().gameStage == GameEngine.GameStages.COUNTDOWN) return;

        Player victim = event.getEntity();
        CTWPlayer victimCTWPlayer = ctw.getCTWPlayerRepository().get(victim.getUniqueId());

        if (this.ctw.getWoolHandler().listPlayersred().contains(victim) || this.ctw.getWoolHandler().listPlayerspink().contains(victim) || this.ctw.getWoolHandler().listPlayersblue().contains(victim) || this.ctw.getWoolHandler().listPlayerscyan().contains(victim)) {
            Bukkit.getOnlinePlayers().forEach(player1 -> this.ctw.getSoundHandler().sendPlayerWoolDeathSound(player1));
        }

        // Eliminar los jugadores de la lista de wools
        this.ctw.getWoolHandler().listPlayersred().remove(victim);
        this.ctw.getWoolHandler().listPlayerspink().remove(victim);
        this.ctw.getWoolHandler().listPlayersblue().remove(victim);
        this.ctw.getWoolHandler().listPlayerscyan().remove(victim);

        this.ctw.getTakenWools().checkForLostWool(event.getEntity(), event.getDrops());
        event.getDrops().clear();

        // Resetear los datos del jugador
        this.ctw.getKillStreakHandler().resetData(victim);

        // Si el asesino es null cambiamos la causa de la muerte a void
        if (victim.getLastDamageCause() == null) {
            EntityDamageEvent event1 = new EntityDamageEvent(victim, EntityDamageEvent.DamageCause.VOID, victim.getHealth());
            victim.setLastDamageCause(event1);
        }

        // Verificamos que el jugador haya sido asesinado por otro jugador
        if (!(victim.getLastDamageCause().getEntity() instanceof Player)){
            return;
        }

        // Verificamos que el asesino no sea null
        if (victim.getKiller() == null){
            return;
        }

        Player killer = victim.getKiller();
        CTWPlayer killerCTWPlayer = ctw.getCTWPlayerRepository().get(killer.getUniqueId());

        // Empezamos a verificar las causas de la muerte de la victima
        // Si la causa de la muerte es un ataque de un jugador:
        if (victim.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {

            // Mandamos sonido de kill al asesino
            killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 3.0f, 3.0f);

            String meleeDeathBroadcast = this.ctw.getLanguageHandler().getMessage("ChatMessages.MeleeDeathBroadcast")
                    .replace("%WeaponLogo%", this.getWeaponLogo(killer))
                    .replace("%KilledPlayer%", this.ctw.getMessageUtils().getTeamColor(victim))
                    .replace("%Killer%", this.ctw.getMessageUtils().getTeamColor(killer));

            // Nueva forma de manejar las stats de los jugadores en la base de datos
            killerCTWPlayer.setMeleeKills(killerCTWPlayer.getMeleeKills() + 1);
            killerCTWPlayer.setTotalKills(killerCTWPlayer.getTotalKills() + 1);

            killerCTWPlayer.setMatchMeleeKills(killerCTWPlayer.getMatchMeleeKills() + 1);
            killerCTWPlayer.setMatchTotalKills(killerCTWPlayer.getTotalKills() + 1);

            this.ctw.getMeleeAchievementHandler().checkForAchievements(killer);
            this.ctw.getOverpoweredAchievementHandler().checkForAchievements(victim);
            this.ctw.getKillStreakHandler().addStreakKill(killer);

            addPoints(victim, killer, meleeDeathBroadcast);
            addRegen(killer);

            // Si la causa de la muerte es un proyectil:
        } else if (victim.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {

            // Mandamos sonido de kill al asesino
            killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 3.0f, 3.0f);

            int distance;

            try {
                PlayerProjectile pj = ctw.playerProjectile.get(killer.getUniqueId());
                distance = pj.getProjectile(victim.getUniqueId());
            } catch (NullPointerException e) {
                distance = (int) victim.getLocation().distance(killer.getLocation());
                e.printStackTrace();
            }

            final String bowDeathBroadcast = this.ctw.getLanguageHandler().getMessage("ChatMessages.BowDeathBroadcast")
                    .replace("%WeaponLogo%", this.getWeaponLogo(killer))
                    .replace("%KilledPlayer%", this.ctw.getMessageUtils().getTeamColor(victim))
                    .replace("%Killer%", this.ctw.getMessageUtils().getTeamColor(killer))
                    .replace("%distance%", String.valueOf(distance));

            // Nueva forma de manejar las stats de los jugadores en la base de datos
            killerCTWPlayer.setBowKills(killerCTWPlayer.getDefenseKills() + 1);
            killerCTWPlayer.setTotalKills(killerCTWPlayer.getTotalKills() + 1);

            killerCTWPlayer.setMatchBowKills(killerCTWPlayer.getMatchBowKills() + 1);
            killerCTWPlayer.setMatchTotalKills(killerCTWPlayer.getMatchTotalKills() + 1);

            this.ctw.getShooterAchievementHandler().checkForAchievements(killer);
            this.ctw.getOverpoweredAchievementHandler().checkForAchievements(killer);
            this.ctw.getDistanceAchievementHandler().checkForAchievements(killer);
            this.ctw.getKillStreakHandler().addStreakKill(killer);
            //this.ctw.getPlayerKillsHandler().addBowKillMatch(killer);

            addPoints2(victim, killer, bowDeathBroadcast);
            addRegen(killer);

            // Si la causa de la muerte es tirado al vacio, lava o caida (altura):
        } else if (victim.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID
                || victim.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.LAVA
                || victim.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.FALL) {

            // Obtenemos el ultimo jugador que daño al jugador que murio
            Player lastDamagerKiller = this.ctw.getLastDamageHandler().getKiller(victim);

            // Verificamos que el ultimo jugador que daño al jugador que murio no sea null
            if (lastDamagerKiller != null) {
                String voidDeathMessage = this.ctw.getLanguageHandler().getMessage("ChatMessages.VoidDeathBroadcast")
                        .replace("%WeaponLogo%", this.ctw.getLanguageHandler().getMessage("Icons.Other"))
                        .replace("%KilledPlayer%", this.ctw.getMessageUtils().getTeamColor(victim))
                        .replace("%Killer%", this.ctw.getMessageUtils().getTeamColor(lastDamagerKiller));

                this.ctw.getKillStreakHandler().addStreakKill(lastDamagerKiller);
                String weaponType = this.ctw.getLastDamageHandler().getWeaponType(lastDamagerKiller);

                if (weaponType == null) {
                    this.ctw.getLastDamageHandler().setData(victim, lastDamagerKiller, "melee");
                    weaponType = "melee";
                }

                if (weaponType.matches("melee")) {
                    killerCTWPlayer.setMeleeKills(killerCTWPlayer.getMeleeKills() + 1);
                    killerCTWPlayer.setMatchMeleeKills(killerCTWPlayer.getTotalKills() + 1);
                    this.ctw.getMeleeAchievementHandler().checkForAchievements(lastDamagerKiller);
                    this.ctw.getOverpoweredAchievementHandler().checkForAchievements(lastDamagerKiller);

                } else if (weaponType.matches("bow")) {
                    killerCTWPlayer.setBowKills(killerCTWPlayer.getBowKills() + 1);
                    killerCTWPlayer.setMatchBowKills(killerCTWPlayer.getTotalKills() + 1);
                    this.ctw.getShooterAchievementHandler().checkForAchievements(lastDamagerKiller);
                    this.ctw.getOverpoweredAchievementHandler().checkForAchievements(lastDamagerKiller);
                    this.ctw.getDistanceAchievementHandler().checkForAchievements(lastDamagerKiller);
                }

                // Nueva forma de manejar las stats de los jugadores en la base de datos
                killerCTWPlayer.setTotalKills(victimCTWPlayer.getTotalKills() + 1);
                killerCTWPlayer.setMatchTotalKills(killerCTWPlayer.getTotalKills() + 1);
                addRegen(lastDamagerKiller);
                addPoints(victim, lastDamagerKiller, voidDeathMessage);
            } else {
                String voidSelfDeathBroadcast = this.ctw.getLanguageHandler().getMessage("ChatMessages.VoidSelfDeathBroadcast")
                        .replace("%WeaponLogo%", this.ctw.getLanguageHandler().getMessage("Icons.Other"))
                        .replace("%KilledPlayer%", this.ctw.getMessageUtils().getTeamColor(victim));

                sendDeathMessage(ChatColor.translateAlternateColorCodes('&', voidSelfDeathBroadcast));

                victimCTWPlayer.setScore(victimCTWPlayer.getScore() - 1);

                //this.ctw.getPlayerScoreHandler().takeScore(victim, this.ctw.getConfigHandler().getInteger("Rewards.Score.death"));
                this.ctw.getMessageUtils().sendScoreMessage(victim, "-" + this.ctw.getConfigHandler().getInteger("Rewards.Score.death"), null);
            }
        }

        Bukkit.getScheduler().runTask(this.ctw, () -> victim.spigot().respawn());
    }

    private void addPoints(Player victim, Player killer, @NotNull String rawMsg4) {
        final String rawMsg5 = rawMsg4.replace("%KilledPlayer%", this.ctw.getMessageUtils().getTeamColor(victim));
        final String rawMsg6 = rawMsg5.replace("%Killer%", this.ctw.getMessageUtils().getTeamColor(killer));
        addPoints2(victim, killer, rawMsg6);
    }

    private void addPoints2(Player victim, @NotNull Player killer, @NotNull String rawMsg6) {

        CTWPlayer victimCTWPlayer = ctw.getCTWPlayerRepository().get(victim.getUniqueId());
        CTWPlayer killerCTWPlayer = ctw.getCTWPlayerRepository().get(killer.getUniqueId());
        this.sendDeathMessage(ChatColor.translateAlternateColorCodes('&', rawMsg6));

        // Quitar puntos al jugador que muere
        victimCTWPlayer.setScore(victimCTWPlayer.getScore() - 1);
        //this.ctw.getPlayerScoreHandler().takeScore(victim, this.ctw.getConfigHandler().getInteger("Rewards.Score.death"));
        this.ctw.getMessageUtils().sendScoreMessage(victim, "-" + this.ctw.getConfigHandler().getInteger("Rewards.Score.death"), null);

        // Añadir puntos al jugador que mata
        // Nueva forma de manejar las stats de los jugadores en la base de datos
        killerCTWPlayer.setScore(killerCTWPlayer.getScore() + 1);

        //this.ctw.getPlayerScoreHandler().addScore(killer, this.ctw.getConfigHandler().getInteger("Rewards.Score.kill"));
        this.ctw.getEconomyHandler().addCoins(killer, Double.valueOf(this.ctw.getConfigHandler().getInteger("Rewards.Coins.kill")));
        this.ctw.getMessageUtils().sendScoreMessage(killer, "+" + this.ctw.getConfigHandler().getInteger("Rewards.Score.kill"), this.ctw.getConfigHandler().getInteger("Rewards.Coins.kill"));
    }

    private void addRegen(Player killer) {

        if (killer == null) {
            return;
        }

        if (this.ctw.getGameEngine().gameStage != GameEngine.GameStages.COUNTDOWN) {
            killer.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE));
            killer.getInventory().addItem(new ItemStack(Material.ARROW, 2));
        }

        if (!checkArmor(killer)) return;

        Bukkit.getScheduler().runTaskLater(this.ctw, () -> {
            if (this.ctw.getKillStreakHandler().getStreak(killer) <= 3) {
                killer.removePotionEffect(PotionEffectType.REGENERATION);
                killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 1));
            } else {
                killer.removePotionEffect(PotionEffectType.ABSORPTION);
                killer.removePotionEffect(PotionEffectType.REGENERATION);

                killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
                killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 200, 0));
            }
        }, 3L);
    }

    private String getWeaponLogo(final Player p) {
        String icon = "";
        try {
            if (p.getItemInHand().getType() == Material.IRON_SWORD) {
                icon = this.ctw.getLanguageHandler().getMessage("Icons.IronSword");
            } else if (p.getItemInHand().getType() == Material.DIAMOND_SWORD) {
                icon = this.ctw.getLanguageHandler().getMessage("Icons.DiamondSword");
            } else if (p.getItemInHand().getType() == Material.BOW) {
                icon = this.ctw.getLanguageHandler().getMessage("Icons.Bow");
            } else if (p.getItemInHand().getType() == Material.GOLD_SWORD) {
                icon = this.ctw.getLanguageHandler().getMessage("Icons.GoldSword");
            } else if (p.getItemInHand().getType() == Material.STONE_SWORD) {
                icon = this.ctw.getLanguageHandler().getMessage("Icons.StoneSword");
            } else if (p.getItemInHand().getType() == Material.WOOD_SWORD) {
                icon = this.ctw.getLanguageHandler().getMessage("Icons.IronSword");
            } else {
                icon = this.ctw.getLanguageHandler().getMessage("Icons.Other");
            }
        } catch (Exception e) {
            CTW.log.warning("Error getting weapon logo for the weapon used! Error: " + e.getMessage());
        }
        return icon;
    }

    private void sendDeathMessage(final String msg) {
        final List<Player> redTeam = this.ctw.getTeamHandler().redTeamCopy();
        final List<Player> blueTeam = this.ctw.getTeamHandler().blueTeamCopy();
        if (!redTeam.isEmpty()) {
            for (final Player p : redTeam) {
                if (p != null && p.isOnline()) {
                    p.sendMessage(msg);
                }
            }
        }

        if (!blueTeam.isEmpty()) {
            for (final Player p : blueTeam) {
                if (p != null && p.isOnline()) {
                    p.sendMessage(msg);
                }
            }
        }
    }

    private boolean checkArmor(@NotNull Player player) {

        if (player.getInventory().getHelmet() == null
                || player.getInventory().getChestplate() == null
                || player.getInventory().getLeggings() == null
                || player.getInventory().getBoots() == null) {
            return false;
        }

        if (player.getInventory().getHelmet().getType() == Material.IRON_HELMET) {
            return false;
        } else if (player.getInventory().getBoots().getType() == Material.IRON_BOOTS) {
            return false;
        } else if (player.getInventory().getLeggings().getType() == Material.IRON_LEGGINGS) {
            return false;
        } else if (player.getInventory().getChestplate().getType() == Material.IRON_CHESTPLATE) {
            return false;
        } else if (player.getInventory().getHelmet().getType() == Material.DIAMOND_HELMET) {
            return false;
        } else if (player.getInventory().getBoots().getType() == Material.DIAMOND_BOOTS) {
            return false;
        } else if (player.getInventory().getLeggings().getType() == Material.DIAMOND_LEGGINGS) {
            return false;
        } else if (player.getInventory().getChestplate().getType() == Material.DIAMOND_CHESTPLATE) {
            return false;
        } else if (player.getInventory().getHelmet().getType() == Material.GOLD_HELMET) {
            return false;
        } else if (player.getInventory().getBoots().getType() == Material.GOLD_BOOTS) {
            return false;
        } else if (player.getInventory().getLeggings().getType() == Material.GOLD_LEGGINGS) {
            return false;
        } else if (player.getInventory().getHelmet().getType() == Material.GOLD_HELMET) {
            return false;
        }
        return true;
    }
}
