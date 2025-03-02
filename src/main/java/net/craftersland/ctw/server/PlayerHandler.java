package net.craftersland.ctw.server;

import com.yapzhenyie.GadgetsMenu.api.GadgetsMenuAPI;
import com.yapzhenyie.GadgetsMenu.cosmetics.particles.ParticleType;
import com.yapzhenyie.GadgetsMenu.player.PlayerManager;
import net.craftersland.ctw.server.achievements.*;
import net.craftersland.ctw.server.database.CTWPlayer;
import net.craftersland.ctw.server.game.GameEngine;
import net.craftersland.ctw.server.utils.StartupKit;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerHandler {
    private final CTW ctw;

    public PlayerHandler(final CTW ctw) {
        this.ctw = ctw;
    }

    public Boolean hasAchievement(final Player p, final @NotNull String achievement) {
        if (achievement.matches("SNIPER1") || achievement.matches("SNIPER2") || achievement.matches("SNIPER3") || achievement.matches("SNIPER4")) {
            return this.ctw.getDistanceAchievementHandler().hasAchievement(p, DistanceAchievementHandler.SniperAchievements.valueOf(achievement));
        }
        if (achievement.matches("MELEE1") || achievement.matches("MELEE2") || achievement.matches("MELEE3") || achievement.matches("MELEE4")) {
            return this.ctw.getMeleeAchievementHandler().hasAchievement(p, MeleeAchievementHandler.MeleeAchievements.valueOf(achievement));
        }
        if (achievement.matches("OVERPOWERED1") || achievement.matches("OVERPOWERED2") || achievement.matches("OVERPOWERED3") || achievement.matches("OVERPOWERED4")) {
            return this.ctw.getOverpoweredAchievementHandler().hasAchievement(p, OverpoweredAchievementHandler.OverpoweredAchievements.valueOf(achievement));
        }
        if (achievement.matches("SHOOTER1") || achievement.matches("SHOOTER2") || achievement.matches("SHOOTER3") || achievement.matches("SHOOTER4")) {
            return this.ctw.getShooterAchievementHandler().hasAchievement(p, ShooterAchievementHandler.ShooterAchievements.valueOf(achievement));
        }
        if (achievement.matches("WOOLMASTER1") || achievement.matches("WOOLMASTER2") || achievement.matches("WOOLMASTER3") || achievement.matches("WOOLMASTER4")) {
            return this.ctw.getWoolAchievementHandler().hasAchievement(p, WoolAchievementHandler.WoolAchievements.valueOf(achievement));
        }
        return true;
    }

    public void sendJoinMessage(final Player p) {
        final int delay = this.ctw.getConfigHandler().getInteger("Settings.JoinMessageDelay") / 1000;
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.ctw, () -> {
            final List<String> rawMsg = new ArrayList<String>(PlayerHandler.this.ctw.getLanguageHandler().getMessageList("ChatMessages.JoinMessage"));
            final List<String> processedMsg = new ArrayList<String>();
            if (!rawMsg.isEmpty()) {
                for (final String s : rawMsg) {
                    processedMsg.add(s.replaceAll("&", "§"));
                }
            }
            if (!processedMsg.isEmpty()) {
                String[] msg = new String[processedMsg.size()];
                msg = processedMsg.toArray(msg);
                p.sendMessage(msg);
                PlayerHandler.this.ctw.getSoundHandler().sendConfirmSound(p.getLocation(), p);
            }
        }, delay * 20L);
    }

    public void addSpectator(final Player p) {
        this.resetPlayer(p);
        this.ctw.getTeamHandler().addSpectator(p);
        Bukkit.getScheduler().runTask(this.ctw, () -> {
            final Location l = PlayerHandler.this.ctw.getMapConfigHandler().getSpectatorSpawn();
            if (l != null) {
                p.teleport(PlayerHandler.this.ctw.getMapConfigHandler().getSpectatorSpawn());
            }
            p.setGameMode(GameMode.SPECTATOR);
        });
    }

    public void addRedTeam(final Player p) {
        if (this.ctw.getTeamHandler().countRedTeam() < this.ctw.getMapConfigHandler().maxPlayers || p.hasPermission("CTW.joinfullteams")) {
            this.resetPlayer(p);
            this.ctw.getTeamHandler().addRedTeam(p);
            this.ctw.getNewScoreboardHandler().addToRedTeam(p);
            Bukkit.getScheduler().runTask(this.ctw, new Runnable() {
                @Override
                public void run() {
                    p.teleport(PlayerHandler.this.ctw.getMapConfigHandler().redSpawn);
                    if (PlayerHandler.this.ctw.getGameEngine().gameStage == GameEngine.GameStages.COUNTDOWN) {
                        p.setGameMode(GameMode.SPECTATOR);
                    } else {
                        p.setGameMode(GameMode.SURVIVAL);
                    }
                }
            });
            this.ctw.getMessageUtils().sendTitleMessage(this.ctw.getLanguageHandler().getMessage("TitleMessages.JoinRedTeam.title").replaceAll("&", "§"), this.ctw.getLanguageHandler().getMessage("TitleMessages.JoinRedTeam.subtitle").replaceAll("&", "§"), p);
            this.ctw.getSoundHandler().sendCompleteSound(this.ctw.getMapConfigHandler().redSpawn, p);
            final List<String> rawMsg = new ArrayList<String>(this.ctw.getLanguageHandler().getMessageList("ChatMessages.JoinRedTeam"));
            final List<String> processedMsg = new ArrayList<>();
            if (!rawMsg.isEmpty()) {
                for (final String s : rawMsg) {
                    processedMsg.add(s.replaceAll("&", "§"));
                }
            }
            if (!processedMsg.isEmpty()) {
                String[] msg = new String[processedMsg.size()];
                msg = processedMsg.toArray(msg);
                p.sendMessage(msg);
            }
            if (this.ctw.getGameEngine().gameStage != GameEngine.GameStages.COUNTDOWN) {
                this.ctw.getStartupKit().giveStartupKit(p);
            }
            if (p.hasPermission("CTW.particles")) {
                this.ctw.getEffectUtils().startVipParticles(p);
            }
        } else {
            this.ctw.getSoundHandler().sendFailedSound(p.getLocation(), p);
            p.sendMessage(this.ctw.getLanguageHandler().getMessage("ChatMessages.RedTeamFull").replaceAll("&", "§"));
        }
    }

    public void respawnRedTeam(final Player p) {
        this.resetPlayer(p);
        CTWPlayer ctwPlayer = ctw.getCTWPlayerRepository().get(p.getUniqueId());
        Bukkit.getScheduler().runTask(this.ctw, () -> {

            if (PlayerHandler.this.ctw.getGameEngine().gameStage == GameEngine.GameStages.COUNTDOWN) {
                p.setGameMode(GameMode.ADVENTURE);

            } else if (PlayerHandler.this.ctw.getGameEngine().gameStage == GameEngine.GameStages.RUNNING) {

                if (ctwPlayer.getEffects() != null) {

                    PlayerManager playerManager = GadgetsMenuAPI.getPlayerManager(p);
                    playerManager.unequipParticle();

                }

                p.setGameMode(GameMode.SURVIVAL);
                p.setHealth(p.getMaxHealth());
            }
            p.teleport(PlayerHandler.this.ctw.getMapConfigHandler().redSpawn);
        });

        if (PlayerHandler.this.ctw.getGameEngine().gameStage == GameEngine.GameStages.RUNNING) {
            this.ctw.getTeamHandler().setRedSuit(p);
            this.ctw.getStartupKit().giveStartupKit(p);
            StartupKit.setUnbreakableArmor(p);
        }
    }

    public void addBlueTeam(final Player p) {
        if (this.ctw.getTeamHandler().countBlueTeam() < this.ctw.getMapConfigHandler().maxPlayers || p.hasPermission("CTW.joinfullteams")) {
            this.resetPlayer(p);
            this.ctw.getTeamHandler().addBlueTeam(p);
            this.ctw.getNewScoreboardHandler().addToBlueTeam(p);
            Bukkit.getScheduler().runTask(this.ctw, new Runnable() {
                @Override
                public void run() {
                    p.teleport(PlayerHandler.this.ctw.getMapConfigHandler().blueSpawn);
                    if (PlayerHandler.this.ctw.getGameEngine().gameStage == GameEngine.GameStages.COUNTDOWN) {
                        p.setGameMode(GameMode.SPECTATOR);
                    } else {
                        p.setGameMode(GameMode.SURVIVAL);
                    }
                }
            });
            this.ctw.getMessageUtils().sendTitleMessage(this.ctw.getLanguageHandler().getMessage("TitleMessages.JoinBlueTeam.title").replaceAll("&", "§"), this.ctw.getLanguageHandler().getMessage("TitleMessages.JoinBlueTeam.subtitle").replaceAll("&", "§"), p);
            this.ctw.getSoundHandler().sendCompleteSound(this.ctw.getMapConfigHandler().blueSpawn, p);
            final List<String> rawMsg = new ArrayList<String>(this.ctw.getLanguageHandler().getMessageList("ChatMessages.JoinBlueTeam"));
            final List<String> processedMsg = new ArrayList<String>();
            if (!rawMsg.isEmpty()) {
                for (final String s : rawMsg) {
                    processedMsg.add(s.replaceAll("&", "§"));
                }
            }
            if (!processedMsg.isEmpty()) {
                String[] msg = new String[processedMsg.size()];
                msg = processedMsg.toArray(msg);
                p.sendMessage(msg);
            }
            if (this.ctw.getGameEngine().gameStage != GameEngine.GameStages.COUNTDOWN) {
                this.ctw.getStartupKit().giveStartupKit(p);
            }
            if (p.hasPermission("CTW.particles")) {
                if (!p.hasPermission("JrMOD")) {
                    this.ctw.getEffectUtils().startVipParticles(p);
                }
            }
        } else {
            this.ctw.getSoundHandler().sendFailedSound(p.getLocation(), p);
            p.sendMessage(this.ctw.getLanguageHandler().getMessage("ChatMessages.BlueTeamFull").replaceAll("&", "§"));
        }
    }

    public void respawnBlueTeam(final Player p) {
        this.resetPlayer(p);
        CTWPlayer ctwPlayer = ctw.getCTWPlayerRepository().get(p.getUniqueId());
        Bukkit.getScheduler().runTask(this.ctw, new Runnable() {
            @Override
            public void run() {

                if (PlayerHandler.this.ctw.getGameEngine().gameStage == GameEngine.GameStages.COUNTDOWN) {

                    p.setGameMode(GameMode.ADVENTURE);
                } else if (PlayerHandler.this.ctw.getGameEngine().gameStage == GameEngine.GameStages.RUNNING) {

                    p.setGameMode(GameMode.SURVIVAL);
                    p.setHealth(p.getMaxHealth());

                    if (ctwPlayer.getEffects() != null) {

                        PlayerManager playerManager = GadgetsMenuAPI.getPlayerManager(p);
                        playerManager.unequipParticle();

                    }
                }
                p.teleport(PlayerHandler.this.ctw.getMapConfigHandler().blueSpawn);
            }
        });
        if (PlayerHandler.this.ctw.getGameEngine().gameStage == GameEngine.GameStages.RUNNING) {
            this.ctw.getTeamHandler().setBlueSuit(p);
            this.ctw.getStartupKit().giveStartupKit(p);
            StartupKit.setUnbreakableArmor(p);
        }
    }

    public void respawnSpectator(final Player p) {
        this.resetPlayer(p);
        Bukkit.getScheduler().runTask(this.ctw, new Runnable() {
            @Override
            public void run() {
                p.setGameMode(GameMode.SURVIVAL);
                p.teleport(PlayerHandler.this.ctw.getMapConfigHandler().spectatorSpawn);
                p.setGameMode(GameMode.SPECTATOR);
            }
        });
    }

    private void resetPlayer(final Player p) {
        p.getInventory().clear();
        p.getInventory().setHelmet(null);
        p.getInventory().setChestplate(null);
        p.getInventory().setLeggings(null);
        p.getInventory().setBoots(null);
        p.setHealth(20.0);
        p.setFoodLevel(20);
        p.setSaturation(4.0f);
        p.setExp(0.0f);
        p.setLevel(0);
        if (!p.getActivePotionEffects().isEmpty()) {
            for (final PotionEffect po : p.getActivePotionEffects()) {
                p.removePotionEffect(po.getType());
            }
        }
    }

    public void autoAddTeam(final Player p) {
        if (this.ctw.getTeamHandler().countRedTeam() < this.ctw.getMapConfigHandler().maxPlayers && this.ctw.getTeamHandler().countRedTeam() <= this.ctw.getTeamHandler().countBlueTeam()) {
            this.addRedTeam(p);
        } else if (this.ctw.getTeamHandler().countBlueTeam() < this.ctw.getMapConfigHandler().maxPlayers && this.ctw.getTeamHandler().countBlueTeam() <= this.ctw.getTeamHandler().countRedTeam()) {
            this.addBlueTeam(p);
        } else if (p.hasPermission("CTW.joinfullteams")) {
            if (this.ctw.getTeamHandler().countRedTeam() <= this.ctw.getTeamHandler().countBlueTeam()) {
                this.addRedTeam(p);
            } else {
                this.addBlueTeam(p);
            }
        } else {
            this.ctw.getSoundHandler().sendBaseDrumSound(p.getLocation(), p);
            p.sendMessage(this.ctw.getLanguageHandler().getMessage("ChatMessages.AllTeamsFull").replaceAll("&", "§"));
        }
    }

    // TODO Efecto
    public void playerSetWonSpectator(final Player p) {
        this.resetPlayer(p);
        CTWPlayer ctwPlayer = ctw.getCTWPlayerRepository().get(p.getUniqueId());
        Bukkit.getScheduler().runTask(this.ctw, new Runnable() {
            @Override
            public void run() {

                String effect = ctwPlayer.getEffects();

                if (effect != null) {
                    PlayerManager playerManager = GadgetsMenuAPI.getPlayerManager(p);
                    playerManager.equipParticle(ParticleType.valueOf(effect));
                }

                p.setGameMode(GameMode.ADVENTURE);
            }
        });
    }

    public void respawnAllPlayers() {
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.ctw, new Runnable() {
            @Override
            public void run() {
                final List<Player> red = PlayerHandler.this.ctw.getTeamHandler().redTeamCopy();
                final List<Player> blue = PlayerHandler.this.ctw.getTeamHandler().blueTeamCopy();
                final List<Player> spec = PlayerHandler.this.ctw.getTeamHandler().spectatorsCopy();
                if (!red.isEmpty()) {
                    for (final Player p : red) {
                        if (p != null && p.isOnline()) {

                            Bukkit.getScheduler().runTask(ctw, () -> {

                                p.setGameMode(GameMode.SURVIVAL);
                                p.setHealth(p.getMaxHealth());
                                p.teleport(PlayerHandler.this.ctw.getMapConfigHandler().redSpawn);
                                ctw.getTeamHandler().setRedSuit(p);
                                ctw.getStartupKit().giveStartupKit(p);
                            });

                            PlayerHandler.this.ctw.getKillStreakHandler().resetData(p);
                        }
                    }
                }
                if (!blue.isEmpty()) {
                    for (final Player p : blue) {
                        if (p != null && p.isOnline()) {

                            Bukkit.getScheduler().runTask(ctw, () -> {

                                p.setGameMode(GameMode.SURVIVAL);
                                p.teleport(PlayerHandler.this.ctw.getMapConfigHandler().blueSpawn);
                                ctw.getTeamHandler().setBlueSuit(p);
                                ctw.getStartupKit().giveStartupKit(p);
                            });

                            PlayerHandler.this.ctw.getKillStreakHandler().resetData(p);
                        }
                    }
                }
                if (!spec.isEmpty()) {
                    for (final Player p : spec) {
                        if (p != null && p.isOnline() && PlayerHandler.this.ctw.getTeamHandler().isSpectator(p)) {
                            PlayerHandler.this.respawnSpectator(p);
                        }
                    }
                }
                Bukkit.broadcastMessage(PlayerHandler.this.ctw.getLanguageHandler().getMessage("ChatMessages.NewGameBroadcast").replaceAll("&", "§"));
            }
        }, 10L);
    }
}
