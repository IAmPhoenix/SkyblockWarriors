package net.Senither.skyblockwarriors.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.Senither.skyblockwarriors.SkyblockWarriors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Items
{

    private SkyblockWarriors _plugin;
    public ItemStack spectatorItem;
    public Inventory shopInventory;
    public String shopInventoryName;

    public Items(SkyblockWarriors plugin)
    {
        _plugin = plugin;

        // Creating the custom lobby item
        spectatorItem = new ItemStack(Material.COMPASS, 1);
        ItemMeta spectatorMeta = spectatorItem.getItemMeta();
        spectatorMeta.setDisplayName(_plugin.chatManager.colorize("&a&lReturn to the Lobby"));
        spectatorMeta.setLore(Arrays.asList(new String[]{_plugin.chatManager.colorize("&eRight click this item to return to the lobby")}));
        spectatorItem.setItemMeta(spectatorMeta);

        generateShop();
    }

    private void generateShop()
    {
        // Set the shop inventory naem
        shopInventoryName = _plugin.chatManager.colorize("&8Item Shop:");
        // Create the inventory
        shopInventory = Bukkit.getServer().createInventory(null, (_plugin.shopConfig.getConfig().getInt("inventoryRows") * 9), shopInventoryName);

        List<String> items = (List<String>) _plugin.shopConfig.getConfig().getList("items");

        if (items.size() != 0) {
            shopInventory.clear();
        }

        for (int i = 0; i < items.size(); i++) {
            String name = items.get(i);

            short durability = (short) _plugin.shopConfig.getConfig().getInt("data." + name + ".durability");
            
            String itemIDString = _plugin.shopConfig.getConfig().getString("data." + name + ".id");
            
            ItemStack item;
            
            if (itemIDString.contains(":")) {
                String[] itemMetaData = itemIDString.split(":");
                item = new ItemStack(Integer.parseInt(itemMetaData[0]),_plugin.shopConfig.getConfig().getInt("data." + name + ".quantity"));
                item.setDurability(Short.parseShort(itemMetaData[1]));
            } else {
                item = new ItemStack(Integer.parseInt(itemIDString), _plugin.shopConfig.getConfig().getInt("data." + name + ".quantity"));
            }

            // Set a custom durability if its not set to 0
            if (durability != 0) {
                item.setDurability(durability);
            }

            // Custom enchantments
            List<String> enchants = (List<String>) _plugin.shopConfig.getConfig().getList("data." + name + ".enchants");
            if (enchants.size() != 0) {
                for (int ei = 0; ei < enchants.size(); ei++) {
                    String[] data = enchants.get(ei).split(" ");
                    int level = Integer.parseInt(data[1]);
                    if (data[0].equalsIgnoreCase("sharpness")) {
                        item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, level);
                    } else if (data[0].equalsIgnoreCase("fireaspect")) {
                        item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, level);
                    } else if (data[0].equalsIgnoreCase("knockback")) {
                        item.addUnsafeEnchantment(Enchantment.KNOCKBACK, level);
                    } else if (data[0].equalsIgnoreCase("knockback_arrow")) {
                        item.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, level);
                    } else if (data[0].equalsIgnoreCase("power")) {
                        item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, level);
                    } else if (data[0].equalsIgnoreCase("infinity")) {
                        item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, level);
                    } else if (data[0].equalsIgnoreCase("thorns")) {
                        item.addUnsafeEnchantment(Enchantment.THORNS, level);
                    } else if (data[0].equalsIgnoreCase("digspeed")) {
                        item.addUnsafeEnchantment(Enchantment.DIG_SPEED, level);
                    } else if (data[0].equalsIgnoreCase("protection")) {
                        item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level);
                    } else if (data[0].equalsIgnoreCase("protection_fall")) {
                        item.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, level);
                    } else if (data[0].equalsIgnoreCase("protection_arrow")) {
                        item.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, level);
                    }
                }
            }

            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(_plugin.chatManager.colorize(_plugin.shopConfig.getConfig().getString("data." + name + ".name")));

            List<String> itemLore = new ArrayList<String>();
            itemLore.add(_plugin.chatManager.colorize("&b&o" + _plugin.shopConfig.getConfig().getInt("data." + name + ".cost") + " Tokens"));

            itemMeta.setLore(itemLore);

            item.setItemMeta(itemMeta);

            shopInventory.setItem(_plugin.shopConfig.getConfig().getInt("data." + name + ".slot"), item);
        }
    }
}
