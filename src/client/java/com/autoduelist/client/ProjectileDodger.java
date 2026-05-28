package com.autoduelist.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public class ProjectileDodger {
    private static final double DETECTION_RANGE = 16.0;
    private static int dodgeCooldown = 0;
    private static DodgeDirection lastDodge = DodgeDirection.NONE;

    public enum DodgeDirection {
        LEFT, RIGHT, NONE
    }

    public static void update(Player player) {
        if (dodgeCooldown > 0) {
            dodgeCooldown--;
            return;
        }

        Projectile dangerousProjectile = detectIncomingProjectile(player);
        
        if (dangerousProjectile != null) {
            performDodge(player, dangerousProjectile);
        }
    }

    private static Projectile detectIncomingProjectile(Player player) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return null;

        AABB searchBox = player.getBoundingBox().inflate(DETECTION_RANGE);
        List<Projectile> projectiles = minecraft.level.getEntitiesOfClass(
            Projectile.class, 
            searchBox,
            p -> p != null && isHeadingTowardsPlayer(p, player)
        );

        Projectile nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Projectile projectile : projectiles) {
            double distance = player.distanceToSqr(projectile);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = projectile;
            }
        }

        return nearest;
    }

    private static boolean isHeadingTowardsPlayer(Projectile projectile, Player player) {
        Vec3 projectilePos = projectile.position();
        Vec3 playerPos = player.position();
        Vec3 projectileVelocity = projectile.getDeltaMovement();

        Vec3 toPlayer = playerPos.subtract(projectilePos).normalize();
        Vec3 velocity = projectileVelocity.normalize();

        double dotProduct = velocity.dot(toPlayer);
        
        return dotProduct > 0.7;
    }

    private static void performDodge(Player player, Projectile projectile) {
        DodgeDirection direction;
        if (lastDodge == DodgeDirection.LEFT) {
            direction = DodgeDirection.RIGHT;
        } else {
            direction = DodgeDirection.LEFT;
        }
        
        if (direction == DodgeDirection.LEFT) {
            player.xxa = -1.0f;
        } else {
            player.xxa = 1.0f;
        }
        
        double distance = player.distanceToSqr(projectile);
        if (distance < 9.0 && player.onGround()) {
            player.setDeltaMovement(
                player.getDeltaMovement().x, 
                0.42f, 
                player.getDeltaMovement().z
            );
        }
        
        lastDodge = direction;
        dodgeCooldown = 15;
    }

    public static boolean isDodging() {
        return dodgeCooldown > 0;
    }

    public static void reset() {
        dodgeCooldown = 0;
        lastDodge = DodgeDirection.NONE;
    }
}
