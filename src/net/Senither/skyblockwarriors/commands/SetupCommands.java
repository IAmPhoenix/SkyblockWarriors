package net.Senither.skyblockwarriors.commands;

import java.util.ArrayList;
import java.util.List;

import net.Senither.skyblockwarriors.SkyblockWarriors;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommands implements CommandExecutor
{

    private SkyblockWarriors _plugin;
    private int countdown = 10;

    public SetupCommands(SkyblockWarriors plugin)
    {
        _plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args)
    {
        /**
         * There are no need to check if we're using the /setup command as this class
         * only are able to take requests from that command, so we just skip that step.
         */
        // Check if the command was executed form the console
        if (!(sender instanceof Player)) {
            _plugin.chatManager.sendMessage(sender, "You can't use the SETUP commands via the console");
            _plugin.chatManager.sendMessage(sender, "Please login to your account to setup the server.");
            return false;
        }

        Player player = (Player) sender;

        // Checks if the player is an admin
        if (!player.hasPermission(_plugin.permissions.PLAYER_RANK_ADMIN)) {
            _plugin.chatManager.missingPermission(player, _plugin.permissions.PLAYER_RANK_ADMIN);
            return false;
        }

        // Check if the player use the help command
        if ((args.length == 0) || args[0].equalsIgnoreCase("help")) {
            // Clearing chat
            _plugin.chatManager.clearChat(player);

            _plugin.chatManager.splitChat(player);

            /**
             * This message is dynamic, this means that it will change depending on what setup 
             * stage the server is set in.
             * 
             * 0 - Setting up spawn locations for the players when the game begins
             * 1 - Spectator spawns and join spawn while the game is in WAITING mode.
             * 2 - Setting the lobby instance name for lilypad to use later on.
             */
            _plugin.chatManager.sendMessage(player, " &7You're on step #" + (_plugin.setupStage + 1));
            _plugin.chatManager.sendMessage(player, "");

            if (_plugin.setupStage == 0) {
                _plugin.chatManager.sendMessage(player, " &7Use the following command to set the spawn points for players when the game begins. Depending on many spawns you set, you will also set the player limit for this instance.");
                _plugin.chatManager.sendMessage(player, " &7&o/setup setspawn");
                _plugin.chatManager.sendMessage(player, " &7When you're done setting up the spawns, use the following command to go to the next stage.");
                _plugin.chatManager.sendMessage(player, " &7&o/setup finish");
            } else if (_plugin.setupStage == 1) {
                _plugin.chatManager.sendMessage(player, " &7Use the following command to set the spawn and spectator spawn");
                _plugin.chatManager.sendMessage(player, " &7&o/setup setspawn");
                _plugin.chatManager.sendMessage(player, " &7When you're done setting up the spawn, use the following command to go to the next stage.");
                _plugin.chatManager.sendMessage(player, " &7&o/setup finish");
            } else {
                _plugin.chatManager.sendMessage(player, " &7Use the following command to set the lobby instance name for Lilypad to use. This will be used to teleport players back upon death and when the game ends.");
                _plugin.chatManager.sendMessage(player, " &7&o/setup lilypad <name>");
            }

            _plugin.chatManager.splitChat(player);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("setspawn")) {
                /**
                 * Set spawn command, this should handle the normal and spectator spawn.
                 */
                if (_plugin.setupStage == 0) {
                    // normal spawns
                    _plugin.spawnLocations.add(player.getLocation());
                    _plugin.chatManager.sendMessage(player, "&bSpawn set for your curret location!");
                } else if (_plugin.setupStage == 1) {
                    // spectator spawn
                    // Setting up the spectator spawn

                    Location loc = player.getLocation();

                    _plugin.spawnConfig.getConfig().set("spectatorSpawn.x", loc.getX());
                    _plugin.spawnConfig.getConfig().set("spectatorSpawn.y", loc.getY());
                    _plugin.spawnConfig.getConfig().set("spectatorSpawn.z", loc.getZ());
                    _plugin.spawnConfig.getConfig().set("spectatorSpawn.pitch", loc.getPitch());
                    _plugin.spawnConfig.getConfig().set("spectatorSpawn.yaw", loc.getYaw());
                    _plugin.spawnConfig.getConfig().set("spectatorSpawn.world", loc.getWorld().getName());

                    _plugin.spawnConfig.saveConfig();

                    _plugin.chatManager.sendMessage(player, "&bSpectator spawn have been set for your curret location!");
                }
            } else if (args[0].equalsIgnoreCase("finish")) {
                /**
                 * Load configs and save data to jump to the next setup stage.
                 */
                if (_plugin.setupStage == 0) {
                    // normal spawns
                    if (_plugin.spawnLocations.size() == 0) {
                        _plugin.chatManager.sendMessage(player, "&cPlease set atleast two spawns first!");
                        return false;
                    }

                    List<String> spawnNames = new ArrayList<String>();
                    int counter = 1;

                    // Setup the spawns
                    for (Location l : _plugin.spawnLocations) {
                        String spawn = "spawn-" + counter;
                        spawnNames.add(spawn);

                        _plugin.spawnConfig.getConfig().set("spawn." + spawn + ".x", l.getWorld().getName());

                        _plugin.spawnConfig.getConfig().set("spawn." + spawn + ".x", l.getX());
                        _plugin.spawnConfig.getConfig().set("spawn." + spawn + ".y", l.getY());
                        _plugin.spawnConfig.getConfig().set("spawn." + spawn + ".z", l.getZ());
                        _plugin.spawnConfig.getConfig().set("spawn." + spawn + ".pitch", l.getPitch());
                        _plugin.spawnConfig.getConfig().set("spawn." + spawn + ".yaw", l.getYaw());
                        _plugin.spawnConfig.getConfig().set("spawn." + spawn + ".world", l.getWorld().getName());

                        counter++;
                    }

                    _plugin.spawnConfig.getConfig().set("spawns", spawnNames);

                    _plugin.spawnConfig.saveConfig();

                    _plugin.setupStage++;

                    player.chat("/setup");
                } else if (_plugin.setupStage == 1) {
                    // spectator spawn
                    _plugin.setupStage++;
                    player.chat("/setup");
                }

            } else if (args[0].equalsIgnoreCase("lilypad")) {
                _plugin.chatManager.sendMessage(player, "&cPlease follow this command by an argument, would be nice if it was a lilypad instance that works as a hub.");
            } else {
                unknowCommand(player);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("lilypad")) {
                String lobby = args[1];

                _plugin.getConfig().set("lobby", lobby);
                _plugin.getConfig().set("arena-setup", true);

                _plugin.saveConfig();

                _plugin.chatManager.sendMessage(player, "&aAwesome! We're done!");
                _plugin.chatManager.sendMessage(player, "&aThe server will auto restart in ten seconds.. In the mean time, you can wait in the lobby you just created ;)");

                final Player p = player;
                _plugin.getServer().getScheduler().scheduleSyncRepeatingTask(_plugin, new Runnable()
                {
                    public void run()
                    {
                        switch (countdown) {
                            case 2:
                                _plugin.lilypad.movePlayer(p);
                                break;
                            case 0:
                                _plugin.getServer().shutdown();
                                break;
                        }
                        countdown--;
                    }
                }, 20, 20);
            } else {
                unknowCommand(player);
            }
        } else {
            unknowCommand(player);
        }
        return false;
    }

    public void unknowCommand(Player player)
    {
        _plugin.chatManager.sendMessage(player, "&cUnknow argument, use &o/setup help &r&cto see the help menu.");
    }
}
