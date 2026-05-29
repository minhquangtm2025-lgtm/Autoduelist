package com.autoduelist.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class HealingManager {
    private static int healCooldown = 0;
    private static final int HEAL_COOLDOWN_TICKS = 40;
    private static int totemCheckCooldown = 0;

    public static void update(Player player) {
        if (healCooldown > 0) {
            healCooldown--;
        }
        
        if (totemCheckCooldown > 0) {
            totemCheckCooldown--;
        }
        
        equipTotemIfNeeded(player);
        
        if (healCooldown > 0) {
            return;
        }

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float healthPercent = health / maxHealth;

        if (healthPercent < 0.3f) {
            if (tryEatGoldenApple(player)) return;
            if (tryEatFood(player, 8)) return;
        }
        else if (healthPercent < 0.5f) {
            if (tryEatFood(player, 6)) return;
        }
        else if (healthPercent < 0.7f) {
            if (tryEatFood(player, 4)) return;
        }
    }
    
    private static void equipTotemIfNeeded(Player player) {
        if (totemCheckCooldown > 0) return;
        
        ItemStack offhand = player.getItemBySlot(EquipmentSlot.OFFHAND);
        
        if (offhand.is(Items.TOTEM_OF_UNDYING)) {
            totemCheckCooldown = 100;
            return;
        }
        
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float healthPercent = health / maxHealth;
        
        if (healthPercent < 0.3f || !offhand.is(Items.SHIELD)) {
            int totemSlot = findTotemInInventory(player);
            if (totemSlot != -1) {
                equipTotemToOffhand(player, totemSlot);
                totemCheckCooldown = 100;
            }
        }
    }
    
    private static int findTotemInInventory(Player player) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.TOTEM_OF_UNDYING)) {
                return i;
            }
        }
        return -1;
    }
    
    private static void equipTotemToOffhand(Player player, int inventorySlot) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode != null) {
            ItemStack totem = player.getInventory().getItem(inventorySlot);
            ItemStack oldOffhand = player.getItemBySlot(EquipmentSlot.OFFHAND);
            
            player.setItemSlot(EquipmentSlot.OFFHAND, totem);
            player.getInventory().setItem(inventorySlot, oldOffhand);
        }
    }

    private static boolean tryEatGoldenApple(Player player) {
        int slot = findItemInHotbar(player, Items.GOLDEN_APPLE);
        if (slot == -1) {
            slot = findItemInHotbar(player, Items.ENCHANTED_GOLDEN_APPLE);
        }
        
        if (slot != -1) {
            switchToSlotAndEat(player, slot);
            healCooldown = HEAL_COOLDOWN_TICKS;
            return true;
        }
        return false;
    }

    private static boolean tryEatFood(Player player, int minNutrition) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                FoodProperties food = item.components().get(net.minecraft.core.component.DataComponents.FOOD);
                if (food != null && food.nutrition() >= minNutrition) {
                    switchToSlotAndEat(player, i);
                    healCooldown = HEAL_COOLDOWN_TICKS;
                    return true;
                }
            }
        }
        return false;
    }

    private static int findItemInHotbar(Player player, Item item) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(item)) {
                return i;
            }
        }
        return -1;
    }

    private static void switchToSlotAndEat(Player player, int slot) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode != null) {
            mc.gameMode.handleInventoryButtonClick(player.containerMenu.containerId, slot);
        }
        player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);
    }

    public static boolean isHealing() {
        return healCooldown > 0;
    }
}
