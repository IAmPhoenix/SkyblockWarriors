package net.Senither.skyblockwarriors.commands;

import net.Senither.skyblockwarriors.SkyblockWarriors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommands implements CommandExecutor
{

    private SkyblockWarriors _plugin;

    public GameCommands(SkyblockWarriors plugin)
    {
        _plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args)
    {
        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (commandLable.equalsIgnoreCase("lobby") || commandLable.equalsIgnoreCase("hub")) {
                // Check if the game is in progress and the player is a spectator or not here
                _plugin.lilypad.movePlayer((Player) sender);
            } else if (commandLable.equalsIgnoreCase("shop") || commandLable.equalsIgnoreCase("store")) {
                // Open the store for the player
                if (_plugin.gameState == 1) {
                    player.openInventory(_plugin.items.shopInventory);
                } else {
                    _plugin.chatManager.sendMessage(player, "&cYou can't open the shop right now :/ Sorry.");
                }
            } else if (commandLable.equalsIgnoreCase("token")
                    || commandLable.equalsIgnoreCase("tokens")
                    || commandLable.equalsIgnoreCase("balance")
                    || commandLable.equalsIgnoreCase("money")
                    || commandLable.equalsIgnoreCase("cash")
                    || commandLable.equalsIgnoreCase("t")
                    || commandLable.equalsIgnoreCase("bal")) {
                _plugin.chatManager.sendMessage(player, "&3You have &b" + _plugin.playerEco.get(player.getName()) + " &3tokens!");
                _plugin.chatManager.sendMessage(player, "&3Use &b/shop &3when the game starts to use them.");
            } else if(commandLable.equalsIgnoreCase("author")) {
                _plugin.chatManager.sendMessage(player, "&5SkyblockWarriors v" + _plugin.v + " &ewas developed by &5Senither");
            }
        }

        return false;
    }
}
