package CompacContraption.Manager;

import CompacContraption.Main;
import CompacContraption.Util.Config;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.ConfigurationSection;

public class WorldManager {
    private World compactWorld;
    private ConfigurationSection wolrdConfig;
    private Location nextLoc;

    public WorldManager() {
        this.wolrdConfig = Main.getPluginConfig().getConfigurationSection("World");
        WorldCreator compactWorldCreator = new WorldCreator("compactWorld");
        compactWorldCreator.environment(World.Environment.valueOf(this.wolrdConfig.getString("Environment")));
        compactWorldCreator.generateStructures(false);
        compactWorldCreator.type(WorldType.FLAT);

        this.compactWorld = compactWorldCreator.createWorld();
        this.compactWorld.setDifficulty(Difficulty.valueOf(this.wolrdConfig.getString("Difficulty")));

        Config c = new Config("data", Main.getInstance());
        String[] values = {"0", "10", "0"};;
        if(c.getConfig().isSet("World.nextLoc")) {
            values = c.getConfig().getString("World.nextLoc").split(";");
        }
        nextLoc = new Location(this.compactWorld, Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
    }
    public void boxCreater(int sx, int sy, int sz) {
        for (int x = -1; x < sx+3; x++) {
            for (int y = 0; y < sy+2; y++) {
                for (int z = -1; z < sz+3; z++) {
                    if (x <= 0 || y == 0 || z <= 0 || x >= sx+1 || y == sy+1 || z >= sz+1) {
                        if(!(x > sx+2 || y > sy+1 || z > sz+2)) {
                            nextLoc.clone().add(x, y, z).getBlock().setType(Material.BEDROCK);
                        }
                    }
                }
            }
        }

        int distance = this.wolrdConfig.getInt("distance");
        if(nextLoc.getX() >= 16*distance*100) {
            nextLoc.setX(0);
            nextLoc.add(0, 0, 16*distance);
        }
        nextLoc.add(16*distance, 0, 0);

    }
    public void sideModeChange(String side, String type, String sloc) {
        String[] values = sloc.split(";");
        Location loc = new Location(this.compactWorld, Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
        BlockFace facing = BlockFace.DOWN;
        if(type.equals("Item Input")) {
            facing = BlockFace.valueOf(side.toUpperCase()).getOppositeFace();
            loc.getBlock().setType(Material.HOPPER);
        }
        else if(type.equals("Item Output")) {
            facing = BlockFace.valueOf(side.toUpperCase());
            loc.getBlock().setType(Material.HOPPER);
        }
        if(type.contains("Item")) {
            Directional blockData = (Directional) loc.getBlock().getBlockData();
            blockData.setFacing(facing);
            loc.getBlock().setBlockData(blockData);
        }
        if(type.equals("Redstone Input")) {
            facing = BlockFace.valueOf(side.toUpperCase());
            loc.getBlock().setType(Material.OBSERVER);
            Directional blockData = (Directional) loc.getBlock().getBlockData();
            blockData.setFacing(facing);
            loc.getBlock().setBlockData(blockData);
        }
        else if(type.equals("Redstone Output")) {
            loc.getBlock().setType(Material.NOTE_BLOCK);
        }
        else if(type.equals("None")) {
            loc.getBlock().setType(Material.BEDROCK);
        }

    }

    public Location getNextLoc() {
        return nextLoc;
    }

    public void setNextLoc(Location nextLoc) {
        this.nextLoc = nextLoc;
    }
    public World getCompactWorld() {
        return compactWorld;
    }

    public void setCompactWorld(World compactWorld) {
        this.compactWorld = compactWorld;
    }
}
