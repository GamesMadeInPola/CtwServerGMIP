package net.craftersland.ctw.server.game;

import net.craftersland.ctw.server.CTW;
import net.craftersland.ctw.server.database.CTWPlayer;
import net.craftersland.ctw.server.utils.StartupKit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameEngine {
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

    private void redTeamWonStage() {
        declareVictory(TeamHandler.Teams.RED, "RedVictory");
    }

    private void blueTeamWonStage() {
        declareVictory(TeamHandler.Teams.BLUE, "BlueVictory");
    }

    private void declareVictory(TeamHandler.Teams team, String victoryKey) {
        try {
            List<Player> winners = (team == TeamHandler.Teams.RED) ? ctw.getTeamHandler().redTeamCopy() : ctw.getTeamHandler().blueTeamCopy();
            winners.forEach(p -> Bukkit.dispatchCommand(ctw.getServer().getConsoleSender(), "mysterydust add " + p.getName() + " 12"));
            setWonSpectators(winners);
            ctw.getSoundHandler().broadcastDragon();
            setPlayerGameMode(GameMode.SPECTATOR);
            ctw.getMessageUtils().broadcastGameStats();
            ctw.getEffectUtils().sendTextParticles(team);
            Bukkit.getScheduler().runTaskLaterAsynchronously(ctw, () -> ctw.getMessageUtils().broadcastTitleMessage(
                    formatColor(ctw.getLanguageHandler().getMessage("TitleMessages." + victoryKey + ".title")),
                    formatColor(ctw.getLanguageHandler().getMessage("TitleMessages." + victoryKey + ".subtitle"))), 30L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setWonSpectators(List<Player> players) {
        players.forEach(p -> ctw.getPlayerHandler().playerSetWonSpectator(p));
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

    public void orderKills() {

        Map<String, Integer> top = new HashMap<>();
        for (CTWPlayer ctwPlayer : ctw.getCTWPlayerRepository().get()) {
            if (ctwPlayer.getTotalKills() == 0) continue;
            top.put(ctwPlayer.getName(), ctwPlayer.getTotalKills());
        }

        List<Map.Entry<String, Integer>> top3 = top.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(3).toList();


        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player != null) {

                player.sendMessage(" ");
                player.sendMessage(" ");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8+--------------------------------------+"));
                player.sendMessage(" ");

                try {

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b       1ro Asesino &8- &7" + top3.get(0).getKey() + " &8- &e " + top3.get(0).getValue()));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a         2do Asesino &8- &7" + top3.get(1).getKey() + " &8- &e " + top3.get(1).getValue()));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&d           3er Asesino &8- &7" + top3.get(2).getKey() + " &8- &e " + top3.get(2).getValue()));

                } catch (IndexOutOfBoundsException e) {
                    ctw.getSendMessage().sendCenteredMessage(player, "&cNo han habido jugadores suficientes...");
                }

                player.sendMessage(" ");
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8+--------------------------------------+"));
            }
        });
    }

    public enum GameStages {
        LOADING, RUNNING, NEXTMAP, IDLE, COUNTDOWN
    }
}
