package net.Senither.skyblockwarriors;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.Senither.skyblockwarriors.api.SkyblockWarriorsAPI;
import net.Senither.skyblockwarriors.commands.GameCommands;
import net.Senither.skyblockwarriors.commands.SetupCommands;
import net.Senither.skyblockwarriors.engine.Controller;
import net.Senither.skyblockwarriors.events.PlayerListener;
import net.Senither.skyblockwarriors.mysql.DatabaseHandler;
import net.Senither.skyblockwarriors.network.Lilypad;
import net.Senither.skyblockwarriors.events.ServerListener;
import net.Senither.skyblockwarriors.vanish.VanishHandler;
import net.Senither.skyblockwarriors.utils.ChatManager;
import net.Senither.skyblockwarriors.utils.Items;
import net.Senither.skyblockwarriors.utils.Messages;
import net.Senither.skyblockwarriors.utils.Permissions;
import net.Senither.skyblockwarriors.utils.YAMLManager;
import net.Senither.skyblockwarriors.world.WorldManager;

import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyblockWarriors extends JavaPlugin
{

    /**
     * Main Variables that will be used across the whole plugin.
     */
    // Version number for the plugin
    public String v;
    // Lobby name variable
    public String lobby;
    // Custom configs
    public YAMLManager spawnConfig;
    public YAMLManager shopConfig;
    // Setup stage checker
    public boolean setup;
    public int setupStage;
    public boolean running = false;
    /*
     * Class handlers
     */
    // Network Classes
    public Lilypad lilypad;
    public ServerListener serverStatus;
    // Utilits Classes
    public ChatManager chatManager;
    public Messages messages;
    public Permissions permissions;
    // Scoreboard Class
    //public ScoreboardHandler scoreboard;
    // Player vanish handler
    public VanishHandler vaninshHandler;
    // MySQL Class
    public DatabaseHandler mysql;
    // Controller class
    public Controller controller;
    // World Manger class
    public WorldManager worldManager;
    // Custom Items class
    public Items items;
    // API Handler
    public static SkyblockWarriorsAPI API;
    /*
     * Game Variables
     */
    // Game Modes
    public int gameState;
    // Spectator Spawn
    public Location spectatorSpawn;
    // Game Spawn Locations
    public List<Location> spawnLocations = new ArrayList<Location>();
    // Player name Holders
    public List<String> playerNames = new ArrayList<String>();
    // Player spawn handler
    public HashMap<String, Location> playerSpawn = new HashMap<String, Location>();
    // PLayer Lives handler
    public HashMap<String, Integer> playerLives = new HashMap<String, Integer>();
    // Player eco
    public HashMap<String, Integer> playerEco = new HashMap<String, Integer>();

    public void onLoad()
    {
        // Save the default "config.yml" file
        saveDefaultConfig();

        // Get the lobby name
        chatManager = new ChatManager(this);
        lobby = getConfig().getString("lobby");

        // Loading up needed classes
        worldManager = new WorldManager(this);

        boolean backupsFound = false;

        File backupDir = new File(getDataFolder(), "backups");
        if (!backupDir.exists()) {
            getLogger().info("No backup directory found; creating one now.");
            getLogger().info("Place world folders you want to reset from in '.../plugins/SkyblockWarriors/backups'");
            backupDir.mkdirs();
        } else {
            for (File backup : backupDir.listFiles()) {
                if ((backup.isDirectory()) && (backup.listFiles().length != 0)) {
                    backupsFound = true;
                }
            }

            if (backupsFound) {
                getLogger().info("Found backup folder, attempting to reset the world..");
                // Resetting the world
                worldManager.resetWorld();
            } else {
                if (!getConfig().getBoolean("arena-setup") == false) {
                    getLogger().info("The plugin should be setup, please copy the world into the backup folder");
                    getLogger().info("so the plugin can reset the world on each restart!");
                }
            }
        }
    }

    @Override
    public void onEnable()
    {
        chatManager.LogInfo("Loading worlds..");
        for (World world : getServer().getWorlds()) {
            world.setAutoSave(false);
            world.setDifficulty(Difficulty.NORMAL);
            world.setAnimalSpawnLimit(0);
            world.setAmbientSpawnLimit(0);
            world.setMonsterSpawnLimit(0);
            world.setPVP(true);

            for (Entity entity : world.getEntities()) {
                entity.remove();
            }
        }
        chatManager.LogInfo("World settings was set for world(s)");
        for (World world : getServer().getWorlds()) {
            chatManager.LogInfo(world.getName());
        }
        // This is just an empty space for neat-ness
        chatManager.LogInfo("");

        // Get the plugin version from the plugin.yml
        v = getDescription().getVersion();

        // Setting up classes
        lilypad = new Lilypad(this);
        serverStatus = new ServerListener(this);
        messages = new Messages(this);
        permissions = new Permissions(this);
        //scoreboard = new ScoreboardHandler();
        vaninshHandler = new VanishHandler(this);
        controller = new Controller(this);
        mysql = new DatabaseHandler(this);

        // Setting up the SkyblockWarriorsAPI
        chatManager.LogInfo("Enabling the API");
        API = new SkyblockWarriorsAPI(this);

        // Check what game stage the plugin should be loaded up in
        chatManager.LogInfo("Checking server status...");
        if (getConfig().getBoolean("arena-setup") == false) {
            chatManager.LogInfo("Setting server in SETUP mode");

            setup = false;

            chatManager.LogInfo("Creating and loading needed configs and folders..");
            setupConfig();
            loadSetupStage();
        } else {
            chatManager.LogInfo("Setting server in READY mode");

            setup = true;

            chatManager.LogInfo("Loading configs..");
            setupConfig();
            loadConfig();
            loadVariablesFromConfig();
        }

        // The shop relays on the shopConfig to be setup
        // Which is why we're first using it now
        items = new Items(this);

        /*
         * Register events
         */

        chatManager.LogInfo("Register needed event listeners..");
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerListener(this), this);

        /*
         * Register commands
         */

        GameCommands GC = new GameCommands(this);
        getCommand("skyblockwarriors").setExecutor(GC);
        getCommand("lobby").setExecutor(GC);
        getCommand("shop").setExecutor(GC);
        getCommand("tokens").setExecutor(GC);
        getCommand("author").setExecutor(GC);

        SetupCommands SC = new SetupCommands(this);
        getCommand("setup").setExecutor(SC);

        chatManager.LogInfo("Finished loading settings! SkyblockWarriors is now enabled in the " + ((setup == false) ? "SETUP" : "READY") + " mode");
    }

    @Override
    public void onDisable()
    {
        vaninshHandler.showAllPlayers();
        lilypad.moveAllPlayers();
        chatManager.LogInfo("Disabling SkyblockWarriors v" + v);
    }

    public void setupConfig()
    {
        boolean spawnConfigSetup = false;
        boolean shopConfigSetup = false;

        // Check if the spawns.yml file exists
        File spawnConfig = new File(getDataFolder(), "spawns.yml");
        if (!spawnConfig.exists()) {
            spawnConfigSetup = true;
        }

        // Checks if the shop.yml file exists
        File shopConfig = new File(getDataFolder(), "shop.yml");
        if (!shopConfig.exists()) {
            shopConfigSetup = true;
        }

        // Load the configs, if they don't exist it will create them
        loadConfig();

        // Create the default spawn config
        if (spawnConfigSetup == true) {
            this.spawnConfig.getConfig().set("spawns", false);
            this.spawnConfig.getConfig().set("spawn", false);
            this.spawnConfig.getConfig().set("spectatorSpawn", false);
            this.spawnConfig.saveConfig();
        }

        // Create the default shop config
        if (shopConfigSetup == true) {

            // Default items
            List<String> items = new ArrayList<String>();
            items.add("woodensword");
            items.add("bow");
            items.add("arrow");

            // Setting up the amount of rows the inventory should have
            this.shopConfig.getConfig().set("inventoryRows", 4);

            // Setting up the list of default items
            this.shopConfig.getConfig().set("items", items);

            // Setting up the Wooden Sword item
            this.shopConfig.getConfig().set("data.woodensword.id", 268);
            this.shopConfig.getConfig().set("data.woodensword.quantity", 1);
            this.shopConfig.getConfig().set("data.woodensword.durability", 0);
            this.shopConfig.getConfig().set("data.woodensword.enchants", Arrays.asList("none 0"));
            this.shopConfig.getConfig().set("data.woodensword.name", "&aWooden Sword");
            this.shopConfig.getConfig().set("data.woodensword.cost", 5);
            this.shopConfig.getConfig().set("data.woodensword.slot", 0);

            // Setting up the Bow item
            this.shopConfig.getConfig().set("data.bow.id", 261);
            this.shopConfig.getConfig().set("data.bow.quantity", 1);
            this.shopConfig.getConfig().set("data.bow.durability", 0);
            this.shopConfig.getConfig().set("data.bow.enchants", Arrays.asList("none 0"));
            this.shopConfig.getConfig().set("data.bow.name", "&dBow");
            this.shopConfig.getConfig().set("data.bow.cost", 8);
            this.shopConfig.getConfig().set("data.bow.slot", 1);

            // Setting up the Arrow item
            this.shopConfig.getConfig().set("data.arrow.id", 262);
            this.shopConfig.getConfig().set("data.arrow.quantity", 12);
            this.shopConfig.getConfig().set("data.arrow.durability", 0);
            this.shopConfig.getConfig().set("data.arrow.enchants", Arrays.asList("none 0"));
            this.shopConfig.getConfig().set("data.arrow.name", "&9Arrow");
            this.shopConfig.getConfig().set("data.arrow.cost", 6);
            this.shopConfig.getConfig().set("data.arrow.slot", 2);

            // Save the config
            this.shopConfig.saveConfig();
        }
    }

    public void loadConfig()
    {
        // Loads the spawnConfig
        spawnConfig = new YAMLManager(this, "spawns.yml");
        spawnConfig.getConfig().options().header(
                "===================================================\n"
                + "=                  Spawn Config                   =\n"
                + "=                Version Beta 1.0                 =\n"
                + "=                                                 =\n"
                + "= All the spawns is stored here                   =\n"
                + "=                                                 =\n"
                + "= DO NOT EDIT ANY OF THIS CONFIG IF YOU DON'T     =\n"
                + "= KNOW YAML!                                      =\n"
                + "===================================================\n");
        spawnConfig.saveDefaultConfig();

        // Loads the shop config
        shopConfig = new YAMLManager(this, "shop.yml");
        shopConfig.getConfig().options().header(
                "===================================================\n"
                + "=                   Shop Config                   =\n"
                + "=                Version Beta 1.0                 =\n"
                + "===================================================\n");
        shopConfig.saveDefaultConfig();
    }

    private void loadVariablesFromConfig()
    {
        /**
         * Load all the spawns from the config.
         */
        // Normal spawns
        List<String> spawnNames = (List<String>) spawnConfig.getConfig().getList("spawns");
        for (String spawn : spawnNames) {
            Location location = new Location(
                    getServer().getWorld(spawnConfig.getConfig().getString("spawn." + spawn + ".world")),
                    spawnConfig.getConfig().getDouble("spawn." + spawn + ".x"),
                    spawnConfig.getConfig().getDouble("spawn." + spawn + ".y"),
                    spawnConfig.getConfig().getDouble("spawn." + spawn + ".z"));
            location.setPitch(spawnConfig.getConfig().getInt("spawn." + spawn + ".pitch"));
            location.setYaw(spawnConfig.getConfig().getInt("spawn." + spawn + ".yaw"));

            spawnLocations.add(location);
        }

        // Spectator Spawn
        spectatorSpawn = new Location(
                getServer().getWorld(spawnConfig.getConfig().getString("spectatorSpawn.world")),
                spawnConfig.getConfig().getDouble("spectatorSpawn.x"),
                spawnConfig.getConfig().getDouble("spectatorSpawn.y"),
                spawnConfig.getConfig().getDouble("spectatorSpawn.z"));
        spectatorSpawn.setPitch(spawnConfig.getConfig().getInt("spectatorSpawn.pitch"));
        spectatorSpawn.setYaw(spawnConfig.getConfig().getInt("spectatorSpawn.yaw"));
    }

    private void loadSetupStage()
    {
        if (spawnConfig.getConfig().getBoolean("spawn") == false) {
            setupStage = 0;
        } else if (spawnConfig.getConfig().getBoolean("spectatorSpawn") == false) {
            setupStage = 1;
        } else {
            setupStage = 2;
        }
    }
}
