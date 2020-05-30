package CompacContraption.Events;

import CompacContraption.Main;
import CompacContraption.Util.Util;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Powerable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;

public class CompactChunkHandler implements Listener {
    private FileConfiguration lang = Main.getPluginLang();
    @EventHandler
    public void onCompactPlace(BlockPlaceEvent e) {
        if(!e.isCancelled()) {
            Block block = e.getBlockPlaced();
            if(block.getType().equals(Material.SHULKER_BOX)) {
                Inventory inv = ((ShulkerBox) block.getState()).getInventory();
                if (inv.getItem(2) != null) {
                    if (inv.getItem(2).hasItemMeta()) {
                        if (inv.getItem(2).getItemMeta().hasLore()) {
                            if (inv.getItem(2).getItemMeta().getLore().contains("Compact")) {
                                if (block.getWorld().equals(Main.getWorldManager().getCompactWorld())) {
                                    e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',lang.getString("place.compact")));
                                    e.setCancelled(true);
                                } else {
                                    this.startTask(inv, block);

                                    String[] str_cords = inv.getItem(26).getItemMeta().getDisplayName().split(";");
                                    String[] str_size = inv.getItem(3).getItemMeta().getDisplayName().replace("Size: ", "").split("x");

                                    this.loadChunks(Integer.parseInt(str_cords[0]), Integer.parseInt(str_cords[2]), Integer.parseInt(str_size[0]), Integer.parseInt(str_size[2]));
                                }
                            }
                        }
                    }
                }
            }
            else if(block.getType().toString().contains("_BED")) {
                if(block.getWorld().equals(Main.getWorldManager().getCompactWorld())) {
                    e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',lang.getString("place.bed")));
                    e.setCancelled(true);
                }
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void onCompactBreak(BlockBreakEvent e) {
        if(!e.isCancelled()) {
            Block block = e.getBlock();
            if(block.getType().equals(Material.SHULKER_BOX)) {
                Inventory inv = ((ShulkerBox) block.getState()).getInventory();
                if (inv.getItem(2) != null) {
                    if (inv.getItem(2).hasItemMeta()) {
                        if (inv.getItem(2).getItemMeta().hasLore()) {
                            if (inv.getItem(2).getItemMeta().getLore().contains("Compact")) {
                                Bukkit.getScheduler().cancelTask(Integer.parseInt(inv.getItem(5).getItemMeta().getDisplayName()));

                                String[] str_cords = inv.getItem(26).getItemMeta().getDisplayName().split(";");
                                String[] str_size = inv.getItem(3).getItemMeta().getDisplayName().replace("Size: ", "").split("x");
                                for(Player p : Main.getWorldManager().getCompactWorld().getPlayers()) {
                                    Location loc1 = new Location(Main.getWorldManager().getCompactWorld(), Integer.parseInt(str_cords[0]), Integer.parseInt(str_cords[1]), Integer.parseInt(str_cords[2]));
                                    Location loc2 = new Location(Main.getWorldManager().getCompactWorld(), Integer.parseInt(str_cords[0])+Integer.parseInt(str_size[0])+1, Integer.parseInt(str_cords[1])+Integer.parseInt(str_size[1])+1, Integer.parseInt(str_cords[2])+Integer.parseInt(str_size[2])+1);
                                    if(Util.inCuboid(p.getLocation(), loc1, loc2)) {
                                        Main.getCompactPlayerHandler().playerLeave(p);
                                    }
                                }

                                this.unloadChunks(Integer.parseInt(str_cords[0]), Integer.parseInt(str_cords[2]), Integer.parseInt(str_size[0]), Integer.parseInt(str_size[2]));
                            }
                        }
                    }
                }
            }
            else if(block.getWorld().equals(Main.getWorldManager().getCompactWorld())) {
                if(block.getType().equals(Material.HOPPER) || block.getType().equals(Material.NOTE_BLOCK) || block.getType().equals(Material.OBSERVER)) {
                    if(!block.getRelative(0,1,0).getType().equals(Material.BEDROCK) && !block.getRelative(0,-1,0).getType().equals(Material.BEDROCK)) {
                        int bedrock = 0;
                        for(int x=-1;x < 2;x++) {
                            for(int z=-1;z < 2;z++) {
                                if(block.getRelative(x,0,z).getType().equals(Material.BEDROCK)) {
                                    bedrock++;
                                }
                            }
                        }
                        if(bedrock == 8) {
                            e.setCancelled(true);
                            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',lang.getString("break.input_output")));
                        }
                    } else {
                        int bedrock = 0;
                        for(int x=-1;x < 2;x++) {
                            for(int y=-1;y < 2;y++) {
                                for(int z=-1;z < 2;z++) {
                                    if(block.getRelative(x,y,z).getType().equals(Material.BEDROCK)) {
                                        bedrock++;
                                    }
                                }
                            }
                        }
                        if(bedrock == 17) {
                            if(!block.getRelative(0,1,0).getType().equals(Material.BEDROCK) || !block.getRelative(0,-1,0).getType().equals(Material.BEDROCK)) {
                                e.setCancelled(true);
                                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',lang.getString("break.input_output")));
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if(!e.getWorld().equals(Main.getWorldManager().getCompactWorld())) {
            for (BlockState blockState : e.getChunk().getTileEntities()) {
                if (blockState instanceof ShulkerBox) {
                    ShulkerBox shulkerBox = (ShulkerBox) blockState;
                    Inventory inv = shulkerBox.getInventory();
                    if (inv.getItem(2) != null) {
                        if (inv.getItem(2).hasItemMeta()) {
                            if (inv.getItem(2).getItemMeta().hasLore()) {
                                if (inv.getItem(2).getItemMeta().getLore().contains("Compact")) {
                                    this.startTask(inv, blockState.getBlock());

                                    String[] str_cords = inv.getItem(26).getItemMeta().getDisplayName().split(";");
                                    String[] str_size = inv.getItem(3).getItemMeta().getDisplayName().replace("Size: ", "").split("x");

                                    this.loadChunks(Integer.parseInt(str_cords[0]), Integer.parseInt(str_cords[2]), Integer.parseInt(str_size[0]), Integer.parseInt(str_size[2]));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        if (e.getChunk() != null) {
            if (!e.getWorld().equals(Main.getWorldManager().getCompactWorld())) {
                for (BlockState blockState : e.getChunk().getTileEntities()) {
                    if (blockState instanceof ShulkerBox) {
                        ShulkerBox shulkerBox = (ShulkerBox) blockState;
                        Inventory inv = shulkerBox.getInventory();
                        if (inv.getItem(2) != null) {
                            if (inv.getItem(2).hasItemMeta()) {
                                if (inv.getItem(2).getItemMeta().hasLore()) {
                                    if (inv.getItem(2).getItemMeta().getLore().contains("Compact")) {
                                        Bukkit.getScheduler().cancelTask(Integer.parseInt(inv.getItem(5).getItemMeta().getDisplayName()));

                                        String[] str_cords = inv.getItem(26).getItemMeta().getDisplayName().split(";");
                                        String[] str_size = inv.getItem(3).getItemMeta().getDisplayName().replace("Size: ", "").split("x");

                                        this.unloadChunks(Integer.parseInt(str_cords[0]), Integer.parseInt(str_cords[2]), Integer.parseInt(str_size[0]), Integer.parseInt(str_size[2]));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onHopper(InventoryMoveItemEvent e) {
        final Inventory inv = e.getDestination();
        final Inventory source = e.getSource();
        if (source.getItem(2) != null || e.getItem() != null) {
            ItemStack item;
            if(source.getItem(2) != null ) {item = source.getItem(2);}
            else{item = e.getItem();}
            if (item.hasItemMeta()) {
                if (item.getItemMeta().hasLore()) {
                    if (item.getItemMeta().getLore().contains("Compact")) {
                        e.setCancelled(true);
                    }
                }
            }
        }
        if (inv.getItem(2) != null) {
            if (inv.getItem(2).hasItemMeta()) {
                if (inv.getItem(2).getItemMeta().hasLore()) {
                    if (inv.getItem(2).getItemMeta().getLore().contains("Compact")) {
                        e.setCancelled(true);
                        Hopper hopper = (Hopper)source.getHolder();
                        BlockFace faceing = ((Directional)hopper.getBlock().getBlockData()).getFacing();
                        int slot = 18;
                        if(faceing.equals(BlockFace.DOWN)) {slot = 19;}
                        else if(faceing.equals(BlockFace.SOUTH)) {slot = 22;}
                        else if(faceing.equals(BlockFace.WEST)) {slot = 23;}
                        else if(faceing.equals(BlockFace.NORTH)) {slot = 20;}
                        else if(faceing.equals(BlockFace.EAST)) {slot = 21;}
                        final int final_slot = slot;
                        final ItemStack final_item = e.getItem();
                        if(inv.getItem(final_slot) == null) {
                            Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable(){
                                public void run(){
                                    ItemStack item = source.getItem(source.first(final_item.getType()));
                                    inv.setItem(final_slot, item);
                                    item.setAmount(0);
                                }
                            }, 1);
                        }
                    }
                }
            }
        }
    }
    public void loadChunks(int x, int z, int sx, int sz) {
        World world = Main.getWorldManager().getCompactWorld();
        int cx = (int)Math.floor(x/16.0);
        int cz = (int)Math.floor(z/16.0);
        int csx = (int)Math.floor((sx+2.0)/16.0);
        int csz = (int)Math.floor((sz+2.0)/16.0);
        for (int ax = 0; ax <= csx; ax++) {
            for (int az = 0; az <= csz; az++) {
                Chunk chunk = world.getChunkAt(cx+ax,cz+az);
                chunk.setForceLoaded(true);
            }
        }
    }
    public void unloadChunks(int x, int z, int sx, int sz) {
        World world = Main.getWorldManager().getCompactWorld();
        int cx = (int)Math.floor(x/16.0);
        int cz = (int)Math.floor(z/16.0);
        int csx = (int)Math.floor((sx+2.0)/16.0);
        int csz = (int)Math.floor((sz+2.0)/16.0);
        for (int ax = 0; ax <= csx; ax++) {
            for (int az = 0; az <= csz; az++) {
                Chunk chunk = world.getChunkAt(cx+ax,cz+az);
                chunk.setForceLoaded(false);
            }
        }
    }
    public void startTask(final Inventory inv, final Block block) {
        ItemMeta task_meta = inv.getItem(5).getItemMeta();
        try {
            int task_id = Integer.parseInt(task_meta.getDisplayName());
            if(Bukkit.getScheduler().isCurrentlyRunning(task_id) || Bukkit.getScheduler().isQueued(task_id)) {
                Bukkit.getScheduler().cancelTask(task_id);
            }
        } catch (NumberFormatException ex) { }
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable(){
            public void run(){
                for (int i=0;i < 6;i++) {
                    ItemMeta itemMeta = inv.getItem(9+i).getItemMeta();
                    String[] side = itemMeta.getDisplayName().split(": ");
                    String[] values = itemMeta.getLore().get(0).split(";");
                    Location loc = new Location(Main.getWorldManager().getCompactWorld(), Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]));
                    Block box_block = loc.getBlock();
                    if(side[1].equals("Item Input")) {
                        ItemStack item = inv.getItem(18+i);
                        if(item != null) {
                            if(box_block.getState() instanceof InventoryHolder) {
                                InventoryHolder holder = (InventoryHolder) box_block.getState();
                                Inventory input_inv = holder.getInventory();
                                HashMap<Integer, ItemStack> leftOver = holder.getInventory().addItem(item);
                                if (!leftOver.isEmpty()) {
                                    inv.setItem(18 + i, leftOver.get(0));
                                } else {
                                    inv.setItem(18 + i, null);
                                }
                            }
                        }
                    }
                    else if(side[1].equals("Item Output")) {
                        ItemStack item = inv.getItem(18+i);
                        if(inv.getItem(18+i) == null) {
                            if(box_block.getState() instanceof InventoryHolder) {
                                InventoryHolder holder = (InventoryHolder) box_block.getState();
                                Inventory hopper_inv = holder.getInventory();
                                if (hopper_inv.getContents() != null) {
                                    for (int a = 0; a < 5; a++) {
                                        ItemStack hopper_item = hopper_inv.getItem(a);
                                        if (hopper_item != null) {
                                            inv.setItem(18 + i, hopper_item);
                                            hopper_inv.setItem(a, null);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        else if(block.getRelative(BlockFace.valueOf(side[0].toUpperCase())) != null) {
                            Block my_block = block.getRelative(BlockFace.valueOf(side[0].toUpperCase()));
                            if(my_block.getState() instanceof InventoryHolder) {
                                Inventory hopper_inv = ((InventoryHolder)my_block.getState()).getInventory();
                                if (hopper_inv.getItem(2) != null) {
                                    if (hopper_inv.getItem(2).hasItemMeta()) {
                                        if (hopper_inv.getItem(2).getItemMeta().hasLore()) {
                                            if (hopper_inv.getItem(2).getItemMeta().getLore().contains("Compact")) {
                                                return;
                                            }
                                        }
                                    }
                                }
                                HashMap<Integer, ItemStack> leftOver = hopper_inv.addItem(item);
                                inv.setItem(18+i,leftOver.get(0));
                            }
                        }
                    }
                    else if(side[1].equals("Redstone Input")) {
                        Block my_block = block.getRelative(BlockFace.valueOf(side[0].toUpperCase()));
                        if(box_block.getBlockData() instanceof Powerable) {
                            Powerable power = (Powerable) box_block.getBlockData();
                            if(my_block.getBlockData() instanceof Powerable) {
                                Powerable my_power = (Powerable) my_block.getBlockData();
                                if (my_power.isPowered()) {
                                    power.setPowered(true);
                                } else {
                                    power.setPowered(false);
                                }

                            } else {
                                power.setPowered(false);
                            }
                            box_block.setBlockData(power);
                        }
                    }
                    else if(side[1].equals("Redstone Output")) {
                        Block rblock = block.getRelative(BlockFace.valueOf(side[0].toUpperCase()));
                        if(rblock.getBlockData() instanceof Powerable) {
                            Powerable power = (Powerable)rblock.getBlockData();
                            if(box_block.isBlockPowered()) {power.setPowered(true);}
                            else {power.setPowered(false);}
                            rblock.setBlockData(power);
                        }
                    }
                }
            }
        }, 0, Main.getPluginConfig().getLong("World.Timer"));
        inv.setItem(5, Util.ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1, ""+task.getTaskId()));
    }
}
