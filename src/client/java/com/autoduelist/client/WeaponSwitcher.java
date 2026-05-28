package com.autoduelist.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MaceItem;
import net.minecraft.client.Minecraft;

public class WeaponSwitcher {
    private static int switchCooldown = 0;
    private static int originalSlot = -1;

    public static void update(Player player, CombatAnalyzer.EnemyAnalysis analysis) {
        if (switchCooldown > 0) {
            switchCooldown--;
            return;
        }

        if (analysis == null) return;

        ItemStack currentWeapon = player.getMainHandItem();
        
        if (analysis.hasShield && !(currentWeapon.getItem() instanceof AxeItem)) {
            int axeSlot = findWeaponInHotbar(player, AxeItem.class);
            if (axeSlot != -1) {
                switchToWeapon(player, axeSlot);
                return;
            }
        }
        
        if (currentWeapon.getItem() instanceof MaceItem) {
            return;
        }
        
        if (!analysis.hasShield && !isSword(currentWeapon)) {
            int swordSlot = findSwordInHotbar(player);
            if (swordSlot != -1) {
                switchToWeapon(player, swordSlot);
                return;
            }
        }
    }

    private static boolean isSword(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String itemName = stack.getItem().toString().toLowerCase();
        return itemName.contains("sword");
    }

    private static int findSwordInHotbar(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isSword(stack)) {
                return i;
            }
        }
        return -1;
    }

    private static <T> int findWeaponInHotbar(Player player, Class<T> weaponClass) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && weaponClass.isInstance(stack.getItem())) {
                return i;
            }
        }
        return -1;
    }

    private static void switchToWeapon(Player player, int slot) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode != null) {
            mc.gameMode.handleInventoryButtonClick(player.containerMenu.containerId, slot);
        }
        switchCooldown = 20;
    }

    public static void reset() {
        originalSlot = -1;
        switchCooldown = 0;
    }
}
