package CompacContraption;

import CompacContraption.Events.CompactChunkHandler;
import CompacContraption.Events.CompactGuiHandler;
import CompacContraption.Events.CompactPlayerHandler;
import CompacContraption.Manager.CommandManager;
import CompacContraption.Manager.CraftingManager;
import CompacContraption.Manager.WorldManager;
import CompacContraption.Util.Config;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
    private static Main instance;
    private static WorldManager worldManager;
    private static CraftingManager craftingManager;
    private static CompactPlayerHandler compactPlayerHandler;
    private static CompactChunkHandler compactChunkHandler;

    private static FileConfiguration config;
    private static FileConfiguration lang;
    private static FileConfiguration crafting;

    @Override
    public void onEnable() {
        instance = this;
        this.getLogger().info("Enabling the plugin");
        this.getLogger().info("Loading configs");
        this.createConfigs();
        Config c = new Config("config", Main.getInstance());
        this.config = c.getConfig();
        c = new Config("crafting", Main.getInstance());
        this.crafting = c.getConfig();
        c = new Config("language", Main.getInstance());
        this.lang = c.getConfig();

        this.getLogger().info("Loading WorldManager");
        this.worldManager = new WorldManager();
        this.getLogger().info("Loading CraftingManager");
        this.craftingManager = new CraftingManager();
        this.getLogger().info("setting up recipes");
        this.craftingManager.setupRecipes(this);
        this.getCommand("compact").setExecutor(new CommandManager());

        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(this.craftingManager, this);

        this.getLogger().info("loading Gui Handler");
        pm.registerEvents(new CompactGuiHandler(), this);
        this.getLogger().info("loading Chunk handler");
        this.compactChunkHandler = new CompactChunkHandler();
        pm.registerEvents(this.compactChunkHandler, this);
        this.getLogger().info("loading Player handler");
        this.compactPlayerHandler = new CompactPlayerHandler();
        pm.registerEvents(this.compactPlayerHandler, this);
    }
    @Override
    public void onDisable() {
        for(Player p : this.compactPlayerHandler.getLast_loc().keySet()) {
            p.teleport(this.compactPlayerHandler.getLast_loc().get(p));
        }
        Config c = new Config("data", Main.getInstance());
        c.set("World.nextLoc", worldManager.getNextLoc().getBlockX()+";"+worldManager.getNextLoc().getBlockY()+";"+worldManager.getNextLoc().getBlockZ());
        c.save();
        getLogger().info("onDisable is called!");
    }
    public static Main getInstance() {
        return instance;
    }

    public static FileConfiguration getPluginConfig() {
        return config;
    }
    public static FileConfiguration getPluginCrafting() { return crafting; }
    public static FileConfiguration getPluginLang() {
        return lang;
    }

    public static WorldManager getWorldManager() { return worldManager; }
    public static CraftingManager getCraftingManager() {
        return craftingManager;
    }
    public static CompactPlayerHandler getCompactPlayerHandler() { return compactPlayerHandler; }
    public static CompactChunkHandler getCompactChunkHandler() { return compactChunkHandler; }

    private void createConfigs() {
        File customConfigFile = new File(getDataFolder(), "config.yml");
        if (!customConfigFile.exists()) {
            this.getLogger().info("config.yml isn´t found, creating one");
            customConfigFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
            this.getLogger().info("done");
        }
        File customConfigFile1 = new File(getDataFolder(), "crafting.yml");
        if (!customConfigFile1.exists()) {
            this.getLogger().info("crafting.yml isn´t found, creating one");
            customConfigFile1.getParentFile().mkdirs();
            saveResource("crafting.yml", false);
            this.getLogger().info("done");
        }
        File customConfigFile2 = new File(getDataFolder(), "language.yml");
        if (!customConfigFile2.exists()) {
            this.getLogger().info("language.yml isn´t found, creating one");
            customConfigFile2.getParentFile().mkdirs();
            saveResource("language.yml", false);
            this.getLogger().info("done");
        }
    }
}
