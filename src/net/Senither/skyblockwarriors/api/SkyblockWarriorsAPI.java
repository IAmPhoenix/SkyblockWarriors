package net.Senither.skyblockwarriors.api;

import java.util.HashMap;
import net.Senither.skyblockwarriors.SkyblockWarriors;

public class SkyblockWarriorsAPI
{

    private SkyblockWarriors _plugin;

    public SkyblockWarriorsAPI(SkyblockWarriors plugin)
    {
        _plugin = plugin;
    }

    public boolean getSetup()
    {
        return _plugin.setup;
    }

    public HashMap<String, Integer> getPlayersAlive()
    {
        return _plugin.playerLives;
    }
}
