package com.mic.dungeonmod;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;

import org.apache.logging.log4j.Logger;
import com.mic.dungeonmod.proxy.CommonProxy;
import com.mic.dungeonmod.util.ModTab;
import com.mic.dungeonmod.util.handlers.RegistryHandler;
import com.mic.dungeonmod.util.world.DungeonGenerator;
import com.mic.dungeonmod.util.world.TieredDungeonGenerator;

@Mod(modid = DungeonMod.MODID, name = DungeonMod.NAME, version = DungeonMod.VERSION)
public class DungeonMod
{
    public static final String MODID = "dungeonmod";
    public static final String NAME = "Dungeon Mod";
    public static final String VERSION = "1.0";
    public static final String CLIENT_PROXY_CLASS = "com.mic.dungeonmod.proxy.ClientProxy";
	public static final String COMMON_PROXY_CLASS = "com.mic.dungeonmod.proxy.CommonProxy";

    private static Logger logger;
    
    public static File config;
    
    static RegistryHandler eventHandler;
    
    @Mod.Instance(DungeonMod.MODID)
	public static DungeonMod instance;
    
    @SidedProxy(clientSide = DungeonMod.CLIENT_PROXY_CLASS, serverSide = DungeonMod.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;
    
//	public static final ModTab dungeonmodTab = new ModTab();    
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {

    	eventHandler = new RegistryHandler();
		MinecraftForge.EVENT_BUS.register(eventHandler);
		eventHandler.preInitRegistries(event);
		proxy.preInit(event);
		LootTableList.register(new ResourceLocation("dungeonmod", "chests/dungeon_chest"));
		LootTableList.register(new ResourceLocation("dungeonmod", "chests/treasure_chest"));
		GameRegistry.registerWorldGenerator(new DungeonGenerator(), 0);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	eventHandler.initRegistries();
		proxy.init(event);
    
    }

    
    @EventHandler
	public static void PostInit(FMLPostInitializationEvent event) {

	}
    
    @EventHandler
	public static void serverInit(FMLServerStartingEvent event)
	{
		RegistryHandler.serverRegistries(event);
	}
    
}
