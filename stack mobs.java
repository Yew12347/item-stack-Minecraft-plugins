import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelayedMobStackPlugin extends JavaPlugin implements Listener {

    private static final double STACK_DISTANCE_MIN = 1.0; // Minimum distance for stacking
    private static final double STACK_DISTANCE_MAX = 4.0; // Maximum distance for stacking
    private static final long STACK_DELAY = 30L; // Delay in ticks (1.5 seconds)

    private final Map<LivingEntity, List<LivingEntity>> stackedMobs = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            // Automatically check and stack mobs when they spawn
            checkAndStackMobs(entity);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity && stackedMobs.containsKey(entity)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        Entity entity = event.getEntity();
        Entity target = event.getTarget();
        if (entity instanceof LivingEntity && target instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            LivingEntity targetEntity = (LivingEntity) target;
            // Check and stack mobs when they come into contact
            checkAndStackMobsWithDelay(livingEntity, targetEntity);
        }
    }

    private void checkAndStackMobs(LivingEntity entity) {
        Location entityLocation = entity.getLocation();

        // Iterate through nearby entities to find potential stacks
        for (Entity nearbyEntity : entity.getNearbyEntities(STACK_DISTANCE_MAX, STACK_DISTANCE_MAX, STACK_DISTANCE_MAX)) {
            if (nearbyEntity instanceof LivingEntity) {
                LivingEntity nearbyLivingEntity = (LivingEntity) nearbyEntity;

                // Check if the entities are of the same type and can be stacked
                if (areSameType(entity, nearbyLivingEntity)) {
                    Location nearbyLocation = nearbyLivingEntity.getLocation();
                    double distance = entityLocation.distance(nearbyLocation);

                    // Check if the distance is within the stacking range
                    if (distance >= STACK_DISTANCE_MIN && distance <= STACK_DISTANCE_MAX) {
                        stackMobs(entity, nearbyLivingEntity);
                    }
                }
            }
        }
    }

    private void checkAndStackMobsWithDelay(LivingEntity entity1, LivingEntity entity2) {
        // Check if the entities are of the same type and can be stacked
        if (areSameType(entity1, entity2)) {
            Location location1 = entity1.getLocation();
            Location location2 = entity2.getLocation();
            double distance = location1.distance(location2);

            // Check if the distance is within the stacking range
            if (distance >= STACK_DISTANCE_MIN && distance <= STACK_DISTANCE_MAX) {
                // Delay the stacking process
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        stackMobs(entity1, entity2);
                    }
                }.runTaskLater(this, STACK_DELAY);
            }
        }
    }

    private boolean areSameType(LivingEntity entity1, LivingEntity entity2) {
        return entity1.getType() == entity2.getType();
    }

    private void stackMobs(LivingEntity entity1, LivingEntity entity2) {
        // Implement your mob stacking logic here
        // Add entity2 to the stack of entity1
        // Adjust entity properties as needed

        // Apply TNT-like effect to the top mob (optional)
        entity1.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0));

        // Hide the bottom mob (optional)
        // entity2.setInvisible(true);

        // Add entity2 to the list of stacked mobs under entity1
        if (!stackedMobs.containsKey(entity1)) {
            stackedMobs.put(entity1, new ArrayList<>());
        }
        stackedMobs.get(entity1).add(entity2);
    }
}
