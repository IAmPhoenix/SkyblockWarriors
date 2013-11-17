package net.Senither.skyblockwarriors.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StageChangeEvent extends Event
{

    private static final HandlerList handlers = new HandlerList();
    private int stageTo, stageFrom;
    private String stageName;

    public StageChangeEvent(int stageTo, int stageFrom)
    {
        this.stageTo = stageTo;
        this.stageFrom = stageFrom;

        switch (stageTo) {
            case 0:
                stageName = "Waiting".toUpperCase();
                break;
            case 1:
                stageName = "In progress".toUpperCase();
                break;
            case 2:
                stageName = "Restarting".toUpperCase();
                break;
        }
    }

    /**
     * Get the current stage ID.
     * 
     * @return Integer
     */
    public int getStage()
    {
        return stageTo;
    }

    /**
     * Get the previous stage ID.
     * 
     * @return Integer
     */
    public int getFromStage()
    {
        return stageFrom;
    }

    /**
     * Get the stage name.
     * 
     * @return String
     */
    public String getStageName()
    {
        return stageName;
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
