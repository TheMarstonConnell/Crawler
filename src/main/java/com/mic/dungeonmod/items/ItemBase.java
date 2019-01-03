package com.mic.dungeonmod.items;

import com.mic.dungeonmod.DungeonMod;
import com.mic.dungeonmod.init.ModItems;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ItemBase extends Item{
	
	public ItemBase(String name) {
		setUnlocalizedName(name);
		setRegistryName(name);
		
//		setCreativeTab(DungeonMod.dungeonmodTab);

		ModItems.ITEMS.add(this);
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(DungeonMod.MODID + ":" + name, "inventory"));

	}
}
