package com.autoduelist.client;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;

public class PotionManager {
    private static int throwCooldown = 0;
    private static final int THROW_COOLDOWN_TICKS = 60;

    public static void update(Player player, LivingEntity target) {
        if (throwCooldown > 0) {
            throwCooldown--;
            return;
        }

        if (target == null || !target.isAlive()) {
            return;
        }

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float healthPercent = health / maxHealth;

        if (healthPercent < 0.4f) {
            tryThrowHealingPotion(player);
        } else {
            double distance = player.distanceToSqr(target);
            if (distance < 64.0 && distance > 9.0) {
                tryThrowDamagePotion(player, target);
            }
        }
    }

    private static void tryThrowHealingPotion(Player player) {
        int slot = findPotionInHotbar(player, true);
        if (slot != -1) {
            throwPotion(player, slot);
        }
    }

    private static void tryThrowDamagePotion(Player player, LivingEntity target) {
        int slot = findPotionInHotbar(player, false);
        if (slot != -1) {
            lookAtTarget(player, target);
            throwPotion(player, slot);
        }
    }

    private static int findPotionInHotbar(Player player, boolean healing) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                String itemName = stack.getItem().toString().toLowerCase();
                if (itemName.contains("splash") || itemName.contains("lingering")) {
                    if (healing && (itemName.contains("healing") || itemName.contains("regeneration"))) {
                        return i;
                    } else if (!healing && (itemName.contains("harming") || itemName.contains("poison"))) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private static void throwPotion(Player player, int slot) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.gameMode != null) {
            mc.gameMode.handleInventoryButtonClick(player.containerMenu.containerId, slot);
        }
        player.startUsingItem(InteractionHand.MAIN_HAND);
        player.stopUsingItem();
        throwCooldown = THROW_COOLDOWN_TICKS;
    }

    private static void lookAtTarget(Player player, LivingEntity target) {
        double dx = target.getX() - player.getX();
        double dy = target.getY() - player.getY();
        double dz = target.getZ() - player.getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance > 0.1) {
            double yaw = Math.atan2(dz, dx) * 180.0 / Math.PI - 90.0;
            double pitch = -Math.asin(dy / distance) * 180.0 / Math.PI - 10.0;
            player.setXRot((float) pitch);
            player.setYRot((float) yaw);
        }
    }

    public static void reset() {
        throwCooldown = 0;
    }
}
