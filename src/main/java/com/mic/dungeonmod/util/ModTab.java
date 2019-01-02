package com.mic.dungeonmod.util;


import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ModTab extends CreativeTabs{

	public ModTab() {
		super("dungeonmodtab");
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(Items.IRON_DOOR);
	}
	
	

}
