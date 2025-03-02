package net.craftersland.ctw.server.utils;

import net.craftersland.ctw.server.CTW;
import net.craftersland.ctw.server.database.CTWPlayer;
import net.craftersland.ctw.server.game.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MessageUtils {
    private final CTW ctw;

    public MessageUtils(final CTW ctw) {
        this.ctw = ctw;
    }

    public void broadcastTitleMessage(final String title, final String subtitle) {
        Bukkit.getScheduler().runTask(this.ctw, () -> Bukkit.getOnlinePlayers().forEach(p -> {
            CTW.tm.sendTitle(p, title);
            CTW.tm.sendSubtitle(p, subtitle);
        }));
    }

    public void sendTitleMessage(final String title, final String subtitle, final Player p) {
        Bukkit.getScheduler().runTask(this.ctw, () -> {
            CTW.tm.sendTitle(p, title);
            CTW.tm.sendSubtitle(p, subtitle);
        });
    }

    public void broadcastActionBarMessage(final String msg) {
        Bukkit.getScheduler().runTask(this.ctw, () -> Bukkit.getOnlinePlayers().forEach(p -> CTW.tm.sendActionbar(p, msg)));
    }

    public void sendActionBarMessage(final String msg, final Player p) {
        Bukkit.getScheduler().runTask(this.ctw, () -> CTW.tm.sendActionbar(p, msg));
    }

    public String getTeamColor(final @NotNull Player p) {
        String name = p.getName();
        final TeamHandler.Teams team = this.ctw.getTeamHandler().getTeam(p);
        if (team == TeamHandler.Teams.RED) {
            name = ChatColor.RED + name;
        } else if (team == TeamHandler.Teams.BLUE) {
            name = ChatColor.BLUE + name;
        }
        return name;
    }

    public String getTeamColorBolded(final @NotNull Player p) {
        String name = p.getName();
        final TeamHandler.Teams team = this.ctw.getTeamHandler().getTeam(p);
        if (team == TeamHandler.Teams.RED) {
            name = String.valueOf(ChatColor.RED) + ChatColor.BOLD + name;
        } else if (team == TeamHandler.Teams.BLUE) {
            name = String.valueOf(ChatColor.BLUE) + ChatColor.BOLD + name;
        }
        return name;
    }

    public void broadcastTabTitleFooter() {
        Bukkit.getScheduler().runTask(this.ctw, new Runnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(p -> CTW.tm.setHeaderAndFooter(p, MessageUtils.this.ctw.getTabList().getTabTitle(), MessageUtils.this.ctw.getTabList().getTabFooter()));
            }
        });
    }

    public void sendTabTitleFooter(final Player p) {
        Bukkit.getScheduler().runTask(this.ctw, new Runnable() {
            @Override
            public void run() {
                CTW.tm.setHeaderAndFooter(p, MessageUtils.this.ctw.getTabList().getTabTitle(), MessageUtils.this.ctw.getTabList().getTabFooter());
            }
        });
    }

    public void sendScoreMessage(final Player player, final String score, final Integer coins) {
        CTWPlayer ctwPlayer = this.ctw.getCTWPlayerRepository().get(player.getUniqueId());
        String msg = this.ctw.getLanguageHandler().getMessage("ChatMessages.ScoreMessage").replaceAll("%points%", score);
        msg = msg.replaceAll("%score%", String.valueOf(ctwPlayer.getScore()));
        if (coins != null) {
            if (player.hasPermission("CTW.3xCoinMultiplier")) {
                final int coin = (int) (coins + (coins * 0.03));
                String cMsg = this.ctw.getLanguageHandler().getMessage("ChatMessages.Coins3xMultiplier").replaceAll("%coins%", String.valueOf(coin));
                cMsg = cMsg.replaceAll("%balance%", this.ctw.getEconomyHandler().getCoins(player).toString());
                msg = msg + cMsg;
            } else if (player.hasPermission("CTW.2xCoinMultiplier")) {
                final int coin = (int) (coins + (coins * 0.02));
                String cMsg = this.ctw.getLanguageHandler().getMessage("ChatMessages.Coins2xMultiplier").replaceAll("%coins%", String.valueOf(coin));
                cMsg = cMsg.replaceAll("%balance%", this.ctw.getEconomyHandler().getCoins(player).toString());
                msg = msg + cMsg;
            } else {
                String cMsg2 = this.ctw.getLanguageHandler().getMessage("ChatMessages.CoinsNoMultiplier").replaceAll("%coins%", String.valueOf(coins));
                cMsg2 = cMsg2.replaceAll("%balance%", this.ctw.getEconomyHandler().getCoins(player).toString());
                msg = msg + cMsg2;
            }
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public void broadcastGameStats() {
        final List<String> mList = new ArrayList<String>();
        for (String s : this.ctw.getLanguageHandler().getMessageList("ChatMessages.GameStats")) {
            s = s.replaceAll("&", "§");
            s = s.replaceAll("%RedTeamVictory%", this.ctw.getTeamVictoryHandler().getRedVictoryScore().toString());
            s = s.replaceAll("%BlueTeamVictory%", this.ctw.getTeamVictoryHandler().getBlueVictoryScore().toString());
            s = s.replaceAll("%RedTeamScore%", this.ctw.getTeamScoreHandler().getRedScore().toString());
            s = s.replaceAll("%BlueTeamScore%", this.ctw.getTeamVictoryHandler().getBlueVictoryScore().toString());
            s = s.replaceAll("%RedTeamKills%", this.ctw.getTeamKillsHandler().getRedKills().toString());
            s = s.replaceAll("%BlueTeamKills%", this.ctw.getTeamKillsHandler().getBlueKills().toString());
            s = s.replaceAll("%RedTeamDamage%", this.ctw.getTeamDamageHandler().getRedDmg().toString());
            s = s.replaceAll("%BlueTeamDamage%", this.ctw.getTeamDamageHandler().getBlueDmg().toString());
            s = s.replaceAll("%RedTeamWools%", this.ctw.getTeamWoolsCaptured().getRedCaptured().toString());
            s = s.replaceAll("%BlueTeamWools%", this.ctw.getTeamWoolsCaptured().getBlueCaptured().toString());
            mList.add(s);
        }
        for (final String msg : mList) {
            Bukkit.broadcastMessage(msg);
        }
        mList.clear();
    }
}
