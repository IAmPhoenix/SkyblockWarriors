package net.Senither.skyblockwarriors.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSpectateEvent extends Event
{

    private static final HandlerList handlers = new HandlerList();
    private Player player;

    public PlayerSpectateEvent(Player player)
    {
        this.player = player;
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

    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
