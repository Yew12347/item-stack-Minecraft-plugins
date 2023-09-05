import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MobStackPlugin extends JavaPlugin implements Listener {

    private final Map<UUID, List<LivingEntity>> stackedMobs = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (stackedMobs.containsKey(entity.getUniqueId())) {
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

    private void stackMobs(LivingEntity entity1, LivingEntity entity2) {
        List<LivingEntity> stack = stackedMobs.computeIfAbsent(entity1.getUniqueId(), k -> new ArrayList<>());
        stack.add(entity2);

        // Apply TNT-like effect to the top mob
        entity1.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0));

        // Hide the bottom mob (optional)
        entity2.setInvisible(true);

        // Prevent bottom mob from taking fall damage and top mob from flying
        entity1.setFallDistance(0);
        entity2.setFallDistance(0);

        // Disable AI actions like flying for the top mob (optional)
        entity1.setAI(false);
    }
  }
