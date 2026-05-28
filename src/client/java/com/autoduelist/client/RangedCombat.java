package com.autoduelist.client;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.InteractionHand;

public class RangedCombat {
    private static int chargeTicks = 0;
    private static boolean isCharging = false;
    private static final int MAX_BOW_CHARGE = 20;

    public static void update(Player player, LivingEntity target) {
        if (target == null || !target.isAlive()) {
            stopCharging(player);
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        double distance = player.distanceToSqr(target);

        if (mainHand.getItem() instanceof BowItem) {
            handleBowCombat(player, target, distance);
        } else if (mainHand.getItem() instanceof CrossbowItem) {
            handleCrossbowCombat(player, target, distance);
        } else if (mainHand.getItem() instanceof TridentItem) {
            handleTridentCombat(player, target, distance);
        } else {
            stopCharging(player);
        }
    }

    private static void handleBowCombat(Player player, LivingEntity target, double distance) {
        if (distance > 100 && distance < 1024) {
            lookAtTarget(player, target);
            
            if (!isCharging) {
                player.startUsingItem(InteractionHand.MAIN_HAND);
                isCharging = true;
                chargeTicks = 0;
            } else {
                chargeTicks++;
                if (chargeTicks >= MAX_BOW_CHARGE) {
                    player.stopUsingItem();
                    isCharging = false;
                    chargeTicks = 0;
                }
            }
        } else {
            stopCharging(player);
        }
    }

    private static void handleCrossbowCombat(Player player, LivingEntity target, double distance) {
        ItemStack crossbow = player.getMainHandItem();
        
        if (CrossbowItem.isCharged(crossbow)) {
            if (distance > 100 && distance < 1024) {
                lookAtTarget(player, target);
                player.stopUsingItem();
            }
        } else {
            if (!isCharging) {
                player.startUsingItem(InteractionHand.MAIN_HAND);
                isCharging = true;
            }
        }
    }

    private static void handleTridentCombat(Player player, LivingEntity target, double distance) {
        if (distance > 25 && distance < 400) {
            lookAtTarget(player, target);
            
            if (!isCharging) {
                player.startUsingItem(InteractionHand.MAIN_HAND);
                isCharging = true;
                chargeTicks = 0;
            } else {
                chargeTicks++;
                if (chargeTicks >= 10) {
                    player.stopUsingItem();
                    isCharging = false;
                    chargeTicks = 0;
                }
            }
        } else if (distance <= 25) {
            stopCharging(player);
        }
    }

    private static void stopCharging(Player player) {
        if (isCharging) {
            player.stopUsingItem();
            isCharging = false;
            chargeTicks = 0;
        }
    }

    private static void lookAtTarget(Player player, LivingEntity target) {
        double dx = target.getX() - player.getX();
        double dy = target.getY() + target.getEyeHeight() - player.getY() - player.getEyeHeight();
        double dz = target.getZ() - player.getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance > 0.1) {
            double yaw = Math.atan2(dz, dx) * 180.0 / Math.PI - 90.0;
            double pitch = -Math.asin(dy / distance) * 180.0 / Math.PI;
            player.setXRot((float) pitch);
            player.setYRot((float) yaw);
        }
    }

    public static boolean isUsingRangedWeapon(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        return mainHand.getItem() instanceof BowItem || 
               mainHand.getItem() instanceof CrossbowItem || 
               mainHand.getItem() instanceof TridentItem;
    }

    public static void reset() {
        isCharging = false;
        chargeTicks = 0;
    }
}
