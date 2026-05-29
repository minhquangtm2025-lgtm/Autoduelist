package com.autoduelist.client;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;

public class WindChargeCombat {
    private static int throwCooldown = 0;
    private static final int THROW_COOLDOWN_TICKS = 20;
    private static boolean isLaunchingCombo = false;
    private static int comboPhase = 0;
    private static LivingEntity comboTarget = null;

    public static void update(Player player, LivingEntity target) {
        if (throwCooldown > 0) {
            throwCooldown--;
        }

        if (target == null || !target.isAlive()) {
            resetCombo();
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.is(Items.WIND_CHARGE)) {
            return;
        }

        double distance = player.distanceToSqr(target);

        if (canDoMaceCombo(player, target, distance)) {
            executeMaceCombo(player, target);
        } else if (distance > 16.0 && distance < 256.0) {
            throwWindCharge(player, target);
        }
    }

    private static boolean canDoMaceCombo(Player player, LivingEntity target, double distance) {
        if (throwCooldown > 0 || isLaunchingCombo) return false;
        if (distance < 25.0 || distance > 100.0) return false;
        
        return hasMaceInHotbar(player) && player.onGround();
    }

    private static void executeMaceCombo(Player player, LivingEntity target) {
        isLaunchingCombo = true;
        comboTarget = target;
        comboPhase = 1;
        
        lookDown(player);
        player.startUsingItem(InteractionHand.MAIN_HAND);
        player.stopUsingItem();
        
        throwCooldown = 100;
    }

    public static void updateCombo(Player player) {
        if (!isLaunchingCombo || comboTarget == null) return;

        if (comboPhase == 1 && !player.onGround() && player.getDeltaMovement().y > 0.3) {
            comboPhase = 2;
            lookAtTarget(player, comboTarget);
            moveTowardsTarget(player, comboTarget);
        }

        if (comboPhase == 2 && player.getDeltaMovement().y < -0.5) {
            comboPhase = 3;
            switchToMace(player);
        }

        if (comboPhase == 3 && player.onGround()) {
            resetCombo();
        }
    }

    private static boolean hasMaceInHotbar(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem().toString().toLowerCase().contains("mace")) {
                return true;
            }
        }
        return false;
    }

    private static void switchToMace(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.getItem().toString().toLowerCase().contains("mace")) {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.gameMode != null) {
                    mc.gameMode.handleInventoryButtonClick(player.containerMenu.containerId, i);
                }
                break;
            }
        }
    }

    private static void lookDown(Player player) {
        player.setXRot(90.0f);
    }

    private static void lookAtTarget(Player player, LivingEntity target) {
        double dx = target.getX() - player.getX();
        double dy = target.getY() - player.getY();
        double dz = target.getZ() - player.getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance > 0.1) {
            double yaw = Math.atan2(dz, dx) * 180.0 / Math.PI - 90.0;
            double pitch = -Math.asin(dy / distance) * 180.0 / Math.PI;
            player.setXRot((float) pitch);
            player.setYRot((float) yaw);
        }
    }

    private static void moveTowardsTarget(Player player, LivingEntity target) {
        double dx = target.getX() - player.getX();
        double dz = target.getZ() - player.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 0.1) {
            player.zza = 1.0f;
            player.setSprinting(true);
        }
    }

    private static void throwWindCharge(Player player, LivingEntity target) {
        if (throwCooldown > 0) return;
        
        lookAtTarget(player, target);
        player.startUsingItem(InteractionHand.MAIN_HAND);
        player.stopUsingItem();
        throwCooldown = THROW_COOLDOWN_TICKS;
    }

    public static boolean hasWindCharge(Player player) {
        return player.getMainHandItem().is(Items.WIND_CHARGE);
    }

    public static boolean isInCombo() {
        return isLaunchingCombo;
    }

    private static void resetCombo() {
        isLaunchingCombo = false;
        comboPhase = 0;
        comboTarget = null;
    }

    public static void reset() {
        throwCooldown = 0;
        resetCombo();
    }
}
