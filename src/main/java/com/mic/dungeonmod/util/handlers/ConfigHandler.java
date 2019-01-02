package com.mic.dungeonmod.util.handlers;

import java.io.File;

import com.mic.dungeonmod.DungeonMod;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConfigHandler {
	public static Configuration config;
	public static int dungeonChance = 0;
	
public static void init(File file){
		
		config = new Configuration(file);
		String category;
		
		//Drop chances
		category = "Dungeon Rates";
		
		dungeonChance = config.getInt("Dungeon Spawn Rate", category, 5, 0, 100, "0 for never & 100 for a lot");
		
		
		config.save();
		
	}
	
	public static void registerConfig(FMLPreInitializationEvent event){
		DungeonMod.config = new File(event.getModConfigurationDirectory() + "/" + DungeonMod.MODID);
		DungeonMod.config.mkdirs();
		init(new File(DungeonMod.config.getPath(), DungeonMod.MODID + ".cfg"));
	}
	
}
