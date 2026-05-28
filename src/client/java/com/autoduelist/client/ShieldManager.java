package com.autoduelist.client;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;

public class ShieldManager {
    private static int blockTicks = 0;
    private static boolean wasBlocking = false;

    public static void update(Player player, LivingEntity target) {
        if (target == null || !target.isAlive()) {
            stopBlocking(player);
            return;
        }

        if (blockTicks > 0) {
            blockTicks--;
        }

        if (shouldBlock(player, target)) {
            startBlocking(player);
        } else {
            stopBlocking(player);
        }
    }

    private static boolean shouldBlock(Player player, LivingEntity target) {
        if (!hasShield(player)) return false;

        double distance = player.distanceToSqr(target);
        float health = player.getHealth();

        // Block khi enemy gần và đang tấn công
        if (distance < 16.0 && target.getAttackAnim(0.5f) > 0.5f) {
            return true;
        }

        // Block khi máu thấp và enemy gần
        if (health < 8.0f && distance < 25.0) {
            return true;
        }

        return false;
    }

    private static boolean hasShield(Player player) {
        ItemStack offHand = player.getItemBySlot(EquipmentSlot.OFFHAND);
        ItemStack mainHand = player.getMainHandItem();
        return offHand.is(Items.SHIELD) || mainHand.is(Items.SHIELD);
    }

    private static void startBlocking(Player player) {
        if (!wasBlocking) {
            player.startUsingItem(hasShieldInOffhand(player) ? 
                net.minecraft.world.InteractionHand.OFF_HAND : 
                net.minecraft.world.InteractionHand.MAIN_HAND);
            wasBlocking = true;
            blockTicks = 20;
        }
    }

    private static void stopBlocking(Player player) {
        if (wasBlocking) {
            player.stopUsingItem();
            wasBlocking = false;
        }
    }

    private static boolean hasShieldInOffhand(Player player) {
        return player.getItemBySlot(EquipmentSlot.OFFHAND).is(Items.SHIELD);
    }

    public static boolean isBlocking() {
        return wasBlocking;
    }
}
