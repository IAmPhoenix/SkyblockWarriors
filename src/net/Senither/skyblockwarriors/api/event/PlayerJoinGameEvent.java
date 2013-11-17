package net.Senither.skyblockwarriors.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJoinGameEvent extends Event
{

    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private int lives, tokens;
    private boolean vip;

    public PlayerJoinGameEvent(Player player, int lives, boolean vip, int tokens)
    {
        this.player = player;
        this.lives = lives;
        this.vip = vip;
        this.tokens = tokens;
    }

    /**
     * Returns the player object of the player who joined the game.
     * 
     * @return Player
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * Return the amount of lives the player has been given.
     * 
     * @return Integer
     */
    public int getLives()
    {
        return lives;
    }

    /**
     * Checks to see if the player is VIP or not.
     * Will return true if the player IS vip.
     * 
     * @return true | false
     */
    public boolean getPlayerIsVIP()
    {
        return vip;
    }

    /**
     * Return the amount of tokens the player has
     * 
     * @return Integer
     */
    public int getPlayerTokens()
    {
        return tokens;
    }

    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
