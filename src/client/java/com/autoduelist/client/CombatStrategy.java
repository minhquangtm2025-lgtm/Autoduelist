package com.autoduelist.client;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class CombatStrategy {
    
    private final Player player;
    private final LivingEntity target;
    private final CombatAnalyzer.EnemyAnalysis analysis;
    private int attackCooldown = 0;
    private int sprintTicks = 0;
    private int wTapPhase = 0;

    public CombatStrategy(Player player, LivingEntity target, CombatAnalyzer.EnemyAnalysis analysis) {
        this.player = player;
        this.target = target;
        this.analysis = analysis;
    }

    public void executeCombatTick() {
        if (target == null || !target.isAlive()) return;

        float playerHealth = player.getHealth();
        @SuppressWarnings("null")
        double distance = player.distanceToSqr(target);
        double optimalDistance = CombatAnalyzer.getOptimalAttackDistance(analysis);

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        HealingManager.update(player);
        ShieldManager.update(player, target);
        ProjectileDodger.update(player);
        WeaponSwitcher.update(player, analysis);
        ComboAttack.update(player, target);
        
        RangedCombat.update(player, target);
        MaceCombat.update(player, target);
        WindChargeCombat.update(player, target);
        PotionManager.update(player, target);

        if (RangedCombat.isUsingRangedWeapon(player)) {
            return;
        }

        if (CombatAnalyzer.shouldRun(analysis, playerHealth)) {
            moveAwayFromTarget();
            return;
        }

        if (ProjectileDodger.isDodging()) {
            return;
        }

        if (distance < optimalDistance * optimalDistance) {
            moveAwayFromTarget();
        } else if (distance > optimalDistance * optimalDistance * 1.5) {
            moveTowardsTarget();
        } else {
            executePrimaryAttack();
        }

        if (CombatAnalyzer.shouldDodge(analysis, playerHealth, Math.sqrt(distance))) {
            dodgeEnemy();
        }

        if (CombatAnalyzer.shouldSprintAttack(analysis, playerHealth) && sprintTicks == 0) {
            performSprintAttack();
        }
    }

    private void executePrimaryAttack() {
        if (ShieldManager.isBlocking()) {
            return;
        }

        boolean isCritical = calculateCriticalHit();
        
        if (attackCooldown <= 0) {
            lookAtTarget(target);
            
            if (ComboAttack.shouldWTap(player, target)) {
                if (wTapPhase == 0) {
                    ComboAttack.performWTap(player);
                    wTapPhase = 1;
                    return;
                } else if (wTapPhase == 1) {
                    ComboAttack.resumeMovement(player);
                    wTapPhase = 0;
                }
            }

            if (ComboAttack.shouldCritChain(player)) {
                ComboAttack.performCritJump(player);
            }
            
            if (isCritical) {
                player.setSprinting(true);
                sprintTicks = 5;
            }
            
            AutoDuelistAI.performAttack(target);
            ComboAttack.incrementCombo();
            MaceCombat.onLanding(player);
            attackCooldown = 8;
        }
    }

    private boolean calculateCriticalHit() {
        // Critical hit khi nhảy hoặc đã có momentum
        return player.fallDistance > 0.0f || player.getDeltaMovement().y < -0.1;
    }

    private void performSprintAttack() {
        // Sprint + jump + attack = critical damage
        player.setSprinting(true);
        player.setDeltaMovement(player.getDeltaMovement().x, 0.42f, player.getDeltaMovement().z);
        sprintTicks = 10;
    }

    private void moveTowardsTarget() {
        // Tiến lại target
        double dx = target.getX() - player.getX();
        double dz = target.getZ() - player.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance > 0.1) {
            player.zza = 1.0f; // Di chuyển forward
            
            // Strafe khi theo đuổi
            if (Math.random() > 0.7) {
                player.xxa = (Math.random() > 0.5) ? 1.0f : -1.0f;
            }
        }
    }

    private void moveAwayFromTarget() {
        // Chạy lùi
        player.zza = -1.0f;
        player.setSprinting(true);
        
        // Strafe để tránh
        player.xxa = (Math.random() > 0.5) ? 0.5f : -0.5f;
    }

    private void dodgeEnemy() {
        // Nhảy + lùi để né tránh attack
        if (player.fallDistance == 0.0f) {
            player.setDeltaMovement(player.getDeltaMovement().x, 0.42f, player.getDeltaMovement().z);
        }
        
        // Di chuyển sang cạnh
        player.xxa = (Math.random() > 0.5) ? 1.0f : -1.0f;
        player.zza = -0.5f;
    }

    private void lookAtTarget(LivingEntity target) {
        double dx = target.getX() - player.getX();
        double dy = target.getY() - player.getY() + target.getEyeHeight();
        double dz = target.getZ() - player.getZ();

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance > 0.1) {
            double yaw = Math.atan2(dz, dx) * 180.0 / Math.PI - 90.0;
            double pitch = -Math.asin(dy / distance) * 180.0 / Math.PI;

            player.setXRot((float) pitch);
            player.setYRot((float) yaw);
        }
    }

    public CombatAnalyzer.CombatStyle getTargetStyle() {
        return analysis.style;
    }

    public boolean isTargetHighThreat() {
        return CombatAnalyzer.isHighThreat(analysis);
    }
}
