package com.autoduelist.client;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;

public class CombatAnalyzer {
    
    public enum CombatStyle {
        MELEE_SWORD,      // Kiếm nhanh, nhỏ
        MELEE_AXE,        // Rìu chậm, mạnh
        RANGED_BOW,       // Cung từ xa
        TANK_SHIELD,      // Khiên phòng thủ
        BALANCED,         // Cân bằng
        UNKNOWN
    }

    public static class EnemyAnalysis {
        public CombatStyle style;
        public float attackDamage;
        public float armor;
        public ItemStack mainHand;
        public ItemStack offHand;
        public boolean hasShield;
        public boolean hasBoots;
        public boolean hasChestplate;
        public boolean hasHelmet;
        public boolean hasLeggings;
        public float estimatedHealth;

        public EnemyAnalysis() {
            this.style = CombatStyle.UNKNOWN;
            this.hasShield = false;
            this.hasBoots = false;
            this.hasChestplate = false;
            this.hasHelmet = false;
            this.hasLeggings = false;
            this.attackDamage = 0.5f;
            this.armor = 0.0f;
            this.estimatedHealth = 20.0f;
        }
    }

    public static EnemyAnalysis analyzeEnemy(LivingEntity enemy) {
        EnemyAnalysis analysis = new EnemyAnalysis();
        
        if (!(enemy instanceof Player)) {
            // Phân tích mob thường
            analysis.style = CombatStyle.MELEE_SWORD;
            analysis.estimatedHealth = enemy.getHealth();
            return analysis;
        }

        Player targetPlayer = (Player) enemy;
        
        // Phân tích equipment
        analysis.mainHand = targetPlayer.getMainHandItem();
        ItemStack offHandItem = targetPlayer.getItemBySlot(EquipmentSlot.OFFHAND);
        analysis.offHand = offHandItem != null ? offHandItem : ItemStack.EMPTY;
        
        // Kiểm tra khiên
        analysis.hasShield = checkHasShield(analysis.offHand);
        
        // Kiểm tra armor
        ItemStack helmet = targetPlayer.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = targetPlayer.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = targetPlayer.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = targetPlayer.getItemBySlot(EquipmentSlot.FEET);
        
        analysis.hasHelmet = !helmet.isEmpty();
        analysis.hasChestplate = !chest.isEmpty();
        analysis.hasLeggings = !legs.isEmpty();
        analysis.hasBoots = !boots.isEmpty();
        
        // Tính toán armor
        analysis.armor = (float) targetPlayer.getArmorValue();
        
        // Phân tích chiến đấu style dựa vào weapon
        analysis.style = determineAttackStyle(analysis.mainHand);
        
        // Ước lượng health
        analysis.estimatedHealth = targetPlayer.getHealth();
        
        return analysis;
    }

    private static boolean checkHasShield(ItemStack offHand) {
        // Kiểm tra xem có phải shield không bằng cách xem tên
        return offHand != null && offHand.getItem().toString().contains("shield");
    }

    private static CombatStyle determineAttackStyle(ItemStack weapon) {
        if (weapon.isEmpty()) {
            return CombatStyle.BALANCED;
        }

        String itemName = weapon.getItem().toString().toLowerCase();
        
        if (itemName.contains("sword")) {
            return CombatStyle.MELEE_SWORD;
        } else if (itemName.contains("axe")) {
            return CombatStyle.MELEE_AXE;
        } else if (itemName.contains("bow")) {
            return CombatStyle.RANGED_BOW;
        }

        return CombatStyle.BALANCED;
    }

    public static boolean isHighThreat(EnemyAnalysis analysis) {
        // Phân tích mức độ nguy hiểm
        int armorCount = 0;
        if (analysis.hasHelmet) armorCount++;
        if (analysis.hasChestplate) armorCount++;
        if (analysis.hasLeggings) armorCount++;
        if (analysis.hasBoots) armorCount++;

        // Nếu có khiên + armor nhiều = high threat
        return analysis.hasShield && armorCount >= 3;
    }

    public static float getOptimalAttackDistance(EnemyAnalysis analysis) {
        switch (analysis.style) {
            case RANGED_BOW:
                return 20.0f; // Giữ khoảng cách xa
            case MELEE_AXE:
                return 5.0f;  // Axe có range dài
            case MELEE_SWORD:
                return 3.5f;  // Kiếm need gần hơn
            case TANK_SHIELD:
                return 4.0f;  // Khiên need khoảng trung bình
            default:
                return 4.0f;
        }
    }

    public static boolean shouldSprintAttack(EnemyAnalysis analysis, float currentHealth) {
        // Sprint attack khi safe hoặc enemy yếu
        return currentHealth > 12.0f && !analysis.hasShield;
    }

    public static boolean shouldRun(EnemyAnalysis analysis, float currentHealth) {
        // Chạy khi máu thấp + enemy mạnh
        return currentHealth < 5.0f && isHighThreat(analysis);
    }

    public static boolean shouldDodge(EnemyAnalysis analysis, float currentHealth, double distance) {
        // Né tránh khi enemy ở gần + máu thấp + không có khiên
        return currentHealth < 10.0f && distance < 3.5f && !analysis.hasShield;
    }
}
