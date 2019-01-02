package com.mic.dungeonmod.command;

import java.util.List;

import com.google.common.collect.Lists;

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

public class LoadCommand extends CommandBase {

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
		aliases.add("loadstructure <structure name>");
		aliases.add("loadstructure <structure name> <x y z>");
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		BlockPos pos = sender.getPosition();

		WorldServer world = server.getWorld(0);
		Template template = world.getStructureTemplateManager().getTemplate(server,
				new ResourceLocation("dungeonmod", "dungeon/" + args[0]));

		PlacementSettings settings = new PlacementSettings();

		if (1 < args.length) {
			BlockPos newPos = new BlockPos(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]));
			template.addBlocksToWorld(world, newPos, settings); // centers placement
		} else {
			template.addBlocksToWorld(world, pos.add(1, 0, 1), settings); // centers placement
		}

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
