package CompacContraption.Manager;

import CompacContraption.Main;
import CompacContraption.Util.Util;
import org.bukkit.*;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class CraftingManager implements Listener {
    private static ConfigurationSection crafting;
    private ArrayList<NamespacedKey> customRecipes = new ArrayList<NamespacedKey>();

    public ArrayList<NamespacedKey> getCustomRecipes() {
        return customRecipes;
    }

    public void setupRecipes(Plugin plugin) {
        this.crafting = Main.getPluginCrafting();
        ConfigurationSection recipes = this.crafting.getConfigurationSection("CompactContraption");
        for(String s : recipes.getKeys(false)) {
            Main.getInstance().getLogger().info("Loading CompactContraption: "+s);
            ConfigurationSection recipe = recipes.getConfigurationSection(s);
            Main.getInstance().getLogger().info("With size: "+recipe.getInt("SizeX")+"x"+recipe.getInt("SizeY")+"x"+recipe.getInt("SizeZ"));
            ItemStack item = Util.ItemStack(Material.SHULKER_BOX, 1, ChatColor.translateAlternateColorCodes('&', recipe.getString("Name")), "SizeX:"+recipe.getInt("SizeX"), "SizeY:"+recipe.getInt("SizeY"), "SizeZ:"+recipe.getInt("SizeZ"));
            NamespacedKey itemKey = new NamespacedKey(plugin, s);
            customRecipes.add(itemKey);
            ShapedRecipe itemRecipe = new ShapedRecipe(itemKey,item);
            itemRecipe.shape(recipe.getString("crafting.1"), recipe.getString("crafting.2"), recipe.getString("crafting.3"));
            ConfigurationSection cs = recipe.getConfigurationSection("crafting.materials");
            for(String s1 : cs.getKeys(false)) {
                // Main.getInstance().getLogger().info(s.charAt(0)+" material: "+Material.getMaterial(cs.getString(s)));
                itemRecipe.setIngredient(s1.charAt(0), Material.getMaterial(cs.getString(s1)));
            }
            plugin.getServer().addRecipe(itemRecipe);
        }
    }

    @EventHandler
    public void onItemCraft(CraftItemEvent e) {
        ItemStack item = e.getCurrentItem();
        if(item.getType().equals(Material.SHULKER_BOX)) {
            if(item.hasItemMeta()) {
                ItemMeta itemMeta = item.getItemMeta();
                if(itemMeta.hasLore()) {
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

                    //call worldmanager box creator
                    Main.getWorldManager().boxCreater(sizeX, sizeY, sizeZ);

                }
            }
        }
    }
}
