package com.autoduelist.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.Minecraft;

public class ArmorManager {
    private static int equipCooldown = 0;
    private static final int EQUIP_COOLDOWN_TICKS = 40;
    
    public static void update(Player player) {
        if (equipCooldown > 0) {
            equipCooldown--;
            return;
        }
        
        if (shouldEquipArmor(player)) {
            equipBestArmor(player);
        }
    }
    
    private static boolean shouldEquipArmor(Player player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        
        return helmet.isEmpty() || chest.isEmpty() || legs.isEmpty() || boots.isEmpty();
    }
    
    private static void equipBestArmor(Player player) {
        equipBestArmorPiece(player, EquipmentSlot.HEAD);
        equipBestArmorPiece(player, EquipmentSlot.CHEST);
        equipBestArmorPiece(player, EquipmentSlot.LEGS);
        equipBestArmorPiece(player, EquipmentSlot.FEET);
        
        equipCooldown = EQUIP_COOLDOWN_TICKS;
    }
    
    private static void equipBestArmorPiece(Player player, EquipmentSlot slot) {
        ItemStack currentArmor = player.getItemBySlot(slot);
        ItemStack bestArmor = findBestArmorInInventory(player, slot);
        
        if (bestArmor != null && isBetterArmor(bestArmor, currentArmor)) {
            int armorSlot = findArmorSlotInInventory(player, bestArmor);
            if (armorSlot != -1) {
                equipArmorFromInventory(player, armorSlot, slot);
            }
        }
    }
    
    private static ItemStack findBestArmorInInventory(Player player, EquipmentSlot slot) {
        ItemStack best = null;
        int bestValue = -1;
        
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty() || !isArmorForSlot(stack, slot)) continue;
            
            int value = calculateArmorValue(stack);
            if (value > bestValue) {
                bestValue = value;
                best = stack;
            }
        }
        
        return best;
    }
    
    private static boolean isArmorForSlot(ItemStack stack, EquipmentSlot slot) {
        String itemName = stack.getItem().toString().toLowerCase();
        
        switch (slot) {
            case HEAD:
                return itemName.contains("helmet");
            case CHEST:
                return itemName.contains("chestplate");
            case LEGS:
                return itemName.contains("leggings");
            case FEET:
                return itemName.contains("boots");
            default:
                return false;
        }
    }
    
    private static int calculateArmorValue(ItemStack stack) {
        int value = 0;
        
        String itemName = stack.getItem().toString().toLowerCase();
        if (itemName.contains("netherite")) value += 500;
        else if (itemName.contains("diamond")) value += 400;
        else if (itemName.contains("iron")) value += 300;
        else if (itemName.contains("chainmail")) value += 200;
        else if (itemName.contains("golden")) value += 150;
        else if (itemName.contains("leather")) value += 100;
        
        int durability = stack.getMaxDamage() - stack.getDamageValue();
        value += durability;
        
        if (stack.isEnchanted()) {
            value += 1000;
        }
        
        return value;
    }
    
    private static boolean isBetterArmor(ItemStack newArmor, ItemStack currentArmor) {
        if (currentArmor.isEmpty()) return true;
        return calculateArmorValue(newArmor) > calculateArmorValue(currentArmor);
    }
    
    private static int findArmorSlotInInventory(Player player, ItemStack armor) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameComponents(stack, armor)) {
                return i;
            }
        }
        return -1;
    }
    
    private static void equipArmorFromInventory(Player player, int inventorySlot, EquipmentSlot equipSlot) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameMode != null) {
            ItemStack armor = player.getInventory().getItem(inventorySlot);
            ItemStack oldArmor = player.getItemBySlot(equipSlot);
            
            player.setItemSlot(equipSlot, armor);
            player.getInventory().setItem(inventorySlot, oldArmor);
        }
    }
    
    public static int getArmorCount(Player player) {
        int count = 0;
        if (!player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) count++;
        if (!player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) count++;
        if (!player.getItemBySlot(EquipmentSlot.LEGS).isEmpty()) count++;
        if (!player.getItemBySlot(EquipmentSlot.FEET).isEmpty()) count++;
        return count;
    }
    
    public static float getTotalArmorValue(Player player) {
        return player.getArmorValue();
    }
    
    public static void reset() {
        equipCooldown = 0;
    }
}
