package com.autoduelist.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import java.util.List;

public class AutoDuelistAI {
    private static boolean isEnabled = false;
    private static final float DETECTION_RANGE = 32.0f;
    private static LivingEntity currentTarget = null;
    private static CombatStrategy currentStrategy = null;
    private static int tickCounter = 0;
    private static final int RETARGET_INTERVAL = 10;

    public static void toggleAI() {
        isEnabled = !isEnabled;
        if (!isEnabled) {
            currentStrategy = null;
            ComboAttack.reset();
            WeaponSwitcher.reset();
            ProjectileDodger.reset();
            RangedCombat.reset();
            MaceCombat.reset();
            WindChargeCombat.reset();
            PotionManager.reset();
        }
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static void update() {
        if (!isEnabled) {
            currentTarget = null;
            currentStrategy = null;
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null) return;

        tickCounter++;

        // Retarget mỗi 10 tick
        if (tickCounter >= RETARGET_INTERVAL) {
            tickCounter = 0;
            currentTarget = findNearestTarget(player);
            
            // Tạo strategy mới nếu có target
            if (currentTarget != null) {
                CombatAnalyzer.EnemyAnalysis analysis = CombatAnalyzer.analyzeEnemy(currentTarget);
                currentStrategy = new CombatStrategy(player, currentTarget, analysis);
            } else {
                currentStrategy = null;
            }
        }

        // Chạy combat strategy nếu có target
        if (currentStrategy != null && currentTarget != null && currentTarget.isAlive()) {
            currentStrategy.executeCombatTick();
        } else {
            currentTarget = null;
            currentStrategy = null;
        }
    }

    @SuppressWarnings("null")
    private static LivingEntity findNearestTarget(Player player) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return null;
        
        AABB searchBox = player.getBoundingBox().inflate(DETECTION_RANGE);
        List<Entity> nearbyEntities = minecraft.level.getEntities(player, searchBox);

        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Entity entity : nearbyEntities) {
            if (entity == player || !(entity instanceof LivingEntity)) continue;

            LivingEntity living = (LivingEntity) entity;
            if (living.isAlive() && isValidTarget(living)) {
                double distance = player.distanceToSqr(living);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = living;
                }
            }
        }

        return nearest;
    }

    private static boolean isValidTarget(LivingEntity entity) {
        // Phân loại target
        if (entity instanceof Monster) return true;
        if (entity instanceof Player) return true;

        return false;
    }

    @SuppressWarnings("null")
    public static void performAttack(LivingEntity target) {
        Minecraft gameInstance = Minecraft.getInstance();
        if (gameInstance.gameMode != null && gameInstance.player != null) {
            gameInstance.gameMode.attack(gameInstance.player, target);
        }
    }

    public static LivingEntity getCurrentTarget() {
        return currentTarget;
    }

    public static CombatStrategy getCurrentStrategy() {
        return currentStrategy;
    }
}
