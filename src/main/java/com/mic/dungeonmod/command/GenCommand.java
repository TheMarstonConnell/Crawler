package com.mic.dungeonmod.command;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.mic.dungeonmod.util.world.DungeonGenerator;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;

public class GenCommand extends CommandBase {

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return "loadstructure";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "loadstructure <structure name>";
	}

	@Override
	public List<String> getAliases() {
		List<String> aliases = Lists.<String>newArrayList();
		aliases.add("generatedungeon");
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		DungeonGenerator dg = new DungeonGenerator();
		dg.generate(new Random(), sender.getPosition().getX() / 16, sender.getPosition().getZ() / 16, server.getWorld(0), server.getWorld(0).getChunkProvider().chunkGenerator, server.getWorld(0).getChunkProvider());

	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		return null;
	}

}
