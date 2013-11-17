package net.Senither.skyblockwarriors.events;

import net.Senither.skyblockwarriors.SkyblockWarriors;
import net.Senither.skyblockwarriors.api.event.PlayerJoinGameEvent;
import net.Senither.skyblockwarriors.api.event.StageChangeEvent;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ServerListener implements Listener
{

    private SkyblockWarriors _plugin;

    public ServerListener(SkyblockWarriors plugin)
    {
        _plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e)
    {
        if (_plugin.setup == false) {
            Player player = e.getPlayer();

            if (!player.hasPermission(_plugin.permissions.PLAYER_RANK_ADMIN)) {
                String message = "&fThe server is currently in &cSETUP &fmode.\n"
                        + "&fYou need the following permission to join\n"
                        + "&6" + _plugin.permissions.PLAYER_RANK_ADMIN;

                e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                e.setKickMessage(_plugin.chatManager.colorize(message));

                _plugin.chatManager.LogInfo(player.getName() + " tried to join while the server is in SETUP mode!");
                return;
            }

            e.setResult(PlayerLoginEvent.Result.ALLOWED);
        } else {
            if ((_plugin.gameState != 0) || (_plugin.spawnLocations.size() <= _plugin.getServer().getOnlinePlayers().length)) {
                _plugin.lilypad.movePlayer(e.getPlayer());
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        e.setJoinMessage(null);

        if (_plugin.gameState == 0) {
            Player player = e.getPlayer();

            if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
                player.setGameMode(GameMode.SURVIVAL);
            }

            if (!player.isFlying()) {
                player.setAllowFlight(true);
                player.setFlying(true);
            }

            player.getInventory().clear();

            if (_plugin.setup == false) {

                player.getInventory().setItem(4, _plugin.items.spectatorItem);

                _plugin.chatManager.clearChat(player);

                _plugin.chatManager.splitChat(player);
                _plugin.chatManager.sendMessage(player, " &7Welcome, " + player.getName() + "!");
                _plugin.chatManager.sendMessage(player, " &7This server is running SkyblockWarriors v" + _plugin.v);
                _plugin.chatManager.sendMessage(player, "");
                _plugin.chatManager.sendMessage(player, " &7The server is currently in &cSETUP &7mode!");
                _plugin.chatManager.sendMessage(player, "");
                _plugin.chatManager.sendMessage(player, " &7Please use &o/setup help &r&7to see the help menu!");
                _plugin.chatManager.splitChat(player);
            } else {
                player.teleport(_plugin.spectatorSpawn);

                int tokens = _plugin.mysql.getPlayerBalance(player.getName());
                int lives = (player.hasPermission(_plugin.permissions.PLAYER_RANK_VIP)) ? 2 : 1;
                boolean vip = (lives == 2) ? true : false;

                // Call the PlayerJoinGameEvent
                _plugin.getServer().getPluginManager().callEvent(new PlayerJoinGameEvent(e.getPlayer(), lives, vip, tokens));

                _plugin.playerLives.put(player.getName(), lives);
                _plugin.playerEco.put(player.getName(), tokens);
                _plugin.playerNames.add(player.getName());

                int i = _plugin.spawnLocations.size() - _plugin.getServer().getOnlinePlayers().length;

                if (i == 0) {
                    _plugin.gameState = 1;

                    _plugin.getServer().getPluginManager().callEvent(new StageChangeEvent(1, 0));

                    _plugin.chatManager.broadcastMessage(_plugin.messages.format(_plugin.messages.PREGAME_JOIN_REACHED, _plugin.getServer().getOnlinePlayers().length, 5));

                    // Four second
                    _plugin.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            _plugin.chatManager.broadcastMessage(_plugin.messages.format(_plugin.messages.PREGAME_COUNTDOWN, 4));
                        }
                    }, 20);

                    // Three seconds
                    _plugin.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            _plugin.chatManager.broadcastMessage(_plugin.messages.format(_plugin.messages.PREGAME_COUNTDOWN, 3));
                        }
                    }, 40);

                    // Two seconds
                    _plugin.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            _plugin.chatManager.broadcastMessage(_plugin.messages.format(_plugin.messages.PREGAME_COUNTDOWN, 2));
                        }
                    }, 60);

                    // One second
                    _plugin.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            _plugin.chatManager.broadcastMessage(_plugin.messages.format(_plugin.messages.PREGAME_COUNTDOWN, 1));
                        }
                    }, 80);

                    // Starting
                    _plugin.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            _plugin.chatManager.broadcastMessage(_plugin.messages.format(_plugin.messages.BATTLE_BEGIN));
                            _plugin.controller.teleportAllPlayers();
                            _plugin.running = true;
                        }
                    }, 100);
                    return;
                } else {
                    _plugin.chatManager.broadcastMessage(_plugin.messages.format(_plugin.messages.PREGAME_PLAYER_JOIN, player.getName(), i));
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoinGame(PlayerJoinGameEvent e)
    {
        _plugin.chatManager.sendMessage(e.getPlayer(), "&3You have &b" + e.getPlayerTokens() + " &3tokens!");
        _plugin.chatManager.sendMessage(e.getPlayer(), "&3Use &b/shop &3when the game starts to use them.");
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e)
    {
        e.setQuitMessage(null);

        String name = e.getPlayer().getName();
        int i = _plugin.spawnLocations.size() - _plugin.getServer().getOnlinePlayers().length;

        if (_plugin.gameState == 0) {
            _plugin.chatManager.broadcastMessage(_plugin.messages.format(_plugin.messages.PREGAME_PLAYER_LEAVE, name, i));
        }

        if (_plugin.playerSpawn.containsKey(name)) {
            _plugin.playerSpawn.remove(name);
        }

        if (_plugin.playerEco.containsKey(name)) {
            _plugin.playerEco.remove(name);
        }

        if (_plugin.playerNames.contains(name)) {
            _plugin.playerNames.remove(name);
        }

        if (_plugin.playerLives.containsKey(name)) {
            _plugin.playerLives.remove(name);

            if (_plugin.gameState == 1) {
                // Check for Win
                if (_plugin.playerNames.size() == 1) {
                    Player winner = _plugin.getServer().getPlayer(_plugin.playerNames.get(0));
                    _plugin.gameState = 2;
                    _plugin.getServer().getPluginManager().callEvent(new StageChangeEvent(2, 1));

                    _plugin.mysql.giveReward(winner.getName());
                    _plugin.chatManager.broadcastMessage(_plugin.messages.format(_plugin.messages.POSTGAME_WINNER, winner.getName()));

                    _plugin.vaninshHandler.showAllPlayers();

                    // Mves all the players to the hub after five seconds
                    _plugin.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
                    {
                        public void run()
                        {
                            _plugin.lilypad.moveAllPlayers();
                        }
                    }, 100);

                    // Auto restarts the server after six seconds
                    _plugin.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
                    {
                        public void run()
                        {
                            _plugin.getServer().shutdown();
                        }
                    }, 120);
                } else {
                    // This will be exeucted if there is more than one player left in the game
                    _plugin.chatManager.broadcastMessage(_plugin.messages.format(_plugin.messages.BATTLE_LEAVE, name, _plugin.playerNames.size()));
                }
            }
        }
    }
}
