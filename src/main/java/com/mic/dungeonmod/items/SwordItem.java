package com.mic.dungeonmod.items;

import com.mic.dungeonmod.DungeonMod;
import com.mic.dungeonmod.init.ModItems;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemSword;
import net.minecraftforge.client.model.ModelLoader;

public class SwordItem extends ItemSword {

	public SwordItem(String name, ToolMaterial material) {
		super(material);
		
		setUnlocalizedName(name);
		setRegistryName(name);
		
		setCreativeTab(CreativeTabs.COMBAT);

		ModItems.ITEMS.add(this);
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(DungeonMod.MODID + ":" + name, "inventory"));
		// TODO Auto-generated constructor stub
	}

}
