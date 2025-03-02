package net.craftersland.ctw.server.utils;

import net.craftersland.ctw.server.CTW;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

public class DebugUtil {

    private static final CTW PLUGIN = CTW.getPlugin(CTW.class);
    private static final boolean debug = PLUGIN.getConfig().getBoolean("General.Debug");
    public static void print(String message) {
        if (debug) {
            PLUGIN.getLogger().info(message);
        }
    }

    public static void error(String message) {
        if (debug) {
            PLUGIN.getLogger().severe(message);
        }
    }

    /**
     * Print a message in the console
     * as a debug message but using
     * color codes.
     *
     * @param message The message to print.
     */
    public static void printBukkit(String message) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    /**
     * Separator for debug messages.
     */
    public static void separator() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&m--------------------------------------------------"));
    }

    /**
     * Separator for debug messages.
     */
    public static void empty() {
        Bukkit.getConsoleSender().sendMessage("");
    }

    public static void debug(String @NotNull ... messages) {
        if (debug) {
            for (String message : messages) {
                printBukkit("&6[GMIP-CTW] &7" + message);
            }
        }
    }
}
