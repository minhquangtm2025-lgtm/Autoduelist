package com.autoduelist.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.MaceItem;
import java.util.ArrayList;
import java.util.List;

public class InventoryAnalyzer {
    
    public static class WeaponInfo {
        public ItemStack stack;
        public int slot;
        public WeaponType type;
        public int priority;
        
        public WeaponInfo(ItemStack stack, int slot, WeaponType type, int priority) {
            this.stack = stack;
            this.slot = slot;
            this.type = type;
            this.priority = priority;
        }
    }
    
    public enum WeaponType {
        SWORD(5),
        AXE(4),
        MACE(6),
        BOW(3),
        CROSSBOW(3),
        TRIDENT(4),
        WIND_CHARGE(2),
        POTION(1),
        SHIELD(7),
        FOOD(8);
        
        public final int basePriority;
        
        WeaponType(int priority) {
            this.basePriority = priority;
        }
    }
    
    public static List<WeaponInfo> analyzeInventory(Player player) {
        List<WeaponInfo> weapons = new ArrayList<>();
        
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) continue;
            
            WeaponType type = identifyWeaponType(stack);
            if (type != null) {
                int priority = calculatePriority(stack, type);
                weapons.add(new WeaponInfo(stack, i, type, priority));
            }
        }
        
        weapons.sort((a, b) -> Integer.compare(b.priority, a.priority));
        return weapons;
    }
    
    private static WeaponType identifyWeaponType(ItemStack stack) {
        String itemName = stack.getItem().toString().toLowerCase();
        
        if (itemName.contains("sword")) return WeaponType.SWORD;
        if (stack.getItem() instanceof AxeItem) return WeaponType.AXE;
        if (stack.getItem() instanceof MaceItem) return WeaponType.MACE;
        if (stack.getItem() instanceof BowItem) return WeaponType.BOW;
        if (stack.getItem() instanceof CrossbowItem) return WeaponType.CROSSBOW;
        if (stack.getItem() instanceof TridentItem) return WeaponType.TRIDENT;
        if (stack.is(Items.WIND_CHARGE)) return WeaponType.WIND_CHARGE;
        if (itemName.contains("splash") || itemName.contains("lingering")) return WeaponType.POTION;
        if (itemName.contains("shield")) return WeaponType.SHIELD;
        if (stack.getItem().components().get(net.minecraft.core.component.DataComponents.FOOD) != null) {
            return WeaponType.FOOD;
        }
        
        return null;
    }
    
    private static int calculatePriority(ItemStack stack, WeaponType type) {
        int priority = type.basePriority * 100;
        
        String itemName = stack.getItem().toString().toLowerCase();
        if (itemName.contains("netherite")) priority += 50;
        else if (itemName.contains("diamond")) priority += 40;
        else if (itemName.contains("iron")) priority += 30;
        else if (itemName.contains("stone")) priority += 20;
        else if (itemName.contains("wood") || itemName.contains("golden")) priority += 10;
        
        int durability = stack.getMaxDamage() - stack.getDamageValue();
        priority += (durability / 10);
        
        if (stack.isEnchanted()) {
            priority += 100;
        }
        
        return priority;
    }
    
    public static void organizeHotbar(Player player) {
        List<WeaponInfo> weapons = analyzeInventory(player);
        
        int[] targetSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        int hotbarIndex = 0;
        
        for (WeaponInfo weapon : weapons) {
            if (hotbarIndex >= 9) break;
            
            if (weapon.slot >= 9) {
                moveItemToHotbar(player, weapon.slot, targetSlots[hotbarIndex]);
                hotbarIndex++;
            }
        }
    }
    
    private static void moveItemToHotbar(Player player, int fromSlot, int toSlot) {
        ItemStack fromStack = player.getInventory().getItem(fromSlot);
        ItemStack toStack = player.getInventory().getItem(toSlot);
        
        player.getInventory().setItem(toSlot, fromStack);
        player.getInventory().setItem(fromSlot, toStack);
    }
    
    public static String getInventoryReport(Player player) {
        List<WeaponInfo> weapons = analyzeInventory(player);
        StringBuilder report = new StringBuilder();
        
        report.append("=== INVENTORY ANALYSIS ===\n");
        report.append("Total weapons: ").append(weapons.size()).append("\n\n");
        
        for (WeaponInfo weapon : weapons) {
            report.append(weapon.type.name())
                  .append(" [Slot ").append(weapon.slot).append("]")
                  .append(" Priority: ").append(weapon.priority)
                  .append("\n");
        }
        
        return report.toString();
    }
    
    public static boolean hasArrows(Player player) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.ARROW) || stack.is(Items.SPECTRAL_ARROW) || stack.is(Items.TIPPED_ARROW)) {
                return true;
            }
        }
        return false;
    }
    
    public static int countWeaponType(Player player, WeaponType type) {
        int count = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && identifyWeaponType(stack) == type) {
                count++;
            }
        }
        return count;
    }
}
