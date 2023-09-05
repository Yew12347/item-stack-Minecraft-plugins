import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class StackItemsPlugin extends JavaPlugin implements Listener {

    private static final double STACK_RANGE = 1.0; // Adjust the range as needed
    private static final int STACK_AMOUNT = 2; // Adjust the stack amount as needed

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Item newItem = event.getEntity();

        // Check for nearby item entities
        List<Item> nearbyItems = getNearbyItems(newItem.getLocation());

        for (Item nearbyItem : nearbyItems) {
            ItemStack newItemStack = newItem.getItemStack();
            ItemStack nearbyItemStack = nearbyItem.getItemStack();

            // Check if the items are of the same type and can be stacked
            if (canStackItems(newItemStack, nearbyItemStack)) {
                // Combine the items and set the new stack size
                nearbyItemStack.setAmount(nearbyItemStack.getAmount() + newItemStack.getAmount());
                nearbyItem.setItemStack(nearbyItemStack);
                newItem.remove(); // Remove the spawned item
                return; // No need to continue checking other nearby items
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        List<ItemStack> drops = event.getDrops();
        List<ItemStack> newDrops = new ArrayList<>();

        for (ItemStack drop : drops) {
            // Check for nearby item entities when a mob or entity dies and drops items
            List<Item> nearbyItems = getNearbyItems(event.getEntity().getLocation());

            boolean stacked = false;

            for (Item nearbyItem : nearbyItems) {
                ItemStack nearbyItemStack = nearbyItem.getItemStack();

                // Check if the items are of the same type and can be stacked
                if (canStackItems(drop, nearbyItemStack)) {
                    // Combine the items and set the new stack size
                    nearbyItemStack.setAmount(nearbyItemStack.getAmount() + drop.getAmount());
                    nearbyItem.setItemStack(nearbyItemStack);
                    stacked = true;
                    break; // No need to continue checking other nearby items
                }
            }

            if (!stacked) {
                newDrops.add(drop);
            }
        }

        drops.clear();
        drops.addAll(newDrops);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        // Prevent players from dropping items separately (items will stack automatically)
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        // Prevent items from being destroyed by fire
        if (event.getEntity() instanceof Item) {
            event.setCancelled(true);
        }
    }

    private List<Item> getNearbyItems(Location location) {
        List<Item> nearbyItems = new ArrayList<>();
        for (Entity entity : location.getWorld().getNearbyEntities(location, STACK_RANGE, STACK_RANGE, STACK_RANGE)) {
            if (entity instanceof Item) {
                nearbyItems.add((Item) entity);
            }
        }
        return nearbyItems;
    }

    private boolean canStackItems(ItemStack itemStack1, ItemStack itemStack2) {
        return itemStack1.isSimilar(itemStack2) && itemStack1.getAmount() + itemStack2.getAmount() <= itemStack1.getMaxStackSize();
    }
}
