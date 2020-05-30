package CompacContraption.Events;

import CompacContraption.Main;

import CompacContraption.Util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CompactGuiHandler implements Listener {
    private static final String[] modes = {"Item Input", "Item Output", "Redstone Input", "Redstone Output", "None"};
    private FileConfiguration lang = Main.getPluginLang();

    @EventHandler
    public void onGuiClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        if(inv != null) {
            if (inv.getHolder() != null) {
                if (inv.getHolder() instanceof ShulkerBox) {
                    if (inv.getItem(2) != null) {
                        if (inv.getItem(2).hasItemMeta()) {
                            if (inv.getItem(2).getItemMeta().hasLore()) {
                                if (inv.getItem(2).getItemMeta().getLore().contains("Compact")) {
                                    if (e.getSlot() != 0 && e.getSlot() != 1) {
                                        e.setCancelled(true);
                                    }
                                    if (e.getSlot() >= 18 && e.getSlot() <= 23) {
                                        if (!e.getCurrentItem().getType().equals(Material.BARRIER)) {
                                            e.setCancelled(false);
                                        }
                                    }
                                    if (e.getSlot() == 8) {
                                        String[] str_cords = inv.getItem(26).getItemMeta().getDisplayName().split(";");
                                        String[] str_size = inv.getItem(3).getItemMeta().getDisplayName().replace("Size: ", "").split("x");
                                        outerloop:
                                        for (double x = Integer.parseInt(str_cords[0])+1.5; x <= Integer.parseInt(str_cords[0])+Integer.parseInt(str_size[0])+0.5; x++) {
                                            for (double y = Integer.parseInt(str_cords[1])+1.2; y <= Integer.parseInt(str_cords[1])+Integer.parseInt(str_size[1])+0.2; y++) {
                                                for (double z = Integer.parseInt(str_cords[2])+1.5; z <= Integer.parseInt(str_cords[2])+Integer.parseInt(str_size[2])+0.5; z++) {
                                                    Location compact_loc = new Location(Main.getWorldManager().getCompactWorld(), x, y, z);
                                                    if(compact_loc.getBlock().getType().isTransparent() ||compact_loc.getBlock().isPassable()) {
                                                        if(compact_loc.getBlock().getRelative(BlockFace.UP).getType().isTransparent() ||compact_loc.getBlock().getRelative(BlockFace.UP).isPassable()) {
                                                            Player player = (Player) e.getWhoClicked();
                                                            Main.getCompactPlayerHandler().onPlayerEnter(player);
                                                            player.teleport(compact_loc);
                                                            break outerloop;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else if (e.getSlot() >= 9 && e.getSlot() <= 14) {
                                        ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
                                        String[] sides = itemMeta.getDisplayName().split(": ");
                                        if(e.getClick().isLeftClick() && !e.getClick().isShiftClick()) {
                                            for (int i = 0; i < modes.length; i++) {
                                                if (modes[i].equals(sides[1])) {
                                                    if (i == modes.length - 1) {
                                                        sides[1] = modes[0];
                                                    } else {
                                                        sides[1] = modes[i + 1];
                                                    }
                                                    if (sides[0].equals("Up")) {
                                                        if (i == 0) {
                                                            sides[1] = modes[i + 2];
                                                        }
                                                    } else if (sides[0].equals("Down")) {
                                                        if (i == 4) {
                                                            sides[1] = modes[1];
                                                        }
                                                    }
                                                    break;
                                                }
                                            }
                                            if (sides[1].equals("Item Input")) {
                                                inv.setItem(e.getSlot(), Util.ItemStack(Material.HOPPER, 1, sides[0] + ": " + sides[1], itemMeta.getLore().get(0)));
                                                inv.setItem(e.getSlot() + 9, null);
                                            }
                                            if (sides[1].equals("Item Output")) {
                                                inv.setItem(e.getSlot(), Util.ItemStack(Material.DROPPER, 1, sides[0] + ": " + sides[1], itemMeta.getLore().get(0)));
                                                inv.setItem(e.getSlot() + 9, null);
                                            }
                                            if (sides[1].equals("Redstone Input")) {
                                                inv.setItem(e.getSlot(), Util.ItemStack(Material.OBSERVER, 1, sides[0] + ": " + sides[1], itemMeta.getLore().get(0)));
                                                inv.setItem(e.getSlot() + 9, Util.ItemStack(Material.BARRIER, 1, ""));
                                            }
                                            if (sides[1].equals("Redstone Output")) {
                                                inv.setItem(e.getSlot(), Util.ItemStack(Material.NOTE_BLOCK, 1, sides[0] + ": " + sides[1], itemMeta.getLore().get(0)));
                                                inv.setItem(e.getSlot() + 9, Util.ItemStack(Material.BARRIER, 1, ""));
                                            }
                                            if (sides[1].equals("None")) {
                                                inv.setItem(e.getSlot(), Util.ItemStack(Material.BARRIER, 1, sides[0] + ": " + sides[1], itemMeta.getLore().get(0)));
                                                inv.setItem(e.getSlot() + 9, Util.ItemStack(Material.BARRIER, 1, ""));
                                            }
                                        } else if(e.getClick().isShiftClick()) {
                                            String[] values = itemMeta.getLore().get(0).split(";");
                                            String[] str_cords = inv.getItem(26).getItemMeta().getDisplayName().split(";");
                                            String[] str_size = inv.getItem(3).getItemMeta().getDisplayName().replace("Size: ", "").split("x");
                                            int x = Integer.parseInt(values[0]);
                                            int y = Integer.parseInt(values[1]);
                                            int z = Integer.parseInt(values[2]);
                                            int min_x = Integer.parseInt(str_cords[0])+1;
                                            int min_y = Integer.parseInt(str_cords[1])+1;
                                            int min_z = Integer.parseInt(str_cords[2])+1;
                                            int max_x = min_x+Integer.parseInt(str_size[0]);
                                            int max_y = min_y+Integer.parseInt(str_size[1]);
                                            int max_z = min_z+Integer.parseInt(str_size[2]);
                                            BlockFace face = BlockFace.valueOf(sides[0].toUpperCase());
                                            if(face.equals(BlockFace.SOUTH) || face.equals(BlockFace.NORTH)) {
                                                if(e.getClick().isRightClick()) {
                                                    if(x+2 <= max_x) {
                                                        x++;
                                                    } else {x = min_x;}
                                                } else if(e.getClick().isLeftClick()) {
                                                    if(y+2 <= max_y) {
                                                        y++;
                                                    } else {y = min_y;}
                                                }
                                            }
                                            else if(face.equals(BlockFace.WEST) || face.equals(BlockFace.EAST)) {
                                                if(e.getClick().isRightClick()) {
                                                    if(z+2 <= max_z) {
                                                        z++;
                                                    } else {z = min_z;}
                                                } else if(e.getClick().isLeftClick()) {
                                                    if(y+2 <= max_y) {
                                                        y++;
                                                    } else {y = min_y;}
                                                }
                                            }
                                            else if(face.equals(BlockFace.UP) || face.equals(BlockFace.DOWN)) {
                                                if(e.getClick().isRightClick()) {
                                                    if(z+2 <= max_z) {
                                                        z++;
                                                    } else {z = min_z;}
                                                } else if(e.getClick().isLeftClick()) {
                                                    if(x+2 <= max_x) {
                                                        x++;
                                                    } else {x = min_x;}
                                                }
                                            }
                                            List<String> lore = itemMeta.getLore();
                                            lore.set(0, x+";"+y+";"+z);
                                            itemMeta.setLore(lore);
                                            e.getCurrentItem().setItemMeta(itemMeta);
                                        }
                                    }
                                    else if(e.getSlot() == 17) {
                                        String[] str_cords = inv.getItem(26).getItemMeta().getDisplayName().split(";");
                                        String[] str_size = inv.getItem(3).getItemMeta().getDisplayName().replace("Size: ", "").split("x");

                                        Main.getCompactChunkHandler().loadChunks(Integer.parseInt(str_cords[0]), Integer.parseInt(str_cords[2]), Integer.parseInt(str_size[0]), Integer.parseInt(str_size[2]));
                                        if(!Bukkit.getScheduler().isQueued(Integer.parseInt(inv.getItem(5).getItemMeta().getDisplayName())) && !Bukkit.getScheduler().isCurrentlyRunning(Integer.parseInt(inv.getItem(5).getItemMeta().getDisplayName()))) {
                                            Main.getCompactChunkHandler().startTask(inv, ((ShulkerBox) inv.getHolder()).getBlock());
                                        }
                                        e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&',lang.getString("gui.load")));
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
    public void onGuiClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        if(inv.getHolder() instanceof ShulkerBox) {
            if (inv.getItem(2) != null) {
                if (inv.getItem(2).hasItemMeta()) {
                    if (inv.getItem(2).getItemMeta().hasLore()) {
                        if (inv.getItem(2).getItemMeta().getLore().contains("Compact")) {
                            String[] str_cords = inv.getItem(26).getItemMeta().getDisplayName().split(";");
                            String[] str_size = inv.getItem(3).getItemMeta().getDisplayName().replace("Size: ", "").split("x");
                            Location loc = new Location(Main.getWorldManager().getCompactWorld(), Integer.parseInt(str_cords[0]), Integer.parseInt(str_cords[1]), Integer.parseInt(str_cords[2]));
                            int sx = Integer.parseInt(str_size[0]);
                            int sy = Integer.parseInt(str_size[1]);
                            int sz = Integer.parseInt(str_size[2]);
                            for (int x = 0; x < sx+2; x++) {
                                for (int y = 0; y < sy+2; y++) {
                                    for (int z = 0; z < sz+2; z++) {
                                        if (x <= 0 || y == 0 || z <= 0 || x >= sx+1 || y == sy+1 || z >= sz+1) {
                                            if(!(x > sx+2 || y > sy+1 || z > sz+2)) {
                                                Block block = loc.clone().add(x, y, z).getBlock();
                                                if(!block.getType().equals(Material.BEDROCK)) {
                                                    block.setType(Material.BEDROCK);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            for (int i = 9; i <= 14; i++) {
                                ItemMeta itemMeta = inv.getItem(i).getItemMeta();
                                String[] sides = itemMeta.getDisplayName().split(": ");
                                Main.getWorldManager().sideModeChange(sides[0], sides[1], itemMeta.getLore().get(0));
                            }
                        }
                    }
                }
            }
        }
    }

}
