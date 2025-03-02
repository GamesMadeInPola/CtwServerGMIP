package net.craftersland.ctw.server;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.craftersland.ctw.server.database.CTWPlayer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PAPI extends PlaceholderExpansion {

    private final CTW plugin;

    public PAPI(CTW plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ctw";
    }

    @Override
    public @NotNull String getAuthor() {
        return "GMIP";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; //
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        if (player == null) {
            return "";
        }

        //General placeholders

        if (params.equalsIgnoreCase("map")) {
            return plugin.getMapHandler().currentMap;
        }

        if (params.equalsIgnoreCase("red_victories")) {
            return plugin.getTeamVictoryHandler().getRedVictoryScore() + "";
        }

        if (params.equalsIgnoreCase("blue_victories")) {
            return plugin.getTeamVictoryHandler().getBlueVictoryScore() + "";
        }

        if (params.equalsIgnoreCase("red_players")) {
            return plugin.getTeamHandler().redTeamCopy().size() + "";
        }

        if (params.equalsIgnoreCase("blue_players")) {
            return plugin.getTeamHandler().blueTeamCopy().size() + "";
        }

        // Player specific placeholders

        if (!player.isOnline()) {
            return "Offline";
        }

        CTWPlayer ctwPlayer = plugin.getCTWPlayerRepository().get(player.getUniqueId());

        if (params.equalsIgnoreCase("player_total_kills")) {
            return ctwPlayer.getTotalKills() + "";
        }

        if (params.equalsIgnoreCase("player_defense_kills")) {
            return ctwPlayer.getDefenseKills() + "";
        }

        if (params.equalsIgnoreCase("player_melee_kills")) {
            return ctwPlayer.getMeleeKills() + "";
        }

        if (params.equalsIgnoreCase("player_bow_kills")) {
            return ctwPlayer.getBowKills() + "";
        }

        if (params.equalsIgnoreCase("player_match_total_kills")) {
            return ctwPlayer.getMatchTotalKills() + "";
        }

        if (params.equalsIgnoreCase("player_match_defense_kills")) {
            return ctwPlayer.getMatchDefenseKills() + "";
        }

        if (params.equalsIgnoreCase("player_match_melee_kills")) {
            return ctwPlayer.getMatchMeleeKills() + "";
        }

        if (params.equalsIgnoreCase("player_match_bow_kills")) {
            return ctwPlayer.getMatchBowKills() + "";
        }

        return "...";
    }
}
