package net.craftersland.ctw.server.achievements;

import net.craftersland.ctw.server.CTW;
import net.craftersland.ctw.server.database.CTWPlayer;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class OverpoweredAchievementHandler {
    private final CTW ctw;
    private final Map<Player, OverpoweredAchievements> achievements;

    public OverpoweredAchievementHandler(final CTW ctw) {
        this.achievements = new HashMap<Player, OverpoweredAchievements>();
        this.ctw = ctw;
    }

    public void loadInitialAchievements(final @NotNull Player p) {
        CTWPlayer ctwPlayer = this.ctw.getCTWPlayerRepository().get(p.getUniqueId());
        final int kills = ctwPlayer.getTotalKills();
        if (kills >= this.ctw.getConfigHandler().getInteger("Achievements.Overpowered.Kills-For-IV")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, OverpoweredAchievements.OVERPOWERED4);
            }
        } else if (kills >= this.ctw.getConfigHandler().getInteger("Achievements.Overpowered.Kills-For-III")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, OverpoweredAchievements.OVERPOWERED3);
            }
        } else if (kills >= this.ctw.getConfigHandler().getInteger("Achievements.Overpowered.Kills-For-II")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, OverpoweredAchievements.OVERPOWERED2);
            }
        } else if (kills >= this.ctw.getConfigHandler().getInteger("Achievements.Overpowered.Kills-For-I")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, OverpoweredAchievements.OVERPOWERED1);
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
        final int kills = ctwPlayer.getTotalKills();
        final OverpoweredAchievements currentAcievement = this.achievements.get(p);
        if (kills >= this.ctw.getConfigHandler().getInteger("Achievements.Overpowered.Kills-For-IV")) {
            if (currentAcievement != OverpoweredAchievements.OVERPOWERED4) {
                this.awardAchievement(p, OverpoweredAchievements.OVERPOWERED4);
            }
        } else if (kills >= this.ctw.getConfigHandler().getInteger("Achievements.Overpowered.Kills-For-III")) {
            if (currentAcievement != OverpoweredAchievements.OVERPOWERED3) {
                this.awardAchievement(p, OverpoweredAchievements.OVERPOWERED3);
            }
        } else if (kills >= this.ctw.getConfigHandler().getInteger("Achievements.Overpowered.Kills-For-II")) {
            if (currentAcievement != OverpoweredAchievements.OVERPOWERED2) {
                this.awardAchievement(p, OverpoweredAchievements.OVERPOWERED2);
            }
        } else if (kills >= this.ctw.getConfigHandler().getInteger("Achievements.Overpowered.Kills-For-I") && currentAcievement != OverpoweredAchievements.OVERPOWERED1) {
            this.awardAchievement(p, OverpoweredAchievements.OVERPOWERED1);
        }
    }

    private void awardAchievement(final Player p, final OverpoweredAchievements achievement) {
        String msg = "";
        if (achievement == OverpoweredAchievements.OVERPOWERED4) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Overpowered-IV");
        } else if (achievement == OverpoweredAchievements.OVERPOWERED3) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Overpowered-III");
        } else if (achievement == OverpoweredAchievements.OVERPOWERED2) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Overpowered-II");
        } else if (achievement == OverpoweredAchievements.OVERPOWERED1) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.Overpowered-I");
        }
        this.achievements.put(p, achievement);
        final String chatMsg = this.ctw.getLanguageHandler().getMessage("ChatMessages.AwardAchievement").replace("%AchievementName%", msg);
        final String title = this.ctw.getLanguageHandler().getMessage("TitleMessages.AwardAchievement.title").replace("%AchievementName%", msg);
        final String subtitle = this.ctw.getLanguageHandler().getMessage("TitleMessages.AwardAchievement.subtitle").replace("%AchievementName%", msg);
        p.awardAchievement(Achievement.OVERPOWERED);
        this.ctw.getSoundHandler().sendCompleteSound(p.getLocation(), p);
        p.sendMessage(chatMsg.replaceAll("&", "§"));
        this.ctw.getMessageUtils().sendTitleMessage(title.replaceAll("&", "§"), subtitle.replaceAll("&", "§"), p);
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.ctw, new Runnable() {
            @Override
            public void run() {
                p.removeAchievement(Achievement.OVERPOWERED);
            }
        }, 20L);
    }

    public String getCurrentAchievement(final Player p) {
        final String data = null;
        final OverpoweredAchievements currentAchievement = this.achievements.get(p);
        if (currentAchievement != null) {
            if (currentAchievement == OverpoweredAchievements.OVERPOWERED4) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Overpowered-IV").replaceAll("&", "§");
            }
            if (currentAchievement == OverpoweredAchievements.OVERPOWERED3) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Overpowered-III").replaceAll("&", "§");
            }
            if (currentAchievement == OverpoweredAchievements.OVERPOWERED2) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Overpowered-II").replaceAll("&", "§");
            }
            if (currentAchievement == OverpoweredAchievements.OVERPOWERED1) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.Overpowered-I").replaceAll("&", "§");
            }
        }
        return data;
    }

    public boolean hasAchievement(final Player p, final OverpoweredAchievements achievement) {
        if (this.achievements.containsKey(p)) {
            final OverpoweredAchievements currentAchievement = this.achievements.get(p);
            if (achievement == OverpoweredAchievements.OVERPOWERED1) {
                return currentAchievement == OverpoweredAchievements.OVERPOWERED1 || currentAchievement == OverpoweredAchievements.OVERPOWERED2 || currentAchievement == OverpoweredAchievements.OVERPOWERED3 || currentAchievement == OverpoweredAchievements.OVERPOWERED4;
            } else if (achievement == OverpoweredAchievements.OVERPOWERED2) {
                return currentAchievement == OverpoweredAchievements.OVERPOWERED2 || currentAchievement == OverpoweredAchievements.OVERPOWERED3 || currentAchievement == OverpoweredAchievements.OVERPOWERED4;
            } else if (achievement == OverpoweredAchievements.OVERPOWERED3) {
                return currentAchievement == OverpoweredAchievements.OVERPOWERED3 || currentAchievement == OverpoweredAchievements.OVERPOWERED4;
            } else
                return achievement == OverpoweredAchievements.OVERPOWERED4 && currentAchievement == OverpoweredAchievements.OVERPOWERED4;
        }
        return false;
    }

    public String getAchievementName(final OverpoweredAchievements achievement) {
        final String data = " ";
        if (achievement == OverpoweredAchievements.OVERPOWERED4) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Overpowered-IV").replaceAll("&", "§");
        }
        if (achievement == OverpoweredAchievements.OVERPOWERED3) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Overpowered-III").replaceAll("&", "§");
        }
        if (achievement == OverpoweredAchievements.OVERPOWERED2) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Overpowered-II").replaceAll("&", "§");
        }
        if (achievement == OverpoweredAchievements.OVERPOWERED1) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.Overpowered-I").replaceAll("&", "§");
        }
        return data;
    }

    public Integer getRequiredKills(final OverpoweredAchievements achievement) {
        if (achievement == OverpoweredAchievements.OVERPOWERED4) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Overpowered.Kills-For-IV");
        }
        if (achievement == OverpoweredAchievements.OVERPOWERED3) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Overpowered.Kills-For-III");
        }
        if (achievement == OverpoweredAchievements.OVERPOWERED2) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Overpowered.Kills-For-II");
        }
        if (achievement == OverpoweredAchievements.OVERPOWERED1) {
            return this.ctw.getConfigHandler().getInteger("Achievements.Overpowered.Kills-For-I");
        }
        return 0;
    }

    public enum OverpoweredAchievements {
        OVERPOWERED1("OVERPOWERED1", 0),
        OVERPOWERED2("OVERPOWERED2", 1),
        OVERPOWERED3("OVERPOWERED3", 2),
        OVERPOWERED4("OVERPOWERED4", 3);

        OverpoweredAchievements(final String s, final int n) {
        }
    }
}
