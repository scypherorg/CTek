package com.scypher.ctek;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CTekClient implements ClientModInitializer {
	public static final String MOD_ID = "ctek";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Override
	public void onInitializeClient() {
		LOGGER.info("Initializing CTek Client...");
	}
}