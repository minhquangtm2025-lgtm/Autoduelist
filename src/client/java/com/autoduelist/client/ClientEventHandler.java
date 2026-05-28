package com.autoduelist.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ClientEventHandler {
    private static boolean shiftPressed = false;

    public static void registerEvents() {
        // Register client tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> onClientTick(client));
    }

    private static void onClientTick(Minecraft client) {
        if (client.player == null || client.getWindow() == null) return;

        // Check if right shift key is pressed
        @SuppressWarnings("null")
        long window = client.getWindow().handle();
        boolean rightShiftPressed = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

        // Toggle AI on key press
        if (rightShiftPressed && !shiftPressed) {
            AutoDuelistAI.toggleAI();
            String status = AutoDuelistAI.isEnabled() ? "§a[AutoDuelist] Enabled" : "§c[AutoDuelist] Disabled";
            if (client.player != null) {
                client.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(status),
                    true
                );
            }
        }

        shiftPressed = rightShiftPressed;

        // Update AI every tick
        AutoDuelistAI.update();
    }
}
