import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Enimport org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
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

public class StackPlugin extends JavaPlugin implements Listener {

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

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        Entity entity = event.getEntity();
        Entity target = event.getTarget();
        if (entity instanceof LivingEntity && target instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            LivingEntity targetEntity = (LivingEntity) target;
            if (isWithinRange(livingEntity, targetEntity) && areSameType(livingEntity, targetEntity)) {
                stackMobs(livingEntity, targetEntity);
            }
        }
    }

    private boolean isWithinRange(LivingEntity entity1, LivingEntity entity2) {
        Location location1 = entity1.getLocation();
        Location location2 = entity2.getLocation();
        double distance = location1.distance(location2);
        return distance <= STACK_RANGE;
    }

    private boolean areSameType(LivingEntity entity1, LivingEntity entity2) {
        return entity1.getType() == entity2.getType();
    }

    private void stackMobs(LivingEntity entity1, LivingEntity entity2) {
        stackedMobs.put(entity1, entity2);

        // Apply TNT-like effect to the top mob
        entity1.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0));

        // Prevent the top mob from flying
        entity1.setAI(false);

        // Set the title of the stacked mob
        updateMobTitle(entity1, stackedMobs.size());

        // Teleport the top mob slightly higher to avoid collision
        Location location = entity1.getLocation();
        entity1.teleport(location.add(0, 0.2, 0));
    }

    private void updateMobTitle(LivingEntity entity, int stackSize) {
        // Set the custom title based on the stack size
        String itemName = entity.getName(); // Replace with the actual item name
        String customTitle = "X" + stackSize + " " + itemName;
        entity.setCustomName(customTitle);
        entity.setCustomNameVisible(true);
    }
}
tity;
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
