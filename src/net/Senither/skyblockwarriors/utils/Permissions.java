package net.Senither.skyblockwarriors.utils;

import net.Senither.skyblockwarriors.SkyblockWarriors;

public class Permissions
{

    @SuppressWarnings("unused")
    private SkyblockWarriors _plugin;
    private String mainPermission = "skyblockwarriors.";

    public Permissions(SkyblockWarriors plugin)
    {
        _plugin = plugin;
    }
    public final String PLAYER_RANK_VIP = mainPermission + "rank.vip";
    public final String PLAYER_RANK_MOD = mainPermission + "rank.mod";
    public final String PLAYER_RANK_ADMIN = mainPermission + "rank.admin";
}
