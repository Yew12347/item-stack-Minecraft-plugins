import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("stackmobs")) {
            if (sender instanceof LivingEntity) {
                stackMobs((LivingEntity) sender);
            } else {
                sender.sendMessage("You must be a player to use this command.");
            }
            return true;
        }
        return false;
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
        // Implement your mob stacking logic here
        // Add entity2 to the stack of entity1
        // Adjust entity properties as needed
        
        // Apply TNT-like effect to the top mob (optional)
        entity1.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0));
        
        // Hide the bottom mob (optional)
        entity2.setInvisible(true);
    }
  }
