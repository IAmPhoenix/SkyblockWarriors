package net.Senither.skyblockwarriors.utils;

import java.util.logging.Logger;

import net.Senither.skyblockwarriors.SkyblockWarriors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatManager
{

    public final SkyblockWarriors _plugin;
    public final String pluginName;
    public final String logName;
    public final String prefix;
    public final ChatColor defaultChatColor = ChatColor.GRAY;
    private final Logger log = Logger.getLogger("Minecraft");

    public ChatManager(SkyblockWarriors plugin)
    {
        _plugin = plugin;
        pluginName = _plugin.getName();
        logName = "[" + pluginName + "] ";
        prefix = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_AQUA + pluginName + ChatColor.DARK_GRAY + "] ";
    }

    /**
     * Log some information to the console
     * 
     * @param message Log message 
     */
    public void LogInfo(String message)
    {
        log.info(logName + message);
    }

    /**
     * Log important information to the console
     * 
     * @param message Log message
     */
    public void LogSevere(String message)
    {
        log.severe(logName + message);
    }

    /**
     * Send a warning to the console
     * 
     * @param message Warning message
     */
    public void LogWarning(String message)
    {
        log.warning(logName + message);
    }

    /**
     * Send a debug message to the console
     * Should only be used for testing/debugging
     * 
     * @param message 
     */
    public void debugMessage(String message)
    {
        if (message == null) {
            message = "Husten, we got a problem!";
        }
        LogSevere("DEBUG MESSAGE : \n " + message);
    }

    /**
     * Print out the onEnable message
     */
    public void enableMessage()
    {
    }

    /**
     * Print out the onDisable message
     */
    public void disableMessage()
    {
        LogInfo("v" + _plugin.v + " disabled.");
    }

    /**
     * Send a message to a player or console
     * 
     * @param player Command Sender object (Console)
     * @param message Message to send
     */
    public void sendMessage(CommandSender player, String message)
    {
        player.sendMessage(defaultChatColor + colorize(message));
    }

    /**
     * Send a message to a player
     * 
     * @param player Player object
     * @param message Message to send
     */
    public void sendMessage(Player player, String message)
    {
        player.sendMessage(defaultChatColor + colorize(message));
    }

    /**
     * Broadcast a message to everyone on the server
     * This include the plugin prefix
     * 
     * @param message Broadcast Message
     */
    public void broadcastPluginMessage(String message)
    {
        Bukkit.broadcastMessage(prefix + defaultChatColor + colorize(message));
    }

    /**
     * Broadcast a message to everyone on the server
     * Without the prefix
     * 
     * @param message Broadcast Message
     */
    public void broadcastMessage(String message)
    {
        Bukkit.broadcastMessage(defaultChatColor + colorize(message));
    }

    /**
     * Sends a message if the player is missing the permission
     * 
     * @param player CommandSender object (Console)
     * @param permission Permission string
     */
    public void missingPermission(CommandSender player, String permission)
    {
        player.sendMessage(ChatColor.RED + "Influent permissions to execute this command.");
        player.sendMessage(ChatColor.RED + "You're missing the permission node " + ChatColor.ITALIC + permission);
    }

    /**
     * Sends a message if the player is missing the permission
     * 
     * @param player Player object
     * @param permission Permission string
     */
    public void missingPermission(Player player, String permission)
    {
        player.sendMessage(ChatColor.RED + "Influent permissions to execute this command.");
        player.sendMessage(ChatColor.RED + "You're missing the permission node " + ChatColor.ITALIC + permission);
    }

    /**
     * Log an Exception to the console as a warning
     * 
     * @param e Exception 
     */
    public void errorMessage(Exception e)
    {
        LogWarning("An error occurred " + e);
    }

    /**
     * Colorize a message
     * 
     * @param message The message
     */
    public String colorize(String message)
    {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * De-Colorize a message
     * 
     * @param message The message
     */
    public String decolorize(String message)
    {
        return ChatColor.stripColor(message);
    }

    /**
     * Split the chat
     * 
     * @param Player player object
     */
    public void splitChat(Player player)
    {
        player.sendMessage(ChatColor.DARK_GRAY + "=====================================================");
    }

    /**
     * Clear the chat
     * 
     * @param Player player object
     */
    public void clearChat(Player player)
    {
        for (int i = 0; i < 35; i++) {
            player.sendMessage("");
        }
    }
}
