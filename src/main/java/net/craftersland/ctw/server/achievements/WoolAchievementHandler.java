package net.craftersland.ctw.server.achievements;

import net.craftersland.ctw.server.CTW;
import net.craftersland.ctw.server.database.CTWPlayer;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WoolAchievementHandler {
    private final CTW ctw;
    private final Map<Player, WoolAchievements> achievements;

    public WoolAchievementHandler(final CTW ctw) {
        this.achievements = new HashMap<Player, WoolAchievements>();
        this.ctw = ctw;
    }

    public void loadInitialAchievements(final @NotNull Player p) {
        CTWPlayer ctwPlayer = this.ctw.getCTWPlayerRepository().get(p.getUniqueId());
        final int woolsPlaced = ctwPlayer.getWoolPlacements();
        if (woolsPlaced >= this.ctw.getConfigHandler().getInteger("Achievements.WoolMaster.Wools-For-IV")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, WoolAchievements.WOOLMASTER4);
            }
        } else if (woolsPlaced >= this.ctw.getConfigHandler().getInteger("Achievements.WoolMaster.Wools-For-III")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, WoolAchievements.WOOLMASTER3);
            }
        } else if (woolsPlaced >= this.ctw.getConfigHandler().getInteger("Achievements.WoolMaster.Wools-For-II")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, WoolAchievements.WOOLMASTER2);
            }
        } else if (woolsPlaced >= this.ctw.getConfigHandler().getInteger("Achievements.WoolMaster.Wools-For-I")) {
            if (!this.achievements.containsKey(p)) {
                this.achievements.put(p, WoolAchievements.WOOLMASTER1);
            }
        } else if (!this.achievements.containsKey(p)) {
            this.achievements.put(p, null);
        }
    }

    public void removeOnDisconnect(final Player p) {
        this.achievements.remove(p);
    }

    public void checkForAchievements(final Player p) {
        CTWPlayer ctwPlayer = this.ctw.getCTWPlayerRepository().get(p.getUniqueId());
        final int woolsPlaced = ctwPlayer.getWoolPlacements();
        final WoolAchievements currentAcievement = this.achievements.get(p);
        if (woolsPlaced >= this.ctw.getConfigHandler().getInteger("Achievements.WoolMaster.Wools-For-IV")) {
            if (currentAcievement != WoolAchievements.WOOLMASTER4) {
                this.awardAchievement(p, WoolAchievements.WOOLMASTER4);
            }
        } else if (woolsPlaced >= this.ctw.getConfigHandler().getInteger("Achievements.WoolMaster.Wools-For-III")) {
            if (currentAcievement != WoolAchievements.WOOLMASTER3) {
                this.awardAchievement(p, WoolAchievements.WOOLMASTER3);
            }
        } else if (woolsPlaced >= this.ctw.getConfigHandler().getInteger("Achievements.WoolMaster.Wools-For-II")) {
            if (currentAcievement != WoolAchievements.WOOLMASTER2) {
                this.awardAchievement(p, WoolAchievements.WOOLMASTER2);
            }
        } else if (woolsPlaced >= this.ctw.getConfigHandler().getInteger("Achievements.WoolMaster.Wools-For-I") && currentAcievement != WoolAchievements.WOOLMASTER1) {
            this.awardAchievement(p, WoolAchievements.WOOLMASTER1);
        }
    }

    private void awardAchievement(final Player p, final WoolAchievements achievement) {
        String msg = "";
        if (achievement == WoolAchievements.WOOLMASTER4) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.WoolMaster-IV");
        } else if (achievement == WoolAchievements.WOOLMASTER3) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.WoolMaster-III");
        } else if (achievement == WoolAchievements.WOOLMASTER2) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.WoolMaster-II");
        } else if (achievement == WoolAchievements.WOOLMASTER1) {
            msg = this.ctw.getLanguageHandler().getMessage("Achievements.WoolMaster-I");
        }
        this.achievements.put(p, achievement);
        final String chatMsg = this.ctw.getLanguageHandler().getMessage("ChatMessages.AwardAchievement").replace("%AchievementName%", msg);
        final String title = this.ctw.getLanguageHandler().getMessage("TitleMessages.AwardAchievement.title").replace("%AchievementName%", msg);
        final String subtitle = this.ctw.getLanguageHandler().getMessage("TitleMessages.AwardAchievement.subtitle").replace("%AchievementName%", msg);
        p.awardAchievement(Achievement.KILL_WITHER);
        this.ctw.getSoundHandler().sendCompleteSound(p.getLocation(), p);
        p.sendMessage(chatMsg.replaceAll("&", "§"));
        this.ctw.getMessageUtils().sendTitleMessage(title.replaceAll("&", "§"), subtitle.replaceAll("&", "§"), p);
        Bukkit.getScheduler().runTaskLaterAsynchronously(this.ctw, new Runnable() {
            @Override
            public void run() {
                p.removeAchievement(Achievement.KILL_WITHER);
            }
        }, 20L);
    }

    public String getCurrentAchievement(final Player p) {
        final String data = null;
        final WoolAchievements currentAchievement = this.achievements.get(p);
        if (currentAchievement != null) {
            if (currentAchievement == WoolAchievements.WOOLMASTER4) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.WoolMaster-IV").replaceAll("&", "§");
            }
            if (currentAchievement == WoolAchievements.WOOLMASTER3) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.WoolMaster-III").replaceAll("&", "§");
            }
            if (currentAchievement == WoolAchievements.WOOLMASTER2) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.WoolMaster-II").replaceAll("&", "§");
            }
            if (currentAchievement == WoolAchievements.WOOLMASTER1) {
                return this.ctw.getLanguageHandler().getMessage("Achievements.WoolMaster-I").replaceAll("&", "§");
            }
        }
        return data;
    }

    public boolean hasAchievement(final Player p, final WoolAchievements achievement) {
        if (this.achievements.containsKey(p)) {
            final WoolAchievements currentAchievement = this.achievements.get(p);
            if (achievement == WoolAchievements.WOOLMASTER1) {
                return currentAchievement == WoolAchievements.WOOLMASTER1 || currentAchievement == WoolAchievements.WOOLMASTER2 || currentAchievement == WoolAchievements.WOOLMASTER3 || currentAchievement == WoolAchievements.WOOLMASTER4;
            } else if (achievement == WoolAchievements.WOOLMASTER2) {
                return currentAchievement == WoolAchievements.WOOLMASTER2 || currentAchievement == WoolAchievements.WOOLMASTER3 || currentAchievement == WoolAchievements.WOOLMASTER4;
            } else if (achievement == WoolAchievements.WOOLMASTER3) {
                return currentAchievement == WoolAchievements.WOOLMASTER3 || currentAchievement == WoolAchievements.WOOLMASTER4;
            } else
                return achievement == WoolAchievements.WOOLMASTER4 && currentAchievement == WoolAchievements.WOOLMASTER4;
        }
        return false;
    }

    public String getAchievementName(final WoolAchievements achievement) {
        final String data = " ";
        if (achievement == WoolAchievements.WOOLMASTER4) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.WoolMaster-IV").replaceAll("&", "§");
        }
        if (achievement == WoolAchievements.WOOLMASTER3) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.WoolMaster-III").replaceAll("&", "§");
        }
        if (achievement == WoolAchievements.WOOLMASTER2) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.WoolMaster-II").replaceAll("&", "§");
        }
        if (achievement == WoolAchievements.WOOLMASTER1) {
            return this.ctw.getLanguageHandler().getMessage("Achievements.WoolMaster-I").replaceAll("&", "§");
        }
        return data;
    }

    public Integer getRequiredKills(final WoolAchievements achievement) {
        if (achievement == WoolAchievements.WOOLMASTER4) {
            return this.ctw.getConfigHandler().getInteger("Achievements.WoolMaster.Wools-For-IV");
        }
        if (achievement == WoolAchievements.WOOLMASTER3) {
            return this.ctw.getConfigHandler().getInteger("Achievements.WoolMaster.Wools-For-III");
        }
        if (achievement == WoolAchievements.WOOLMASTER2) {
            return this.ctw.getConfigHandler().getInteger("Achievements.WoolMaster.Wools-For-II");
        }
        if (achievement == WoolAchievements.WOOLMASTER1) {
            return this.ctw.getConfigHandler().getInteger("Achievements.WoolMaster.Wools-For-I");
        }
        return 0;
    }

    public enum WoolAchievements {
        WOOLMASTER1("WOOLMASTER1", 0),
        WOOLMASTER2("WOOLMASTER2", 1),
        WOOLMASTER3("WOOLMASTER3", 2),
        WOOLMASTER4("WOOLMASTER4", 3);

        WoolAchievements(final String s, final int n) {
        }
    }
}
