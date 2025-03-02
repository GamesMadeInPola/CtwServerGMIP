package net.craftersland.ctw.server.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SchedulerUtil {


    /**
     * Do a sync task.
     *
     * @param runnable the runnable.
     */
    public static void doSync(@NotNull JavaPlugin plugin, Runnable runnable) {
        plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    /**
     * Do sync a task which will be executed after delay.
     *
     * @param runnable The runnable.
     * @param delay    The delay in ticks.
     */
    public static void doSyncLater(@NotNull JavaPlugin plugin, Runnable runnable, long delay) {
        plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
    }

    /**
     * Do sync task which will be executed after delay
     * and will be repeated every period.
     *
     * @param runnable The runnable.
     * @param delay    The delay in ticks.
     * @param period   The period in ticks.
     */
    public static void doSyncRepeating(@NotNull JavaPlugin plugin, Runnable runnable, long delay, long period) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    /**
     * Do an async task.
     *
     * @param runnable the runnable.
     */
    public static void doAsync(@NotNull JavaPlugin plugin, Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    /**
     * Do an async task which will be executed after delay.
     *
     * @param runnable The runnable.
     * @param delay    The delay in ticks.
     */
    public static void doAsyncLater(@NotNull JavaPlugin plugin, Runnable runnable, long delay) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    /**
     * Do an async task which will be executed after delay
     * and will be repeated every period.
     *
     * @param runnable The runnable.
     * @param delay    The delay in ticks.
     * @param period   The period in ticks.
     */
    public static void doAsyncRepeating(@NotNull JavaPlugin plugin, Runnable runnable, long delay, long period) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }
}
