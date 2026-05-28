package com.autoduelist.client;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;

public class MaceCombat {
    private static int jumpCooldown = 0;
    private static boolean isJumping = false;

    public static void update(Player player, LivingEntity target) {
        if (jumpCooldown > 0) {
            jumpCooldown--;
        }

        if (target == null || !target.isAlive()) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof MaceItem)) {
            return;
        }

        double distance = player.distanceToSqr(target);

        if (shouldJumpAttack(player, target, distance)) {
            performJumpAttack(player);
        }
    }

    private static boolean shouldJumpAttack(Player player, LivingEntity target, double distance) {
        return distance < 16.0 && 
               player.onGround() && 
               jumpCooldown == 0 &&
               !isJumping;
    }

    private static void performJumpAttack(Player player) {
        player.setDeltaMovement(
            player.getDeltaMovement().x, 
            0.6f, 
            player.getDeltaMovement().z
        );
        isJumping = true;
        jumpCooldown = 40;
    }

    public static void onLanding(Player player) {
        if (isJumping && player.onGround()) {
            isJumping = false;
        }
    }

    public static boolean isMaceEquipped(Player player) {
        return player.getMainHandItem().getItem() instanceof MaceItem;
    }

    public static void reset() {
        jumpCooldown = 0;
        isJumping = false;
    }
}
