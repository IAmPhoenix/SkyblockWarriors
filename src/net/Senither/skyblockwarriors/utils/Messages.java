package net.Senither.skyblockwarriors.utils;

import net.Senither.skyblockwarriors.SkyblockWarriors;

public class Messages
{

    private SkyblockWarriors _plugin;

    public Messages(SkyblockWarriors plugin)
    {
        _plugin = plugin;
    }
    // Server messages
    public final String SERVER_WELCOME = "&3Welcome to Skyblock Warriors.";
    public final String SERVER_KICK_SHUTDOWN = "&6Server shutting down.";
    public final String SERVER_KICK_INPROGRESS = "&4Game in progress.";
    // Pre-Game messages
    public final String PREGAME_JOIN_REACHED = "&6Reached &5%d&6 players. Starting in &5%d&6 seconds!";
    public final String PREGAME_COUNTDOWN = "&6The game is starting in &5%s &6seconds!";
    public final String PREGAME_NOT_ENOUGH = "&4Not enough players. Cancelling countdown.";
    public final String PREGAME_PLAYER_JOIN = "&5%s&e joined! &5%d&e more player(s) are needed until the game starts.";
    public final String PREGAME_PLAYER_LEAVE = "&5%s&e Left the match. &5%d&e more player(s) are needed until the game starts.";
    // Player messages
    public final String PLAYER_BECOME_SPEC = "&eYou are out of lives! You have been set as a spectator. You can use the compass to return to the lobby.";
    // Start of battle messages
    public final String BATTLE_BEGIN = "&6The battle has begun.";
    public final String BATTLE_DEATH = "&5%s&e has fallen! &5%d&e more player(s) remain.";
    public final String BATTLE_DEATHVIP = "&5%s&e has died but has been given an extra life as a VIP.";
    public final String BATTLE_LEAVE = "&5%s&e has left the game! &5%d&e more player(s) remain.";
    // End game messages
    public final String POSTGAME_WINNER = "&5%s&6 is victorious!";

    public String format(String message, Object... args)
    {
        return _plugin.chatManager.colorize(String.format(message, args));
    }
}
