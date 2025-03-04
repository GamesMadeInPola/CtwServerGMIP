package net.craftersland.ctw.server.game;

import net.craftersland.ctw.server.CTW;
import net.craftersland.ctw.server.database.CTWPlayer;
import net.craftersland.ctw.server.utils.StartupKit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameEngine implements Listener {
    private final CTW ctw;
    public GameStages gameStage;
    public String motd;
    private int countdown;

    public GameEngine(@NotNull CTW ctw) {
        this.ctw = ctw;
        this.countdown = 35;
        this.motd = formatColor(ctw.getLanguageHandler().getMessage("MOTD-Status.Loading"));
        this.gameStage = GameStages.LOADING;
        scheduleTasks();
    }

    private void scheduleTasks() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(ctw, () -> {
            if (gameStage == GameStages.COUNTDOWN) {
                countdownStage();
            } else if (gameStage == GameStages.RUNNING) {
                checkForWoolsPlaced();
                motd = formatColor(ctw.getLanguageHandler().getMessage("MOTD-Status.Status")
                        .replace("%MapName%", ctw.getMapHandler().currentMap));
            }
            ctw.getJoinMenu().menuUpdateTask();
            ctw.getRestartHandler().checkMemoryUsage();
        }, 20L, 20L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(ctw, () -> ctw.map = ctw.getMapHandler().currentMap, 20L, 1000L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(ctw, () -> {
            if (player.isOnline()) {
                setScoreboard(player);
            }
        }, 5L);
    }

    private void setScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("Stats", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.YELLOW + "Scoreboard");

        Score score = objective.getScore(ChatColor.GREEN + "Kills: ");
        score.setScore(0);

        player.setScoreboard(scoreboard);
    }

    private void checkForWoolsPlaced() {
        if (ctw.getWoolHandler().isRedPlaced() && ctw.getWoolHandler().isPinkPlaced()) {
            endGame(GameStages.COUNTDOWN, this::redTeamWonStage);
        } else if (ctw.getWoolHandler().isBluePlaced() && ctw.getWoolHandler().isCyanPlaced()) {
            endGame(GameStages.COUNTDOWN, this::blueTeamWonStage);
        }
    }

    private void endGame(GameStages nextStage, @NotNull Runnable victoryMethod) {
        gameStage = GameStages.IDLE;
        victoryMethod.run();
        gameStage = nextStage;
    }

    private void countdownStage() {
        try {
            if (countdown == 30) {
                ctw.getMapHandler().getNextMap();
                this.orderKills();
                checkForServerRestart();
                broadcastCountdown("CountdownStart");
            } else if (countdown == 20 || countdown == 10) {
                broadcastCountdown("CountdownProgress");
                if (countdown == 20) ctw.getMapHandler().loadNextMap();
            } else if (countdown <= 5 && countdown > 0) {
                broadcastCountdown("CountdownLast5sec");
            } else if (countdown == 0) {
                startGame();
            }
            countdown--;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkForServerRestart() {
        if (ctw.getConfigHandler().getInteger("Settings.ServerRestartAfterGamesPlayed") != 0
                && ctw.getMapHandler().getPlayedMaps() > ctw.getConfigHandler().getInteger("Settings.ServerRestartAfterGamesPlayed")) {
            ctw.getRestartHandler().serverStop();
        }
    }

    private void broadcastCountdown(String key) {
        String message = formatColor(ctw.getLanguageHandler().getMessage("ChatMessages." + key).replace("%countdown%", String.valueOf(countdown)));
        Bukkit.broadcastMessage(message);
    }

    private void startGame() {
        countdown = 35;
        gameStage = GameStages.RUNNING;
        ctw.getMessageUtils().broadcastTitleMessage(ctw.getLanguageHandler().getMessage("TitleMessages.CountdownOver.title"),
                ctw.getLanguageHandler().getMessage("TitleMessages.CountdownOver.subtitle"));
        ctw.getSoundHandler().broadcastLevelUpSound();
        ctw.map = ctw.getMapHandler().currentMap;
        setPlayerGameMode(GameMode.SURVIVAL);
        Bukkit.getOnlinePlayers().forEach(StartupKit::setUnbreakableArmor);
    }

    private void setPlayerGameMode(GameMode mode) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (ctw.getTeamHandler().isBlueTeam(p) || ctw.getTeamHandler().isRedTeam(p)) {
                p.setGameMode(mode);
            }
        });
    }

    private String formatColor(String message) {
        return message.replaceAll("&", "§");
    }

    public enum GameStages {
        LOADING, RUNNING, NEXTMAP, IDLE, COUNTDOWN
    }
}
