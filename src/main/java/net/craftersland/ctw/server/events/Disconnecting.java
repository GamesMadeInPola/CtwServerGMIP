package net.craftersland.ctw.server.events;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.craftersland.ctw.server.CTW;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class Disconnecting implements Listener {
    private final CTW ctw;

    public Disconnecting(final CTW ctw) {
        this.ctw = ctw;
    }

    @EventHandler
    public void onDisconnect(final PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(this.ctw, () -> {
            final Player p = event.getPlayer();

            ctw.playerProjectile.remove(p.getUniqueId());

            this.ctw.getTeamHandler().removeOnDisconnect(p);
            this.ctw.getNewScoreboardHandler().removePlayer(p);
            //this.ctw.getNewScoreboardHandler().removeFromTeams(p);
            this.ctw.getWoolHandler().removeBlueTakenByPlayer(p);
            this.ctw.getWoolHandler().removeCyanTakenByPlayer(p);
            this.ctw.getWoolHandler().removePinkTakenByPlayer(p);
            this.ctw.getWoolHandler().removeRedTakenByPlayer(p);
            //this.ctw.getPlayerScoreHandler().saveScoreToDatabase(p);
            //this.ctw.getPlayerScoreHandler().removeEffect(p);
            //this.ctw.getPlayerKillsHandler().saveKillsToDatabasse(p);
            //this.ctw.getPlayerWoolsPlacedHandler().saveDataToDatabasse(p);
            //this.ctw.getPlayerBowDistanceKillHandler().saveDataToDatabasse(p);
            this.ctw.getWoolAchievementHandler().removeOnDisconnect(p);
            this.ctw.getShooterAchievementHandler().removeOnDisconnect(p);
            this.ctw.getMeleeAchievementHandler().removeOnDisconnect(p);
            this.ctw.getOverpoweredAchievementHandler().removeOnDisconnect(p);
            this.ctw.getDistanceAchievementHandler().removeOnDisconnect(p);
            this.ctw.getKillStreakHandler().removeOnDisconnect(p);
            this.ctw.getKitHandler().removeInventory(p);
            this.ctw.getLastDamageHandler().removeData(p);
            this.ctw.getEffectUtils().removeEffectsOnDisconnect(p);

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(this.ctw.getLobbyServersHandler().getLobbyToTeleport());
            p.sendPluginMessage(this.ctw, "BungeeCord", out.toByteArray());

        });
    }
}
