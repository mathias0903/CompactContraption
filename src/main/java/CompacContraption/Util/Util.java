package CompacContraption.Util;

import org.apache.commons.lang.math.IntRange;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Util {
    public static ItemStack ItemStack(Material item, int amount, String display) {
        ItemStack customitem = new ItemStack(item, amount);
        ItemMeta custommeta = customitem.getItemMeta();
        custommeta.setDisplayName(display);
        customitem.setItemMeta(custommeta);
        return customitem;
    }
    public static ItemStack ItemStack(Material item, int amount, String display, String... lore) {
        ItemStack customitem = new ItemStack(item, amount);
        ItemMeta custommeta = customitem.getItemMeta();
        custommeta.setDisplayName(display);
        custommeta.setLore(Arrays.asList(lore));
        customitem.setItemMeta(custommeta);
        return customitem;
    }
    public static boolean inCuboid(Location origin, Location l1, Location l2){
        return new IntRange(l1.getX(), l2.getX()).containsDouble(origin.getX())
                && new IntRange(l1.getY(), l2.getY()).containsDouble(origin.getY())
                &&  new IntRange(l1.getZ(), l2.getZ()).containsDouble(origin.getZ());
    }
}
