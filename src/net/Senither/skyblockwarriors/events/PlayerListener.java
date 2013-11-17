package net.Senither.skyblockwarriors.events;

import net.Senither.skyblockwarriors.SkyblockWarriors;
import net.Senither.skyblockwarriors.api.event.PlayerSpectateEvent;
import net.Senither.skyblockwarriors.api.event.StageChangeEvent;

import net.minecraft.server.v1_6_R3.Packet205ClientCommand;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerListener implements Listener
{

    private SkyblockWarriors _plugin;

    public PlayerListener(SkyblockWarriors plugin)
    {
        _plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e)
    {
        if (!_plugin.controller.isAllowed(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlaced(BlockPlaceEvent e)
    {
        if (!_plugin.controller.isAllowed(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
        if (e.getItem() != null && e.getItem().equals(_plugin.items.spectatorItem)) {
            _plugin.lilypad.movePlayer(e.getPlayer());
            return;
        }

        if (!_plugin.controller.isAllowed(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamageByPlayer(EntityDamageByEntityEvent e)
    {
        Player p = null;
        Player d = null;

        if (e.getEntity() instanceof Player) {
            p = (Player) e.getEntity();
        } else if (e.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getEntity();
            p = (Player) arrow.getShooter();
        }

        if (p != null) {
            if (!_plugin.controller.isAllowed(p)) {
                e.setCancelled(true);
            }
        }

        if (e.getDamager() instanceof Player) {
            d = (Player) e.getDamager();
        } else if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();
            d = (Player) arrow.getShooter();
        }

        if (d != null) {
            if (!_plugin.controller.isAllowed(d)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e)
    {
        if (!_plugin.controller.isAllowed(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e)
    {
        if (!_plugin.controller.isAllowed(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e)
    {
        e.setDeathMessage(null);

        Player player = e.getEntity();

        if (_plugin.playerLives.containsKey(player.getName()) && _plugin.gameState == 1) {
            int lives = _plugin.playerLives.get(player.getName());

            if (lives == 1 && _plugin.running) {
                _plugin.playerLives.remove(player.getName());
                _plugin.playerNames.remove(player.getName());

                // Check win
                if (_plugin.playerNames.size() == 1) {

                    Player winner = Bukkit.getPlayer(_plugin.playerNames.get(0));
                    _plugin.gameState = 2;
                    _plugin.getServer().getPluginManager().callEvent(new StageChangeEvent(2, 1));

                    _plugin.mysql.giveReward(winner.getName());
                    _plugin.chatManager.broadcastMessage(_plugin.messages.format(_plugin.messages.POSTGAME_WINNER, winner.getName()));

                    _plugin.vaninshHandler.showAllPlayers();

                    // Auto respawns the player
                    final String name = player.getName();
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
                    {
                        public void run()
                        {
                            Player p = Bukkit.getPlayer(name);

                            Packet205ClientCommand packet = new Packet205ClientCommand();
                            packet.a = 1;
                            ((CraftPlayer) p).getHandle().playerConnection.a(packet);

                            p.teleport(_plugin.spectatorSpawn);

                            p.setAllowFlight(true);
                            p.setFlying(true);

                            p.setHealth(20.0);
                        }
                    });

                    // Mves all the players to the hub after five seconds
                    _plugin.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
                    {
                        public void run()
                        {
                            _plugin.lilypad.moveAllPlayers();
                        }
                    }, 100);

                    // Auto restarts the server after six seconds
                    _plugin.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
                    {
                        public void run()
                        {
                            _plugin.getServer().shutdown();
                        }
                    }, 120);

                    return;
                }

                _plugin.getServer().getPluginManager().callEvent(new PlayerSpectateEvent(player));

                _plugin.chatManager.broadcastMessage(_plugin.messages.format(_plugin.messages.BATTLE_DEATH, player.getName(), _plugin.playerLives.size()));

                final String name = player.getName();
                _plugin.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
                {
                    public void run()
                    {
                        _plugin.controller.setPlayerAsSpectator(name);
                    }
                }, 20);
            } else {
                if (_plugin.running) {
                    lives--;
                    _plugin.playerLives.put(player.getName(), lives);
                    _plugin.chatManager.broadcastMessage(_plugin.messages.format(_plugin.messages.BATTLE_DEATHVIP, player.getName()));
                }
            }
        }

        /*final String name = player.getName();

         Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(_plugin, new Runnable()
         {
         public void run()
         {
         Player p = Bukkit.getPlayer(name);

         Packet205ClientCommand packet = new Packet205ClientCommand();
         packet.a = 1;
         ((CraftPlayer) p).getHandle().playerConnection.a(packet);



         }
         });
         * */
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e)
    {
        if (_plugin.playerLives.containsKey(e.getPlayer().getName()) && _plugin.gameState == 1) {
            e.setRespawnLocation(_plugin.playerSpawn.get(e.getPlayer().getName()));

            _plugin.controller.setuPlayer(e.getPlayer());

            _plugin.vaninshHandler.vanishPlayer(e.getPlayer());
            _plugin.vaninshHandler.showPlayer(e.getPlayer());
        } else if (_plugin.gameState == 2) {
            e.setRespawnLocation(_plugin.spectatorSpawn);

            e.getPlayer().setAllowFlight(true);
            e.getPlayer().setFlying(true);
        } else {
            e.setRespawnLocation(_plugin.spectatorSpawn);

            e.getPlayer().setAllowFlight(true);
            e.getPlayer().setFlying(true);

            _plugin.vaninshHandler.vanishPlayer(e.getPlayer());
        }

        e.getPlayer().setHealth(20.0);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        if (((e.getWhoClicked() instanceof Player)) && (e.getInventory().getTitle().equalsIgnoreCase(_plugin.items.shopInventoryName))) {
            Player p = (Player) e.getWhoClicked();

            e.setCancelled(true);

            try {
                if (e.getCurrentItem() == null || e.getCurrentItem().getTypeId() == 0) {
                    return;
                }

                ItemMeta im = e.getCurrentItem().getItemMeta();
                String[] lore = im.getLore().get(0).split(" ");
                int cost = Integer.parseInt(_plugin.chatManager.decolorize(lore[0]));

                if (_plugin.mysql.playerPayment(p, cost) == true) {
                    p.getInventory().addItem(e.getCurrentItem());
                    _plugin.chatManager.sendMessage(p, "&b" + im.getDisplayName() + " &3has been delivered for a cost of &b" + cost + " &3tokens!");
                    p.closeInventory();
                }
                return;
            } catch (NullPointerException ex) {
            }
        }
    }
}