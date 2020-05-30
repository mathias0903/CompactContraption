package CompacContraption.Manager;

import CompacContraption.Main;
import CompacContraption.Util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CommandManager implements CommandExecutor {
    public boolean onCommand(CommandSender cs, Command command, String s, String[] args) {
        if (cs instanceof Player) {
            if(cs.hasPermission("compactContraption.admin")){
                if(args.length >= 1 && args.length <= 2) {
                    if(args[0].equals("get")) {
                        if(args[1] != null) {
                            ConfigurationSection recipe = Main.getPluginCrafting().getConfigurationSection("CompactContraption."+args[1]);
                            ItemStack item = Util.ItemStack(Material.SHULKER_BOX, 1, ChatColor.translateAlternateColorCodes('&', recipe.getString("Name")), "SizeX:"+recipe.getInt("SizeX"), "SizeY:"+recipe.getInt("SizeY"), "SizeZ:"+recipe.getInt("SizeZ"));
                            ItemMeta itemMeta = item.getItemMeta();
                            List<String> lore = itemMeta.getLore();
                            int sizeX = Integer.parseInt(lore.get(0).substring(6));
                            int sizeY = Integer.parseInt(lore.get(1).substring(6));
                            int sizeZ = Integer.parseInt(lore.get(2).substring(6));
                            UUID uuid = UUID.randomUUID();
                            ShulkerBox shulker = (ShulkerBox) ((BlockStateMeta)itemMeta).getBlockState();
                            Inventory inv = Bukkit.createInventory(null, 27, itemMeta.getDisplayName());

                            inv.setItem(2, Util.ItemStack(Material.PAPER, 1,uuid.toString(), "Compact"));
                            inv.setItem(3, Util.ItemStack(Material.BOOK, 1, "Size: "+sizeX+"x"+sizeY+"x"+sizeZ));
                            inv.setItem(4, Util.ItemStack(Material.BEACON, 1, "Spawn Position: 1;1;1"));
                            inv.setItem(8, Util.ItemStack(Material.LIME_CONCRETE, 1, "Enter Block"));
                            inv.setItem(17, Util.ItemStack(Material.HEART_OF_THE_SEA, 1, "Load CompactContraption"));

                            Location loc = Main.getWorldManager().getNextLoc();
                            inv.setItem(26, Util.ItemStack(Material.BEDROCK, 1, (int)loc.getX()+";"+(int)loc.getY()+";"+(int)loc.getZ()));

                            int mx = (int) loc.getX()+(int) Math.ceil((1+sizeX)/2.0);
                            int my = (int) loc.getY()+(int) Math.ceil((1+sizeY)/2.0);
                            int mz = (int) loc.getZ()+(int) Math.ceil((1+sizeZ)/2.0);

                            int tx = (int) loc.getX()+sizeX+1;
                            int ty = (int) loc.getY()+sizeY+1;
                            int tz = (int) loc.getZ()+sizeZ+1;

                            inv.setItem(9, Util.ItemStack(Material.BARRIER, 1,"Up: None", mx+";"+ty+";"+mz));
                            inv.setItem(10, Util.ItemStack(Material.BARRIER, 1,"Down: None",mx+";"+(int)loc.getY()+";"+mz));
                            inv.setItem(11, Util.ItemStack(Material.BARRIER, 1,"North: None",mx+";"+my+";"+(int)loc.getZ()));
                            inv.setItem(12, Util.ItemStack(Material.BARRIER, 1,"East: None",tx+";"+my+";"+mz));
                            inv.setItem(13, Util.ItemStack(Material.BARRIER, 1,"South: None",mx+";"+my+";"+tz));
                            inv.setItem(14, Util.ItemStack(Material.BARRIER, 1,"West: None",(int)loc.getX()+";"+my+";"+mz));
                            for(int i=6; i>0; i--) {
                                inv.setItem(17+i, Util.ItemStack(Material.BARRIER, 1,""));
                            }

                            List<Integer> slots = Arrays.asList(5,6,7,15,16,24,25);
                            for(int slot: slots) {
                                inv.setItem(slot, Util.ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1,""));
                            }
                            shulker.getInventory().setContents(inv.getContents());
                            ((BlockStateMeta)itemMeta).setBlockState(shulker);
                            item.setItemMeta(itemMeta);
                            ((Player) cs).getInventory().addItem(item);

                            //call worldmanager box creator
                            Main.getWorldManager().boxCreater(sizeX, sizeY, sizeZ);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
