package com.mic.dungeonmod.util.world;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.jar.Attributes.Name;

import com.mic.dungeonmod.DungeonMod;
import com.mic.dungeonmod.util.handlers.ConfigHandler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.fml.common.IWorldGenerator;

public class TieredDungeonGenerator implements IWorldGenerator {

	public static List<ResourceLocation> center;
	public static List<ResourceLocation> hallway;
	public static List<ResourceLocation> end;
	public static List<ResourceLocation> corner_left;
	public static List<ResourceLocation> corner_right;
	public static List<ResourceLocation> t_split;
	public static List<ResourceLocation> threeway;

	boolean debug = false;

	public static final ResourceLocation STAIRS = new ResourceLocation("dungeonmod", "tiereddungeon/stairs");
	public static final ResourceLocation STARTINGROOM = new ResourceLocation("dungeonmod",
			"tiereddungeon/dp_starting_room_basic");
	public static final ResourceLocation ENDBASIC = new ResourceLocation("dungeonmod", "tiereddungeon/dp_ending_basic");
	public static final ResourceLocation HALLWAY = new ResourceLocation("dungeonmod",
			"tiereddungeon/dp_corridor_basic");
	public static final ResourceLocation ENTRANCE_BASIC = new ResourceLocation("dungeonmod",
			"dungeon/entrance/dp_entrance_basic");
	public static final ResourceLocation ENTRANCE_DESERT = new ResourceLocation("dungeonmod",
			"dungeon/entrance/dp_entrance_desert");

	public static Map<String, Integer> chances = new HashMap<String, Integer>();

	public TieredDungeonGenerator() {

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {

		class Dungeon {

			List<BlockPos> rooms = new ArrayList<BlockPos>();

			/**
			 * Generates the starting room of a dungeon and creates a branch for each door.
			 * 
			 * @param world
			 * @param random
			 * @param pos
			 */
			public void generateDungeon(WorldServer world, Random random, BlockPos pos) {

				// if (debug)
				System.out.println("Generating a dungeon at " + pos + "...");
				rooms.add(pos);
				MinecraftServer server = world.getMinecraftServer();
				Template template = world.getStructureTemplateManager().getTemplate(server, STARTINGROOM);
				PlacementSettings settings = new PlacementSettings();

				// template.getDataBlocks(pos, placementIn)
				template.addBlocksToWorld(world, pos.add(-10, 0, -10), settings); // centers placement

				template = world.getStructureTemplateManager().getTemplate(server, STAIRS);

				int i = 0;
				// || pos.getY() < 62
				while ((!world.getBlockState(pos.add(-3, 9 + i, -3)).getBlock().equals(Blocks.AIR))
						&& (pos.add(-3, 9 + i, -3).getY() < 62)) {
					template.addBlocksToWorld(world, pos.add(-3, 9 + i, -3), settings); // centers placement
					i += 9;
				}
				Biome biome = world.getBiomeForCoordsBody(pos);
				if (biome == Biomes.DESERT || biome == Biomes.DESERT_HILLS || biome == Biomes.MUTATED_DESERT) {
					template = world.getStructureTemplateManager().getTemplate(server, ENTRANCE_DESERT);
				} else {
					template = world.getStructureTemplateManager().getTemplate(server, ENTRANCE_BASIC);
				}
				template.addBlocksToWorld(world, pos.add(-11, 9 + i, -11), settings); // centers placement

				// creates branches for each door
				branch(world, random, pos.add(0, 0, -8), 1, 0, 0); // north
//				branch(world, random, pos, 2, 0, 0); // east
				branch(world, random, pos.add(0, 0, 8), 3, 0, 0); // south
//				branch(world, random, pos, 4, 0, 0); // west

			}

			/**
			 * Continues a branch of a dungeon structure.
			 * 
			 * @param world
			 * @param random
			 * @param pos
			 * @param direction
			 */
			public void branch(WorldServer world, Random random, BlockPos pos, int direction, int length, int corners) {
				if (debug)
					System.out.println("Branching...");

				// fix directional overflow
				if (direction == 5)
					direction = 1;
				else if (direction == 0)
					direction = 4;

				MinecraftServer server = world.getMinecraftServer();
				Template template = null;
				PlacementSettings settings = new PlacementSettings();
				BlockPos copyPos = pos;

				switch (direction) {
				case 1:
					if (debug)
						System.out.println("going north...");
					pos = pos.add(0, 0, -5);
					settings.setRotation(Rotation.COUNTERCLOCKWISE_90);
					copyPos = pos.add(-3, 0, 2);

					break;
				case 2:
					if (debug)
						System.out.println("going east...");

					pos = pos.add(5, 0, 0);
					copyPos = pos.add(-2, 0, -3);

					break;
				case 3:
					if (debug)
						System.out.println("going south...");

					pos = pos.add(0, 0, 5);
					settings.setRotation(Rotation.CLOCKWISE_90);
					copyPos = pos.add(3, 0, -2);

					break;
				case 4:
					if (debug)
						System.out.println("going west...");
					pos = pos.add(-5, 0, 0);

					settings.setRotation(Rotation.CLOCKWISE_180);
					copyPos = pos.add(2, 0, 3);

					break;
				}

				//endings
				if (length > 5) {
					template = world.getStructureTemplateManager().getTemplate(server, ENDBASIC);
					switch(direction) {
					case 1:
						copyPos = pos.add(-5, 0, 2);
						break;
					case 2:
						break;
					case 3:
						copyPos = pos.add(5, 0, -2);
						break;
					case 4:
						break;
					}
					
				} else {
					template = world.getStructureTemplateManager().getTemplate(server, HALLWAY);

					branch(world, random, pos, direction, length + 1, corners);
				}
				
				template.addBlocksToWorld(world, copyPos, settings);

			}

			private BlockPos getTopBlock(World world, BlockPos pos, Random random) {

				int yPos = random.nextInt(30) + 30;

				pos = pos.add(0, yPos, 0);

				return pos;

			}

		}

		Dungeon dungeon = new Dungeon();

		// limits chance of spawning
		if (random.nextInt(10000) > ConfigHandler.dungeonChance) {
			return;
		}

		WorldServer sWorld = (WorldServer) world;
		int x = chunkX * 16 + random.nextInt(16);
		int z = chunkZ * 16 + random.nextInt(16);

		BlockPos xzPos = new BlockPos(x, 1, z);

		final BlockPos basePos = new BlockPos(chunkX * 16 + random.nextInt(16), 0, chunkZ * 16 + random.nextInt(16));
		final PlacementSettings settings = new PlacementSettings().setRotation(Rotation.NONE);

		// no spawning in oceans
		Biome biome = world.getBiomeForCoordsBody(xzPos);
		if (!(biome == Biomes.OCEAN || biome == Biomes.DEEP_OCEAN || biome == Biomes.BEACH || biome == Biomes.COLD_BEACH
				|| biome == Biomes.STONE_BEACH || biome == Biomes.FROZEN_OCEAN)) {
			dungeon.generateDungeon(sWorld, random, dungeon.getTopBlock(world, basePos, random));
		}

	}

}
