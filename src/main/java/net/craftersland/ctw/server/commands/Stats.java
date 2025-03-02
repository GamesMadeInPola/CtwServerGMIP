package net.craftersland.ctw.server.commands;

import net.craftersland.ctw.server.CTW;
import net.craftersland.ctw.server.database.CTWPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Stats implements CommandExecutor {
    private final CTW ctw;

    public Stats(final CTW ctw) {
        this.ctw = ctw;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final @NotNull String cmdlabel, final String[] args) {
        if (!cmdlabel.equalsIgnoreCase("stats")) {
            return false;
        }
        if (args.length != 0) {
            sender.sendMessage(this.ctw.getLanguageHandler().getMessage("ChatMessages.StatsUsage"));
            return true;
        }
        if (sender instanceof Player) {
            final Player p = (Player) sender;

            this.sendStats(p);
            return true;
        }
        sender.sendMessage(ChatColor.RED + "You can't run this command from server console!");
        return true;
    }

    private void sendStats(final Player p) {
        CTWPlayer ctwPlayer = this.ctw.getCTWPlayerRepository().get(p.getUniqueId());
        final int score = ctwPlayer.getScore();
        final int totalKills = ctwPlayer.getTotalKills();
        final int meleeKills = ctwPlayer.getMeleeKills();
        final int bowKills = ctwPlayer.getBowKills();
        final int distance = ctwPlayer.getBowDistanceKill();
        final int woolsPickup = ctwPlayer.getWoolPickups();
        final int woolsPlaced = ctwPlayer.getWoolPlacements();
        final List<String> rawMsg = new ArrayList<String>(this.ctw.getLanguageHandler().getMessageList("ChatStats"));
        final List<String> msg = new ArrayList<String>();
        for (final String s : rawMsg) {
            final String ss = s.replace("%score%", new StringBuilder().append(score).toString());
            final String sss = ss.replace("%meleeKills%", new StringBuilder().append(meleeKills).toString());
            final String ssss = sss.replace("%bowKills%", new StringBuilder().append(bowKills).toString());
            final String sssss = ssss.replace("%totalKills%", new StringBuilder().append(totalKills).toString());
            final String ssssss = sssss.replace("%distance%", new StringBuilder().append(distance).toString());
            final String sssssss = ssssss.replace("%wools%", new StringBuilder().append(woolsPickup).toString());
            final String woolsPlacedString = sssssss.replace("%wools%", new StringBuilder().append(woolsPlaced).toString());
            final String ssssssss = woolsPlacedString.replaceAll("&", "§");
            msg.add(ssssssss);
        }
        final String woolAchievement = this.ctw.getWoolAchievementHandler().getCurrentAchievement(p);
        if (woolAchievement != null) {
            final String s2 = "   &f" + woolAchievement;
            msg.add(s2.replaceAll("&", "§"));
        }
        final String shooterAchievement = this.ctw.getShooterAchievementHandler().getCurrentAchievement(p);
        if (shooterAchievement != null) {
            final String s3 = "   &f" + shooterAchievement;
            msg.add(s3.replaceAll("&", "§"));
        }
        final String meleeAchievement = this.ctw.getMeleeAchievementHandler().getCurrentAchievement(p);
        if (meleeAchievement != null) {
            final String s4 = "   &f" + meleeAchievement;
            msg.add(s4.replaceAll("&", "§"));
        }
        final String overpoweredAchievement = this.ctw.getOverpoweredAchievementHandler().getCurrentAchievement(p);
        if (overpoweredAchievement != null) {
            final String s5 = "   &f" + overpoweredAchievement;
            msg.add(s5.replaceAll("&", "§"));
        }
        final String sniperAchievement = this.ctw.getDistanceAchievementHandler().getCurrentAchievement(p);
        if (sniperAchievement != null) {
            final String s6 = "   &f" + sniperAchievement;
            msg.add(s6.replaceAll("&", "§"));
        }
        msg.add(" ");
        String[] data = new String[msg.size()];
        data = msg.toArray(data);
        p.sendMessage(data);
        this.ctw.getSoundHandler().sendConfirmSound(p.getLocation(), p);
    }
}
