package com.autoduelist.client;

import net.fabricmc.api.ClientModInitializer;

public class AutoDuelistClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Register keybindings
		KeyBindings.register();
		
		// Register event handlers
		ClientEventHandler.registerEvents();
	}
}