package net.Senither.skyblockwarriors.network;

import java.io.UnsupportedEncodingException;
import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.request.RequestException;
import lilypad.client.connect.api.request.impl.MessageRequest;
import lilypad.client.connect.api.request.impl.RedirectRequest;
import lilypad.client.connect.api.result.FutureResult;
import lilypad.client.connect.api.result.FutureResultListener;
import lilypad.client.connect.api.result.Result;
import lilypad.client.connect.api.result.StatusCode;
import lilypad.client.connect.api.result.impl.MessageResult;

import net.Senither.skyblockwarriors.SkyblockWarriors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Lilypad
{

    public SkyblockWarriors _plugin;

    public Lilypad(SkyblockWarriors plugin)
    {

        _plugin = plugin;

        sendServerStatus();
    }

    public Connect getBukkitConnect()
    {
        return (Connect) Bukkit.getServer().getServicesManager().getRegistration(Connect.class).getProvider();
    }

    public void movePlayer(final Player player)
    {
        Connect connect = getBukkitConnect();

        try {
            connect.request(new RedirectRequest(_plugin.lobby, player.getName())).registerListener(new FutureResultListener()
            {
                public void onResult(Result redirectResult)
                {
                    if (redirectResult.getStatusCode() == StatusCode.SUCCESS) {
                        return;
                    }
                    _plugin.chatManager.sendMessage(player, "&cThe lobby seems to be offline at the time. Try again in a minute.");
                }
            });
        } catch (RequestException ex) {
            _plugin.chatManager.sendMessage(player, "&cThe lobby seems to be offline at the time. Try again in a minute.");
        }
    }

    public void moveAllPlayers()
    {
        for (Player player : Bukkit.getOnlinePlayers()) {
            movePlayer(player);
        }
    }

    private void sendServerStatus()
    {        
        Bukkit.getScheduler().scheduleSyncRepeatingTask(_plugin, new Runnable()
        {
            public void run()
            {
                Connect connect = getBukkitConnect();
                
                String message = getState() + "%" + Bukkit.getOnlinePlayers().length + "%" + Bukkit.getMaxPlayers();
                
                MessageRequest request = null;
                try {
                    request = new MessageRequest(_plugin.lobby, "MG-Lobby", message);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                FutureResult<MessageResult> futureResult = null;

                try {
                    futureResult = connect.request(request);
                } catch (RequestException e) {
                    e.printStackTrace();
                }
            }
        }, 40, 20);
    }
    
    private String getState()
    {
        switch (_plugin.gameState) {
            case 0:
                //return "Joinable";
                return "" + 1;
            case 1:
                //return "In Progress";
                return "" + 2;
            default:
                //return "Restarting";
                return "" + 2;
        }
    }
}
