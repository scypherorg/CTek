package com.scypher.ctek;

import com.google.gson.*;
import com.scypher.ctek.PS.Energy.EnergyNetwork;
import com.scypher.ctek.PS.base.PSManager;
import com.scypher.ctek.blocks.CTBlocks;
import com.scypher.ctek.items.CTItems;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CTek implements ModInitializer {
	public static final String MOD_ID = "ctek";
	static String _DATAPATH;
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing CTek...");
		CTItems.Initialize();
		LOGGER.info("Items Initialized...");
		CTBlocks.Initialize();
		LOGGER.info("Blocks Initialized...");
		ServerWorldEvents.LOAD.register((server, world) -> {
			if(world != server.getWorld(World.OVERWORLD))
				return;
			_DATAPATH = server.getSavePath(WorldSavePath.ROOT).toString();
			if(_DATAPATH.endsWith("."))
				_DATAPATH = _DATAPATH.substring(0, _DATAPATH.length() - 1);
			_DATAPATH += "ctek.json";
			try {
				FileReader reader = new FileReader(_DATAPATH);
				LoadWorld(new Gson().fromJson(reader, JsonObject.class));
				reader.close();
			}/* catch (Exception e) {
				throw e;
				//CTek.LOGGER.warn("Unable to load ctek file at {} - {} :: {}", _DATAPATH, e.getStackTrace()[0], e.getMessage());
            }*/ catch (IOException e) {
				CTek.LOGGER.warn("Unable to load ctek file at {} - {} :: {}", _DATAPATH, e.getStackTrace()[0], e.getMessage());
            }
        });
		ServerTickEvents.END_SERVER_TICK.register(PSManager::OnTick);
		LOGGER.info("Done initializing CTek.");
	}
	void LoadWorld(JsonObject data)
	{
		PSManager.LoadData(data.get("PSManager").getAsJsonObject());
		EnergyNetwork.LoadData(data.get("EnergyNetwork").getAsJsonObject());
	}
	public static void SaveData()
	{
		com.google.gson.JsonObject data = new JsonObject();
		data.add("PSManager", PSManager.SaveData());
		data.add("EnergyNetwork", EnergyNetwork.SaveData());
		try {
			FileWriter writer = new FileWriter(_DATAPATH);
			//Write to File
			new GsonBuilder()/*.setPrettyPrinting()*/.create().toJson(data, writer);
			writer.close();
		} catch (Exception e) {
			CTek.LOGGER.warn("Unable to save ctek file at {} - {} :: {}", _DATAPATH, e.getStackTrace()[0], e.getMessage());
			return;
		}
		CTek.LOGGER.info("Saved CTek data!");
	}
}