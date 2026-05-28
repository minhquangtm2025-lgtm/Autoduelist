package com.autoduelist.client;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;

public class WindChargeCombat {
    private static int throwCooldown = 0;
    private static final int THROW_COOLDOWN_TICKS = 20;

    public static void update(Player player, LivingEntity target) {
        if (throwCooldown > 0) {
            throwCooldown--;
            return;
        }

        if (target == null || !target.isAlive()) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        if (!mainHand.is(Items.WIND_CHARGE)) {
            return;
        }

        double distance = player.distanceToSqr(target);

        if (distance > 16.0 && distance < 256.0) {
            throwWindCharge(player, target);
        }
    }

    private static void throwWindCharge(Player player, LivingEntity target) {
        lookAtTarget(player, target);
        player.startUsingItem(InteractionHand.MAIN_HAND);
        player.stopUsingItem();
        throwCooldown = THROW_COOLDOWN_TICKS;
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

    public static boolean hasWindCharge(Player player) {
        return player.getMainHandItem().is(Items.WIND_CHARGE);
    }

    public static void reset() {
        throwCooldown = 0;
    }
}
