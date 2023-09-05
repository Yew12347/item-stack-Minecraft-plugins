import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MobStackPlugin extends JavaPlugin implements Listener {

    private final Map<LivingEntity, List<LivingEntity>> stackedMobs = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        List<LivingEntity> mobs = event.getEntity().getNearbyEntities(4.0, 4.0, 4.0, entity -> entity instanceof LivingEntity);

        if (mobs.isEmpty()) {
            return; // No nearby mobs to stack with
        }

        LivingEntity topMob = event.getEntity();
        for (LivingEntity nearbyMob : mobs) {
            if (nearbyMob.equals(topMob)) {
                continue; // Skip checking against itself
            }

            // Implement your mob stacking logic here
            // Add nearbyMob on top of topMob
            // Adjust entity properties as needed
            topMob.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0));

            // Hide the nearbyMob (optional)
            // nearbyMob.setInvisible(true);

            // Prevent nearbyMob from taking fall damage and topMob from flying (optional)
            topMob.setFallDistance(0);
            nearbyMob.setFallDistance(0);

            // Disable AI actions like flying for the topMob (optional)
            topMob.setAI(false);

            // You may want to remove the nearbyMob from the world
            nearbyMob.remove();

            // Add nearbyMob to the stack of topMob
            // You should manage this data structure to track stacked mobs
            // stackedMobs.computeIfAbsent(topMob, k -> new ArrayList<>()).add(nearbyMob);
        }
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
        return distance >= 1 && distance <= 4;
    }

    private boolean areSameType(LivingEntity entity1, LivingEntity entity2) {
        return entity1.getType() == entity2.getType();
    }

    // Implement your stackMobs method as needed

          }
