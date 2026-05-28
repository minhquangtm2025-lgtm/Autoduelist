package com.autoduelist.client;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ComboAttack {
    private static int comboCounter = 0;
    private static int wTapCooldown = 0;
    private static boolean isInCombo = false;
    private static int critChainCount = 0;

    public static void update(Player player, LivingEntity target) {
        if (wTapCooldown > 0) {
            wTapCooldown--;
        }

        if (target == null || !target.isAlive()) {
            reset();
            return;
        }
    }

    public static boolean shouldWTap(Player player, LivingEntity target) {
        if (wTapCooldown > 0) return false;
        
        double distance = player.distanceToSqr(target);
        
        // W-Tap khi ở khoảng cách gần để reset knockback
        if (distance < 12.0 && player.isSprinting()) {
            return true;
        }
        
        return false;
    }

    public static void performWTap(Player player) {
        // Nhả W (stop forward movement)
        player.zza = 0.0f;
        player.setSprinting(false);
        wTapCooldown = 3; // 3 ticks delay
    }

    public static void resumeMovement(Player player) {
        if (wTapCooldown == 0) {
            player.zza = 1.0f;
            player.setSprinting(true);
        }
    }

    public static boolean shouldCritChain(Player player) {
        // Crit chain: nhảy liên tục để tạo chuỗi critical hits
        if (player.onGround() && critChainCount < 3) {
            return true;
        }
        
        if (critChainCount >= 3) {
            critChainCount = 0;
            return false;
        }
        
        return false;
    }

    public static void performCritJump(Player player) {
        if (player.onGround()) {
            player.setDeltaMovement(
                player.getDeltaMovement().x, 
                0.42f, 
                player.getDeltaMovement().z
            );
            critChainCount++;
        }
    }

    public static void startCombo() {
        isInCombo = true;
        comboCounter = 0;
    }

    public static void incrementCombo() {
        if (isInCombo) {
            comboCounter++;
        }
    }

    public static int getComboCount() {
        return comboCounter;
    }

    public static boolean isInCombo() {
        return isInCombo;
    }

    public static void reset() {
        isInCombo = false;
        comboCounter = 0;
        critChainCount = 0;
        wTapCooldown = 0;
    }
}
