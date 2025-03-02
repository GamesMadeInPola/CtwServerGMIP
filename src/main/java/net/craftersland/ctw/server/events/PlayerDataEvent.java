package net.craftersland.ctw.server.events;

import net.craftersland.ctw.server.CTW;
import net.craftersland.ctw.server.database.CTWPlayer;
import net.craftersland.ctw.server.utils.DebugUtil;
import net.craftersland.ctw.server.utils.SchedulerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PlayerDataEvent implements Listener {
    private final CTW ctw;

    public PlayerDataEvent(final CTW ctw) {
        this.ctw = ctw;
    }

    @EventHandler
    public void onLogin(final @NotNull PlayerLoginEvent event) {

        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;
        
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(this.ctw, () -> {
            Optional<CTWPlayer> ctwPlayer = ctw.getDataHandler().getCTWPlayer(player.getUniqueId());

            if (ctwPlayer.isEmpty()) {
                ctw.getLogger().severe("User not found in the database. Creating a new ctwPlayer for " + event.getPlayer().getName());
                ctwPlayer = Optional.of(new CTWPlayer(player.getUniqueId(), player.getName(), 0, 0, 0, 0, 0, 0, 0, 0));
            }

            ctwPlayer.get().setName(player.getName()); // Establecemos el nombre por si acaso :p
            ctw.getCTWPlayerRepository().save(player.getUniqueId(), ctwPlayer.get());
        });


        Inventory joinTeamMenu = this.ctw.getJoinMenu().JoinMenuGUI(player);

        this.ctw.getPlayerHandler().addSpectator(player);

        Bukkit.getScheduler().runTaskLater(this.ctw, () -> {
            this.ctw.getPlayerHandler().sendJoinMessage(player);
            this.ctw.getMessageUtils().sendTabTitleFooter(player);
            this.ctw.getKillStreakHandler().resetData(player);
            this.ctw.getNewScoreboardHandler().addPlayer(player);

            // Load achievements for player
            this.ctw.getWoolAchievementHandler().loadInitialAchievements(player);
            this.ctw.getShooterAchievementHandler().loadInitialAchievements(player);
            this.ctw.getMeleeAchievementHandler().loadInitialAchievements(player);
            this.ctw.getOverpoweredAchievementHandler().loadInitialAchievements(player);
            this.ctw.getDistanceAchievementHandler().loadInitialAchievements(player);
        }, 1L);
        
        Bukkit.getScheduler().runTaskLater(this.ctw, () -> player.openInventory(joinTeamMenu), 15L);
    }

    @EventHandler
    public void onQuit(final @NotNull PlayerQuitEvent event) {
        SchedulerUtil.doAsync(this.ctw, () -> {
            CTWPlayer ctwPlayer = this.ctw.getDataHandler().getCTWPlayer(event.getPlayer().getUniqueId()).orElse(null);

            if (ctwPlayer != null) {
                if (ctw.getDataHandler().saveCTWPlayer(ctwPlayer)){
                    ctw.getCTWPlayerRepository().remove(event.getPlayer().getUniqueId());
                    DebugUtil.printBukkit("Guardado correctamente el jugador " + event.getPlayer().getName());
                }
            }
        });
    }
}
