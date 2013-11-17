package net.Senither.skyblockwarriors.vanish;

import java.util.ArrayList;
import java.util.List;

import net.Senither.skyblockwarriors.SkyblockWarriors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VanishHandler
{

    private List<String> hiddenPlayers = new ArrayList<String>();
    private SkyblockWarriors _plugin;

    public VanishHandler(SkyblockWarriors plugin)
    {
        _plugin = plugin;
    }

    public boolean isVanished(Player player)
    {
        return hiddenPlayers.contains(player.getName());
    }

    public void vanishPlayer(Player player)
    {
        if (isVanished(player)) {
            return;
        }

        if (!hiddenPlayers.contains(player.getName())) {
            hiddenPlayers.add(player.getName());
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!isVanished(p)) {
                p.hidePlayer(player);
            }
        }
    }

    public void showPlayer(Player player)
    {
        hiddenPlayers.remove(player.getName());
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(player);
        }
    }

    public void showAllPlayers()
    {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isVanished(p)) {
                showPlayer(p);
            }
        }
    }

    public void setSpectator(Player player)
    {
        if (!hiddenPlayers.contains(player.getName())) {
            hiddenPlayers.add(player.getName());
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(player.getName())) {
                continue;
            }

            if (isVanished(p)) {
                player.showPlayer(p);
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!isVanished(p)) {
                p.hidePlayer(player);
            }
        }
    }
}
