package net.craftersland.ctw.server.achievements;

import net.craftersland.ctw.server.CTW;
import net.craftersland.ctw.server.database.CTWPlayer;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DistanceAchievementHandler {
    private final CTW ctw;
    private final Map<Player, SniperAchievements> achievements;

    public DistanceAchievementHandler(final CTW ctw) {
        this.achievements = new HashMap<>();
        this.ctw = ctw;
    }

    public void loadInitialAchievements(final @NotNull Player p) {
        CTWPlayer ctwPlayer = this.ctw.getCTWPlayerRepository().get(p.getUniqueId());
        final int distance = ctwPlayer.getBowDistanceKill();
        if (distance >= this.ctw.getConfigHandler().getInteger("Achievements.Sniper.Distance-For-IV")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, SniperAchievements.SNIPER4);
            }
        } else if (distance >= this.ctw.getConfigHandler().getInteger("Achievements.Sniper.Distance-For-III")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, SniperAchievements.SNIPER3);
            }
        } else if (distance >= this.ctw.getConfigHandler().getInteger("Achievements.Sniper.Distance-For-II")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, SniperAchievements.SNIPER2);
            }
        } else if (distance >= this.ctw.getConfigHandler().getInteger("Achievements.Sniper.Distance-For-I")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, SniperAchievements.SNIPER1);
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
        final int distance = ctwPlayer.getBowDistanceKill();
        final SniperAchievements currentAcievement = this.achievements.get(p);
        if (distance >= this.ctw.getConfigHandler().getInteger("Achievements.Sniper.Distance-For-IV")) {
            if (currentAcievement != SniperAchievements.SNIPER4) {
                this.awardAchievement(p, SniperAchievements.SNIPER4);
            }
        } else if (distance >= this.ctw.getConfigHandler().getInteger("Achievements.Sniper.Distance-For-III")) {
            if (currentAcievement != SniperAchievements.SNIPER3) {
                this.awardAchievement(p, SniperAchievements.SNIPER3);
            }
        } else if (distance >= this.ctw.getConfigHandler().getInteger("Achievements.Sniper.Distance-For-II")) {
            if (currentAcievement != SniperAchievements.SNIPER2) {
                this.awardAchievement(p, SniperAchievements.SNIPER2);
            }
        } else if (distance >= this.ctw.getConfigHandler().getInteger("Achievements.Sniper.Distance-For-I") && currentAcievement != SniperAchievements.SNIPER1) {
            this.awardAchievement(p, SniperAchievements.SNIPER1);
        }
    }

    private void awardAchievement(final Player p, final SniperAchievements achievement) {
        String msg = "";
        if (achievement == SniperAchievements.SNIPER4) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Sniper-IV");
        } else if (achievement == SniperAchievements.SNIPER3) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Sniper-III");
        } else if (achievement == SniperAchievements.SNIPER2) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Sniper-II");
        } else if (achievement == SniperAchievements.SNIPER1) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Sniper-I");
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
        final SniperAchievements currentAchievement = this.achievements.get(p);
        if (currentAchievement != null) {
            if (currentAchievement == SniperAchievements.SNIPER4) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Sniper-IV").replaceAll("&", "§");
            }
            if (currentAchievement == SniperAchievements.SNIPER3) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Sniper-III").replaceAll("&", "§");
            }
            if (currentAchievement == SniperAchievements.SNIPER2) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Sniper-II").replaceAll("&", "§");
            }
            if (currentAchievement == SniperAchievements.SNIPER1) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Sniper-I").replaceAll("&", "§");
            }
        }
        return data;
    }

    public boolean hasAchievement(final Player p, final SniperAchievements achievement) {
        if (this.achievements.containsKey(p)) {
            final SniperAchievements currentAchievement = this.achievements.get(p);
            if (achievement == SniperAchievements.SNIPER1) {
                return currentAchievement == SniperAchievements.SNIPER1 || currentAchievement == SniperAchievements.SNIPER2 || currentAchievement == SniperAchievements.SNIPER3 || currentAchievement == SniperAchievements.SNIPER4;
            } else if (achievement == SniperAchievements.SNIPER2) {
                return currentAchievement == SniperAchievements.SNIPER2 || currentAchievement == SniperAchievements.SNIPER3 || currentAchievement == SniperAchievements.SNIPER4;
            } else if (achievement == SniperAchievements.SNIPER3) {
                return currentAchievement == SniperAchievements.SNIPER3 || currentAchievement == SniperAchievements.SNIPER4;
            } else return achievement == SniperAchievements.SNIPER4 && currentAchievement == SniperAchievements.SNIPER4;
        }
        return false;
    }

    public String getAchievementName(final SniperAchievements achievement) {
        final String data = " ";
        if (achievement == SniperAchievements.SNIPER4) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Sniper-IV").replaceAll("&", "§");
        }
        if (achievement == SniperAchievements.SNIPER3) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Sniper-III").replaceAll("&", "§");
        }
        if (achievement == SniperAchievements.SNIPER2) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Sniper-II").replaceAll("&", "§");
        }
        if (achievement == SniperAchievements.SNIPER1) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Sniper-I").replaceAll("&", "§");
        }
        return data;
    }

    public Integer getRequiredKills(final SniperAchievements achievement) {
        if (achievement == SniperAchievements.SNIPER4) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Sniper.Distance-For-IV");
        }
        if (achievement == SniperAchievements.SNIPER3) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Sniper.Distance-For-III");
        }
        if (achievement == SniperAchievements.SNIPER2) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Sniper.Distance-For-II");
        }
        if (achievement == SniperAchievements.SNIPER1) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Sniper.Distance-For-I");
        }
        return 0;
    }

    public enum SniperAchievements {
        SNIPER1("SNIPER1", 0),
        SNIPER2("SNIPER2", 1),
        SNIPER3("SNIPER3", 2),
        SNIPER4("SNIPER4", 3);

        SniperAchievements(final String s, final int n) {
        }
    }
}
