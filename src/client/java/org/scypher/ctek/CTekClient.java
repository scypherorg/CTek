package org.scypher.ctek;

import net.fabricmc.api.ClientModInitializer;
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