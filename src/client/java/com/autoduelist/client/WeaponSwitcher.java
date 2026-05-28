package com.autoduelist.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MaceItem;
import net.minecraft.client.Minecraft;
import java.util.List;

public class WeaponSwitcher {
    private static int switchCooldown = 0;
    private static int originalSlot = -1;
    private static boolean autoOrganized = false;

    public static void update(Player player, CombatAnalyzer.EnemyAnalysis analysis) {
        if (!autoOrganized) {
            organizeWeaponsInHotbar(player);
            autoOrganized = true;
        }

        if (switchCooldown > 0) {
            switchCooldown--;
            return;
        }

        if (analysis == null) return;

        ItemStack currentWeapon = player.getMainHandItem();
        
        if (analysis.hasShield && !(currentWeapon.getItem() instanceof AxeItem)) {
            int axeSlot = findBestWeaponInHotbar(player, InventoryAnalyzer.WeaponType.AXE);
            if (axeSlot != -1) {
                switchToWeapon(player, axeSlot);
                return;
            }
        }
        
        if (currentWeapon.getItem() instanceof MaceItem) {
            return;
        }
        
        if (!analysis.hasShield && !isSword(currentWeapon)) {
            int swordSlot = findBestWeaponInHotbar(player, InventoryAnalyzer.WeaponType.SWORD);
            if (swordSlot != -1) {
                switchToWeapon(player, swordSlot);
                return;
            }
        }
    }

    private static void organizeWeaponsInHotbar(Player player) {
        List<InventoryAnalyzer.WeaponInfo> weapons = InventoryAnalyzer.analyzeInventory(player);
        
        int hotbarSlot = 0;
        for (InventoryAnalyzer.WeaponInfo weapon : weapons) {
            if (hotbarSlot >= 9) break;
            
            if (weapon.slot >= 9) {
                moveItemToHotbar(player, weapon.slot, hotbarSlot);
                hotbarSlot++;
            } else if (weapon.slot < 9) {
                hotbarSlot++;
            }
        }
    }

    private static void moveItemToHotbar(Player player, int fromSlot, int toSlot) {
        if (fromSlot == toSlot) return;
        
        ItemStack fromStack = player.getInventory().getItem(fromSlot);
        ItemStack toStack = player.getInventory().getItem(toSlot);
        
        if (toStack.isEmpty()) {
            player.getInventory().setItem(toSlot, fromStack);
            player.getInventory().setItem(fromSlot, ItemStack.EMPTY);
        } else {
            player.getInventory().setItem(toSlot, fromStack);
            player.getInventory().setItem(fromSlot, toStack);
        }
    }

    private static int findBestWeaponInHotbar(Player player, InventoryAnalyzer.WeaponType type) {
        int bestSlot = -1;
        int bestPriority = -1;
        
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            
            InventoryAnalyzer.WeaponType weaponType = identifyWeaponType(stack);
            if (weaponType == type) {
                int priority = calculateWeaponPriority(stack);
                if (priority > bestPriority) {
                    bestPriority = priority;
                    bestSlot = i;
                }
            }
        }
        
        return bestSlot;
    }

    private static InventoryAnalyzer.WeaponType identifyWeaponType(ItemStack stack) {
        String itemName = stack.getItem().toString().toLowerCase();
        
        if (itemName.contains("sword")) return InventoryAnalyzer.WeaponType.SWORD;
        if (stack.getItem() instanceof AxeItem) return InventoryAnalyzer.WeaponType.AXE;
        if (stack.getItem() instanceof MaceItem) return InventoryAnalyzer.WeaponType.MACE;
        
        return null;
    }

    private static int calculateWeaponPriority(ItemStack stack) {
        int priority = 0;
        
        String itemName = stack.getItem().toString().toLowerCase();
        if (itemName.contains("netherite")) priority += 50;
        else if (itemName.contains("diamond")) priority += 40;
        else if (itemName.contains("iron")) priority += 30;
        else if (itemName.contains("stone")) priority += 20;
        else if (itemName.contains("wood") || itemName.contains("golden")) priority += 10;
        
        if (stack.isEnchanted()) priority += 100;
        
        return priority;
    }

    private static boolean isSword(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String itemName = stack.getItem().toString().toLowerCase();
        return itemName.contains("sword");
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
        autoOrganized = false;
    }
}
