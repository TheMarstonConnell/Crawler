package com.mic.dungeonmod.init;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mic.dungeonmod.DungeonMod;
import com.mic.dungeonmod.items.ItemBase;
import com.mic.dungeonmod.items.SwordItem;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemSword;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.common.util.EnumHelper;


public class ModItems {
	//DIAMOND(3, 1561, 8.0F, 3.0F, 10)
	
	public static final List<Item> ITEMS = new ArrayList<Item>();
	
	public static final ItemSword sword = new SwordItem("skull_sword", EnumHelper.addToolMaterial("material_skull", 3, 1561, 8.0f, 4.5f, 10));
	
	ToolMaterial i = null;
	
	@Mod.EventBusSubscriber(modid = DungeonMod.MODID)
	public static class ItemRegistry
	{
		public static final Set<Item> ITEM_SET = new HashSet<Item>();
		@SubscribeEvent
		public static void newRegistry(final RegistryEvent.NewRegistry event)
		{
			
		}
		@SubscribeEvent
		public static void register(final RegistryEvent.Register<Item> event)
		{
			final IForgeRegistry<Item> registry = event.getRegistry();
			
			for (final Item item : ITEMS)
			{
				registry.register(item);
				ITEM_SET.add(item);
			}
			
		}
	}
	
}
