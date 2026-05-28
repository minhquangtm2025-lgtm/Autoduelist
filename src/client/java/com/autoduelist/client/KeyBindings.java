package com.autoduelist.client;

import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String CATEGORY = "category.autoduelist";
    public static final int TOGGLE_AI_KEY = GLFW.GLFW_KEY_RIGHT_SHIFT;

    public static void register() {
        // Key bindings are handled through ClientEventHandler
        // The right shift key is detected directly in ClientEventHandler
    }
}