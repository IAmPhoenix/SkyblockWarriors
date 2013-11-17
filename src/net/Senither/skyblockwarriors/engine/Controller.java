package net.Senither.skyblockwarriors.engine;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import net.Senither.skyblockwarriors.SkyblockWarriors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Controller
{

    private SkyblockWarriors _plugin;

    public Controller(SkyblockWarriors plugin)
    {
        _plugin = plugin;

        /**
         * Game Stats
         *   0 : Waiting for the map to start
         *   1 : Game is in progress
         *   2 : Game is ending, Restarting
         */
        _plugin.gameState = 0;
    }

    public void teleportAllPlayers()
    {
        List<String> players = new ArrayList<String>();

        for (Player player : _plugin.getServer().getOnlinePlayers()) {
            players.add(player.getName());
        }

        int i = 0;

        for (String name : players) {
            Location loc = _plugin.spawnLocations.get(i);
            _plugin.playerSpawn.put(name, loc);

            setuPlayer(_plugin.getServer().getPlayer(name));

            _plugin.getServer().getPlayer(name).teleport(loc);

            i++;
        }
    }

    public void setuPlayer(Player player)
    {
        if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        player.getInventory().clear();

        player.setFlying(false);
        player.setAllowFlight(false);

        player.setHealth(20.0);
        player.setFoodLevel(20);

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 100));
    }

    public void setPlayerAsSpectator(String name)
    {
        Player player = Bukkit.getPlayer(name);

        // Check if the player logged off, rage quit? ;)
        if (player != null) {
            player.getInventory().clear();
            player.getInventory().setItem(4, _plugin.items.spectatorItem);
            _plugin.chatManager.sendMessage(player, _plugin.messages.format(_plugin.messages.PLAYER_BECOME_SPEC));
        }
    }

    public boolean isAllowed(Player player)
    {
        if ((player.getAllowFlight() == true)
                || _plugin.vaninshHandler.isVanished(player)
                || (_plugin.gameState == 0)
                || (_plugin.gameState == 2)
                || (_plugin.running == false)
                || (!_plugin.playerLives.containsKey(player.getName()))) {
            return false;
        }
        return true;
    }
}
