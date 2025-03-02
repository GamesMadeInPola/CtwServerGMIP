package net.craftersland.ctw.server.achievements;

import net.craftersland.ctw.server.CTW;
import net.craftersland.ctw.server.database.CTWPlayer;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ShooterAchievementHandler {
    private final CTW ctw;
    private final Map<Player, ShooterAchievements> achievements;

    public ShooterAchievementHandler(final CTW ctw) {
        this.achievements = new HashMap<Player, ShooterAchievements>();
        this.ctw = ctw;
    }

    public void loadInitialAchievements(final @NotNull Player p) {
        CTWPlayer ctwPlayer = this.ctw.getCTWPlayerRepository().get(p.getUniqueId());
        final int bowKills = ctwPlayer.getBowKills();
        if (bowKills >= this.ctw.getConfigHandler().getInteger("Achievements.Bow-Kills.Kills-For-IV")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, ShooterAchievements.SHOOTER4);
            }
        } else if (bowKills >= this.ctw.getConfigHandler().getInteger("Achievements.Bow-Kills.Kills-For-III")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, ShooterAchievements.SHOOTER3);
            }
        } else if (bowKills >= this.ctw.getConfigHandler().getInteger("Achievements.Bow-Kills.Kills-For-II")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, ShooterAchievements.SHOOTER2);
            }
        } else if (bowKills >= this.ctw.getConfigHandler().getInteger("Achievements.Bow-Kills.Kills-For-I")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, ShooterAchievements.SHOOTER1);
            }
        } else if (!this.achievements.containsKey(p)) {
            this.achievements.put(p, null);
        }
    }

    public void removeOnDisconnect(final Player p) {
        this.achievements.remove(p);
    }

    public void checkForAchievements(final @NotNull Player p) {
        CTWPlayer ctwPlayer = this.ctw.getCTWPlayerRepository().get(p.getUniqueId());
        final int bowKills = ctwPlayer.getBowKills();
        final ShooterAchievements currentAcievement = this.achievements.get(p);
        if (bowKills >= this.ctw.getConfigHandler().getInteger("Achievements.Bow-Kills.Kills-For-IV")) {
            if (currentAcievement != ShooterAchievements.SHOOTER4) {
                this.awardAchievement(p, ShooterAchievements.SHOOTER4);
            }
        } else if (bowKills >= this.ctw.getConfigHandler().getInteger("Achievements.Bow-Kills.Kills-For-III")) {
            if (currentAcievement != ShooterAchievements.SHOOTER3) {
                this.awardAchievement(p, ShooterAchievements.SHOOTER3);
            }
        } else if (bowKills >= this.ctw.getConfigHandler().getInteger("Achievements.Bow-Kills.Kills-For-II")) {
            if (currentAcievement != ShooterAchievements.SHOOTER2) {
                this.awardAchievement(p, ShooterAchievements.SHOOTER2);
            }
        } else if (bowKills >= this.ctw.getConfigHandler().getInteger("Achievements.Bow-Kills.Kills-For-I") && currentAcievement != ShooterAchievements.SHOOTER1) {
            this.awardAchievement(p, ShooterAchievements.SHOOTER1);
        }
    }

    private void awardAchievement(final Player p, final ShooterAchievements achievement) {
        String msg = "";
        if (achievement == ShooterAchievements.SHOOTER4) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Bow-Kills-IV");
        } else if (achievement == ShooterAchievements.SHOOTER3) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Bow-Kills-III");
        } else if (achievement == ShooterAchievements.SHOOTER2) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Bow-Kills-II");
        } else if (achievement == ShooterAchievements.SHOOTER1) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Bow-Kills-I");
        }
        this.achievements.put(p, achievement);
        final String chatMsg = this.ctw.getLanguageHandler().getMessage("ChatMessages.AwardAchievement").replace("%AchievementName%", msg);
        final String title = this.ctw.getLanguageHandler().getMessage("TitleMessages.AwardAchievement.title").replace("%AchievementName%", msg);
        final String subtitle = this.ctw.getLanguageHandler().getMessage("TitleMessages.AwardAchievement.subtitle").replace("%AchievementName%", msg);
        p.awardAchievement(Achievement.SNIPE_SKELETON);
        this.ctw.getSoundHandler().sendCompleteSound(p.getLocation(), p);
        p.sendMessage(chatMsg.replaceAll("&", "§"));
        this.ctw.getMessageUtils().sendTitleMessage(title.replaceAll("&", "§"), subtitle.replaceAll("&", "§"), p);
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.ctw, new Runnable() {
            @Override
            public void run() {
                p.removeAchievement(Achievement.SNIPE_SKELETON);
            }
        }, 20L);
    }

    public String getCurrentAchievement(final Player p) {
        final String data = null;
        final ShooterAchievements currentAchievement = this.achievements.get(p);
        if (currentAchievement != null) {
            if (currentAchievement == ShooterAchievements.SHOOTER4) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Bow-Kills-IV").replaceAll("&", "§");
            }
            if (currentAchievement == ShooterAchievements.SHOOTER3) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Bow-Kills-III").replaceAll("&", "§");
            }
            if (currentAchievement == ShooterAchievements.SHOOTER2) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Bow-Kills-II").replaceAll("&", "§");
            }
            if (currentAchievement == ShooterAchievements.SHOOTER1) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Bow-Kills-I").replaceAll("&", "§");
            }
        }
        return data;
    }

    public boolean hasAchievement(final Player p, final ShooterAchievements achievement) {
        if (this.achievements.containsKey(p)) {
            final ShooterAchievements currentAchievement = this.achievements.get(p);
            if (achievement == ShooterAchievements.SHOOTER1) {
                return currentAchievement == ShooterAchievements.SHOOTER1 || currentAchievement == ShooterAchievements.SHOOTER2 || currentAchievement == ShooterAchievements.SHOOTER3 || currentAchievement == ShooterAchievements.SHOOTER4;
            } else if (achievement == ShooterAchievements.SHOOTER2) {
                return currentAchievement == ShooterAchievements.SHOOTER2 || currentAchievement == ShooterAchievements.SHOOTER3 || currentAchievement == ShooterAchievements.SHOOTER4;
            } else if (achievement == ShooterAchievements.SHOOTER3) {
                return currentAchievement == ShooterAchievements.SHOOTER3 || currentAchievement == ShooterAchievements.SHOOTER4;
            } else
                return achievement == ShooterAchievements.SHOOTER4 && currentAchievement == ShooterAchievements.SHOOTER4;
        }
        return false;
    }

    public String getAchievementName(final ShooterAchievements achievement) {
        final String data = " ";
        if (achievement == ShooterAchievements.SHOOTER4) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Bow-Kills-IV").replaceAll("&", "§");
        }
        if (achievement == ShooterAchievements.SHOOTER3) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Bow-Kills-III").replaceAll("&", "§");
        }
        if (achievement == ShooterAchievements.SHOOTER2) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Bow-Kills-II").replaceAll("&", "§");
        }
        if (achievement == ShooterAchievements.SHOOTER1) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Bow-Kills-I").replaceAll("&", "§");
        }
        return data;
    }

    public Integer getRequiredKills(final ShooterAchievements achievement) {
        if (achievement == ShooterAchievements.SHOOTER4) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Bow-Kills.Kills-For-IV");
        }
        if (achievement == ShooterAchievements.SHOOTER3) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Bow-Kills.Kills-For-III");
        }
        if (achievement == ShooterAchievements.SHOOTER2) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Bow-Kills.Kills-For-II");
        }
        if (achievement == ShooterAchievements.SHOOTER1) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Bow-Kills.Kills-For-I");
        }
        return 0;
    }

    public enum ShooterAchievements {
        SHOOTER1("SHOOTER1", 0),
        SHOOTER2("SHOOTER2", 1),
        SHOOTER3("SHOOTER3", 2),
        SHOOTER4("SHOOTER4", 3);

        ShooterAchievements(final String s, final int n) {
        }
    }
}
