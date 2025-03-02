package net.craftersland.ctw.server.achievements;

import net.craftersland.ctw.server.CTW;
import net.craftersland.ctw.server.database.CTWPlayer;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MeleeAchievementHandler {
    private final CTW ctw;
    private final Map<Player, MeleeAchievements> achievements;

    public MeleeAchievementHandler(final CTW ctw) {
        this.achievements = new HashMap<Player, MeleeAchievements>();
        this.ctw = ctw;
    }

    public void loadInitialAchievements(final @NotNull Player p) {
        CTWPlayer ctwPlayer = this.ctw.getCTWPlayerRepository().get(p.getUniqueId());
        final int meleeKills = ctwPlayer.getMeleeKills();
        if (meleeKills >= this.ctw.getConfigHandler().getInteger("Achievements.Melee-Kills.Kills-For-IV")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, MeleeAchievements.MELEE4);
            }
        } else if (meleeKills >= this.ctw.getConfigHandler().getInteger("Achievements.Melee-Kills.Kills-For-III")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, MeleeAchievements.MELEE3);
            }
        } else if (meleeKills >= this.ctw.getConfigHandler().getInteger("Achievements.Melee-Kills.Kills-For-II")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, MeleeAchievements.MELEE2);
            }
        } else if (meleeKills >= this.ctw.getConfigHandler().getInteger("Achievements.Melee-Kills.Kills-For-I")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, MeleeAchievements.MELEE1);
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
        final int meleeKills = ctwPlayer.getMeleeKills();
        final MeleeAchievements currentAcievement = this.achievements.get(p);
        if (meleeKills >= this.ctw.getConfigHandler().getInteger("Achievements.Melee-Kills.Kills-For-IV")) {
            if (currentAcievement != MeleeAchievements.MELEE4) {
                this.awardAchievement(p, MeleeAchievements.MELEE4);
            }
        } else if (meleeKills >= this.ctw.getConfigHandler().getInteger("Achievements.Melee-Kills.Kills-For-III")) {
            if (currentAcievement != MeleeAchievements.MELEE3) {
                this.awardAchievement(p, MeleeAchievements.MELEE3);
            }
        } else if (meleeKills >= this.ctw.getConfigHandler().getInteger("Achievements.Melee-Kills.Kills-For-II")) {
            if (currentAcievement != MeleeAchievements.MELEE2) {
                this.awardAchievement(p, MeleeAchievements.MELEE2);
            }
        } else if (meleeKills >= this.ctw.getConfigHandler().getInteger("Achievements.Melee-Kills.Kills-For-I") && currentAcievement != MeleeAchievements.MELEE1) {
            this.awardAchievement(p, MeleeAchievements.MELEE1);
        }
    }

    private void awardAchievement(final Player p, final MeleeAchievements achievement) {
        String msg = "";
        if (achievement == MeleeAchievements.MELEE4) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Melee-Kills-IV");
        } else if (achievement == MeleeAchievements.MELEE3) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Melee-Kills-III");
        } else if (achievement == MeleeAchievements.MELEE2) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Melee-Kills-II");
        } else if (achievement == MeleeAchievements.MELEE1) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Melee-Kills-I");
        }
        this.achievements.put(p, achievement);
        final String chatMsg = this.ctw.getLanguageHandler().getMessage("ChatMessages.AwardAchievement").replace("%AchievementName%", msg);
        final String title = this.ctw.getLanguageHandler().getMessage("TitleMessages.AwardAchievement.title").replace("%AchievementName%", msg);
        final String subtitle = this.ctw.getLanguageHandler().getMessage("TitleMessages.AwardAchievement.subtitle").replace("%AchievementName%", msg);
        p.awardAchievement(Achievement.OVERKILL);
        this.ctw.getSoundHandler().sendCompleteSound(p.getLocation(), p);
        p.sendMessage(chatMsg.replaceAll("&", "§"));
        this.ctw.getMessageUtils().sendTitleMessage(title.replaceAll("&", "§"), subtitle.replaceAll("&", "§"), p);
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.ctw, new Runnable() {
            @Override
            public void run() {
                p.removeAchievement(Achievement.OVERKILL);
            }
        }, 20L);
    }

    public String getCurrentAchievement(final Player p) {
        final String data = null;
        final MeleeAchievements currentAchievement = this.achievements.get(p);
        if (currentAchievement != null) {
            if (currentAchievement == MeleeAchievements.MELEE4) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Melee-Kills-IV").replaceAll("&", "§");
            }
            if (currentAchievement == MeleeAchievements.MELEE3) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Melee-Kills-III").replaceAll("&", "§");
            }
            if (currentAchievement == MeleeAchievements.MELEE2) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Melee-Kills-II").replaceAll("&", "§");
            }
            if (currentAchievement == MeleeAchievements.MELEE1) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Melee-Kills-I").replaceAll("&", "§");
            }
        }
        return data;
    }

    public boolean hasAchievement(final Player p, final MeleeAchievements achievement) {
        if (this.achievements.containsKey(p)) {
            final MeleeAchievements currentAchievement = this.achievements.get(p);
            if (achievement == MeleeAchievements.MELEE1) {
                return currentAchievement == MeleeAchievements.MELEE1 || currentAchievement == MeleeAchievements.MELEE2 || currentAchievement == MeleeAchievements.MELEE3 || currentAchievement == MeleeAchievements.MELEE4;
            } else if (achievement == MeleeAchievements.MELEE2) {
                return currentAchievement == MeleeAchievements.MELEE2 || currentAchievement == MeleeAchievements.MELEE3 || currentAchievement == MeleeAchievements.MELEE4;
            } else if (achievement == MeleeAchievements.MELEE3) {
                return currentAchievement == MeleeAchievements.MELEE3 || currentAchievement == MeleeAchievements.MELEE4;
            } else return achievement == MeleeAchievements.MELEE4 && currentAchievement == MeleeAchievements.MELEE4;
        }
        return false;
    }

    public String getAchievementName(final MeleeAchievements achievement) {
        final String data = " ";
        if (achievement == MeleeAchievements.MELEE4) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Melee-Kills-IV").replaceAll("&", "§");
        }
        if (achievement == MeleeAchievements.MELEE3) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Melee-Kills-III").replaceAll("&", "§");
        }
        if (achievement == MeleeAchievements.MELEE2) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Melee-Kills-II").replaceAll("&", "§");
        }
        if (achievement == MeleeAchievements.MELEE1) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Melee-Kills-I").replaceAll("&", "§");
        }
        return data;
    }

    public Integer getRequiredKills(final MeleeAchievements achievement) {
        if (achievement == MeleeAchievements.MELEE4) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Melee-Kills.Kills-For-IV");
        }
        if (achievement == MeleeAchievements.MELEE3) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Melee-Kills.Kills-For-III");
        }
        if (achievement == MeleeAchievements.MELEE2) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Melee-Kills.Kills-For-II");
        }
        if (achievement == MeleeAchievements.MELEE1) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Melee-Kills.Kills-For-I");
        }
        return 0;
    }

    public enum MeleeAchievements {
        MELEE1("MELEE1", 0),
        MELEE2("MELEE2", 1),
        MELEE3("MELEE3", 2),
        MELEE4("MELEE4", 3);

        MeleeAchievements(final String s, final int n) {
        }
    }
}
