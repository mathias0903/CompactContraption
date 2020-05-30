package CompacContraption.Events;

import CompacContraption.Main;
import CompacContraption.Util.Config;
import CompacContraption.Util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CompactPlayerHandler implements Listener {
    HashMap<Player, Location> last_loc = new HashMap<Player, Location>();

    public HashMap<Player, Location> getLast_loc() {
        return last_loc;
    }
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        final Player p = e.getPlayer();
        ConfigurationSection c = new Config("data", Main.getInstance()).getConfig();
        if(!last_loc.containsKey(p)) {
            if(c.isSet("Players."+p.getUniqueId().toString())) {
                String[] str = c.getString("Players."+p.getUniqueId().toString()).split(";");
                last_loc.put(p, new Location(Bukkit.getWorld(str[0]), Double.parseDouble(str[1]), Double.parseDouble(str[2]), Double.parseDouble(str[3])));
            }
        }
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable(){
            public void run(){
                p.discoverRecipes(Main.getCraftingManager().getCustomRecipes());
                if(p.getWorld().equals(Main.getWorldManager().getCompactWorld())) {
                    if (last_loc.get(p) != null) {
                        playerLeave(p);
                    } else if(p.getBedSpawnLocation() != null) {
                        p.teleport(p.getBedSpawnLocation());
                    } else {
                        p.performCommand("spawn");
                    }
                }
            }
        }, 1);

    }
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent e) {
        if(last_loc.containsKey(e.getPlayer())) {
            Player p = e.getPlayer();
            Location loc = last_loc.get(p);
            Config c = new Config("data", Main.getInstance());
            c.set("Players."+p.getUniqueId().toString(), loc.getWorld().getName()+";"+loc.getX()+";"+loc.getY()+";"+loc.getZ());
            c.save();
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null) {
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (e.getClickedBlock().getType().equals(Material.BEDROCK)) {
                    Player player = e.getPlayer();
                    if (player.getLocation().getWorld().equals(Main.getWorldManager().getCompactWorld())) {
                        if (player.isSneaking()) {
                            if(e.getItem() == null) {
                                playerLeave(player);
                            }
                        }
                    }
                }
            }
        }
    }

    public void onPlayerEnter(Player player) {
        last_loc.put(player, player.getLocation());
    }

    public void playerLeave(Player player) {
        player.teleport(last_loc.get(player));
        last_loc.remove(player);
    }
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        if(e.getPlayer().getWorld().equals(Main.getWorldManager().getCompactWorld())) {
            String command = e.getMessage().replace("/", "");
            if (Main.getPluginConfig().getBoolean("Commands.Whitelist")) {
                if (!Main.getPluginConfig().getStringList("Commands.List").contains("command")) {
                    e.setCancelled(true);
                }
            } else if (!Main.getPluginConfig().getBoolean("Commands.Whitelist")) {
                if (Main.getPluginConfig().getStringList("Commands.List").contains("command")) {
                    e.setCancelled(true);
                }
            }
            if(e.getPlayer().isOp()) {
                e.setCancelled(false);
            }
        }
    }
}
