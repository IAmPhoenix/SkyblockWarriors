package net.Senither.skyblockwarriors.utils;

import net.Senither.skyblockwarriors.SkyblockWarriors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardHandler
{

    protected Team playerTeam;
    protected SkyblockWarriors _plugin;
    protected Scoreboard board;

    /**
     * Create the scoreboard objects, set the default values
     */
    public void ScoreboardHandler(SkyblockWarriors plugin)
    {
        _plugin = plugin;

        // Create Sidebar
        board = Bukkit.getScoreboardManager().getNewScoreboard();

        // Register the scoreboard
        Objective obj = board.registerNewObjective("Players", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(ChatColor.GOLD + "Players");

        playerTeam = board.registerNewTeam("player");
        playerTeam.setAllowFriendlyFire(true);
        playerTeam.setCanSeeFriendlyInvisibles(false);
    }

    public void setPlayer(Player p)
    {
        p.setScoreboard(board);
    }

    public void updatePlayers()
    {
        int count = 1;

        for (Player p : Bukkit.getOnlinePlayers()) {

            Scoreboard b = p.getScoreboard();

            if (b != null) {
                Objective o = b.getObjective(DisplaySlot.SIDEBAR);
                if (o != null) {
                    String name = p.getName();

                    playerTeam.addPlayer(p);

                    playerTeam.addPlayer(Bukkit.getOfflinePlayer(ChatColor.RED + name));
                    Score player = o.getScore(Bukkit.getOfflinePlayer(ChatColor.RED + name));
                    player.setScore(count);

                    CraftPlayer cp = (CraftPlayer) p;

                    if (cp != null) {
                        cp.setScoreboard(b);
                    }
                }
            }
        }
    }
}
