package com.mic.dungeonmod.util.handlers;

import com.mic.dungeonmod.DungeonMod;
import com.mic.dungeonmod.command.GenCommand;
import com.mic.dungeonmod.command.LoadCommand;
import com.mic.dungeonmod.init.ModItems;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RegistryHandler {

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onModelRegister(ModelRegistryEvent event) {
		for (Item item : ModItems.ITEMS) {
			DungeonMod.proxy.registerItemRenderer(item, 0, "inventory");
		}
		//for blocks
//		for (Block block : ModBlocks.BLOCKS) {
//			if (block instanceof IHasModel) {
//				((IHasModel) block).registerModels();
//			}
//		}
	}
	
	public static void initRegistries() {

	}

	public static void preInitRegistries(FMLPreInitializationEvent event) {
		ConfigHandler.registerConfig(event);

	}

	public static void serverRegistries(FMLServerStartingEvent event) {
		event.registerServerCommand(new LoadCommand());
//		event.registerServerCommand(new GenCommand());

	}

	
}
