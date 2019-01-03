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

public class DungeonGenerator implements IWorldGenerator {

	public static List<ResourceLocation> center;
	public static List<ResourceLocation> hallway;
	public static List<ResourceLocation> end;
	public static List<ResourceLocation> corner_left;
	public static List<ResourceLocation> corner_right;
	public static List<ResourceLocation> t_split;
	public static List<ResourceLocation> threeway;

	boolean debug = false;

	public static final ResourceLocation STAIRS = new ResourceLocation("dungeonmod", "dungeon/dp_stairs");
	public static final ResourceLocation ENTRANCE_GOLD = new ResourceLocation("dungeonmod", "dungeon/dp_entrance_gold");
	public static final ResourceLocation DOWNSTEP = new ResourceLocation("dungeonmod", "dungeon/dp_downlevel");

	public static Map<String, Integer> chances = new HashMap<String, Integer>();

	public DungeonGenerator() {

		center = new ArrayList<ResourceLocation>();
		hallway = new ArrayList<ResourceLocation>();
		end = new ArrayList<ResourceLocation>();
		corner_left = new ArrayList<ResourceLocation>();
		corner_right = new ArrayList<ResourceLocation>();
		t_split = new ArrayList<ResourceLocation>();
		threeway = new ArrayList<ResourceLocation>();

		chances.put("hallway", 25);
		chances.put("end", 15);
		chances.put("corner_left", 10);
		chances.put("corner_right", 10);
		chances.put("t_split", 8);
		chances.put("threeway", 1);

		InputStream stream = this.getClass().getClassLoader()
				.getResourceAsStream("assets/dungeonmod/structures/dungeon/center");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		try {
			while (reader.ready()) {
				String e = reader.readLine();
				center.add(new ResourceLocation("dungeonmod", "dungeon/center/" + e.replaceAll(".nbt", "")));
				System.out.println(e);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stream = this.getClass().getClassLoader().getResourceAsStream("assets/dungeonmod/structures/dungeon/hall");
		reader = new BufferedReader(new InputStreamReader(stream));
		try {
			while (reader.ready()) {
				String e = reader.readLine();
				hallway.add(new ResourceLocation("dungeonmod", "dungeon/hall/" + e.replaceAll(".nbt", "")));
				System.out.println(e);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stream = this.getClass().getClassLoader().getResourceAsStream("assets/dungeonmod/structures/dungeon/end");
		reader = new BufferedReader(new InputStreamReader(stream));
		try {
			while (reader.ready()) {
				String e = reader.readLine();
				end.add(new ResourceLocation("dungeonmod", "dungeon/end/" + e.replaceAll(".nbt", "")));
				System.out.println(e);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stream = this.getClass().getClassLoader()
				.getResourceAsStream("assets/dungeonmod/structures/dungeon/corner/right");
		reader = new BufferedReader(new InputStreamReader(stream));
		try {
			while (reader.ready()) {
				String e = reader.readLine();
				corner_right
						.add(new ResourceLocation("dungeonmod", "dungeon/corner/right/" + e.replaceAll(".nbt", "")));
				System.out.println(e);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stream = this.getClass().getClassLoader()
				.getResourceAsStream("assets/dungeonmod/structures/dungeon/corner/left");
		reader = new BufferedReader(new InputStreamReader(stream));
		try {
			while (reader.ready()) {
				String e = reader.readLine();
				corner_left.add(new ResourceLocation("dungeonmod", "dungeon/corner/left/" + e.replaceAll(".nbt", "")));
				System.out.println(e);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stream = this.getClass().getClassLoader().getResourceAsStream("assets/dungeonmod/structures/dungeon/tsplit");
		reader = new BufferedReader(new InputStreamReader(stream));
		try {
			while (reader.ready()) {
				String e = reader.readLine();
				t_split.add(new ResourceLocation("dungeonmod", "dungeon/tsplit/" + e.replaceAll(".nbt", "")));
				System.out.println(e);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		stream = this.getClass().getClassLoader().getResourceAsStream("assets/dungeonmod/structures/dungeon/threeway");
		reader = new BufferedReader(new InputStreamReader(stream));
		try {
			while (reader.ready()) {
				String e = reader.readLine();
				threeway.add(new ResourceLocation("dungeonmod", "dungeon/threeway/" + e.replaceAll(".nbt", "")));
				System.out.println(e);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// File f = new File("assets/dungeonmod/structures/dungeon");
		// System.out.println(f.getAbsolutePath().toString());
		// File centers = new File(f.getPath().toString() + "/center");
		// ArrayList<String> names = new
		// ArrayList<String>(Arrays.asList(centers.list()));
		// for(int x = 0; x < names.size(); x ++) {
		// center.add(new ResourceLocation("dungeonmod", "dungeon/" +
		// names.get(x).replaceAll(".nbt", "")));
		// }
		//
		// for(int x = 0; x < center.size(); x ++) {
		// System.out.println(center.get(x));
		// }

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
				BlockPos middle = pos.add(0, 5, 0);

				MinecraftServer server = world.getMinecraftServer();
				Template template = world.getStructureTemplateManager().getTemplate(server,
						center.get(random.nextInt(center.size())));
				PlacementSettings settings = new PlacementSettings();

				// template.getDataBlocks(pos, placementIn)
				template.addBlocksToWorld(world, pos.add(-4, 0, -4), settings); // centers placement

				world.setBlockToAir(middle);
				world.setBlockToAir(middle.add(0, 0, 1));
				world.setBlockToAir(middle.add(0, 0, -1));
				world.setBlockToAir(middle.add(1, 0, 0));
				world.setBlockToAir(middle.add(-1, 0, 0));

				template = world.getStructureTemplateManager().getTemplate(server, STAIRS);

				int i = 0;
				// || pos.getY() < 62
				while ((!world.getBlockState(pos.add(-3, 6 + i, -3)).getBlock().equals(Blocks.AIR))
						&& (pos.add(-3, 6 + i, -3).getY() < 62)) {
					template.addBlocksToWorld(world, pos.add(-3, 6 + i, -3), settings); // centers placement
					i += 6;
				}
				template = world.getStructureTemplateManager().getTemplate(server, ENTRANCE_GOLD);
				template.addBlocksToWorld(world, pos.add(-4, 6 + i, -4), settings); // centers placement

				// creates branches for each door
				branch(world, random, pos, 1, 0, 0); // north
				branch(world, random, pos, 2, 0, 0); // east
				branch(world, random, pos, 3, 0, 0); // south
				branch(world, random, pos, 4, 0, 0); // west

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

				// int choice = random.nextInt(6); // 4 for no corners, 6 for corners

				int choice = 0;
				int total = 0;
				total += chances.get("hallway");
				total += chances.get("end");
				total += chances.get("corner_left");
				total += chances.get("corner_right");
				total += chances.get("t_split");
				total += chances.get("threeway");

				choice = random.nextInt(total);

				PlacementSettings settings = new PlacementSettings();
				BlockPos copyPos = pos;
				// System.out.println("Before change: " + pos);
				switch (direction) {
				case 1:
					if (debug)
						System.out.println("going north...");
					pos = pos.add(0, 0, -8);
					settings.setRotation(Rotation.COUNTERCLOCKWISE_90);
					copyPos = pos.add(-4, 0, 4);

					if (rooms.contains(pos.add(0, 0, -8))) {

						choice = random.nextInt(2) + 4;
					}

					if (rooms.contains(pos.add(8, 0, 0))) {
						choice = 5;
						if (rooms.contains(pos.add(-8, 0, 0))) {
							choice = 3;
						}
					} else if (rooms.contains(pos.add(-8, 0, 0))) {
						choice = 4;
					}

					break;
				case 2:
					if (debug)
						System.out.println("going east...");

					pos = pos.add(8, 0, 0);
					copyPos = pos.add(-4, 0, -4);

					if (rooms.contains(pos.add(8, 0, 0))) {
						choice = random.nextInt(2) + 4;
					}
					if (rooms.contains(pos.add(0, 0, -8))) {
						choice = 5;
						if (rooms.contains(pos.add(0, 0, 8))) {
							choice = 3;
						}
					} else if (rooms.contains(pos.add(0, 0, 8))) {
						choice = 4;
					}

					break;
				case 3:
					if (debug)
						System.out.println("going south...");

					pos = pos.add(0, 0, 8);
					settings.setRotation(Rotation.CLOCKWISE_90);
					copyPos = pos.add(4, 0, -4);

					if (rooms.contains(pos.add(0, 0, 8))) {
						choice = random.nextInt(2) + 4;
					}

					if (rooms.contains(pos.add(8, 0, 0))) {
						choice = 4;
						if (rooms.contains(pos.add(-8, 0, 0))) {
							choice = 3;
						}
					} else if (rooms.contains(pos.add(-8, 0, 0))) {
						choice = 5;
					}

					break;
				case 4:
					if (debug)
						System.out.println("going west...");
					pos = pos.add(-8, 0, 0);

					settings.setRotation(Rotation.CLOCKWISE_180);

					copyPos = pos.add(4, 0, 4);

					if (rooms.contains(pos.add(-8, 0, 0))) {
						choice = random.nextInt(2) + 4;
					}

					if (rooms.contains(pos.add(0, 0, -8))) {
						choice = 4;
						if (rooms.contains(pos.add(0, 0, 8))) {
							choice = 3;
						}
					} else if (rooms.contains(pos.add(0, 0, 8))) {
						choice = 5;
					}

					break;
				}

				if (debug)
					System.out.println("After change: " + pos);

				if ((length < 7 && choice == 3) || length < 3)
					choice = 2;

				if (corners >= 2 && choice == 4) {
					choice = 5;
				} else if (corners <= -2 && choice == 5) {
					choice = 4;
				}

				// if(length == -1) {
				// choice = (chances.get("hallway") + chances.get("end") +
				// chances.get("corner_left")
				// + chances.get("corner_right") + chances.get("t_split") +
				// chances.get("threeway")) + 1;
				// }
				// length = 0;
				if (length == -1) {
					template = world.getStructureTemplateManager().getTemplate(server,
							threeway.get(random.nextInt(threeway.size())));
					// turnChange = -1;
					// corners = corners - 1;
					branch(world, random, pos, direction - 1, length + 1, corners - 1);
					branch(world, random, pos, direction, length + 1, corners);
					branch(world, random, pos, direction + 1, length + 1, corners + 1);
				} else {
					if (choice < chances.get("hallway")) {
						if (debug)
							System.out.println("Hallway");
						template = world.getStructureTemplateManager().getTemplate(server,
								hallway.get(random.nextInt(hallway.size())));

						branch(world, random, pos, direction, length + 1, corners);
					} else if (choice < (chances.get("hallway") + chances.get("end"))) {
						if (debug)
							System.out.println("Ending.");

						if (random.nextInt(10) == 0) {
							template = world.getStructureTemplateManager().getTemplate(server, DOWNSTEP);
							copyPos = copyPos.add(0, -6, 0);
							rooms.add(copyPos);
							if (direction == 1)
								direction = 3;
							else if (direction == 3)
								direction = 1;
							else if (direction == 2)
								direction = 4;
							else if (direction == 4)
								direction = 2;
							branch(world, random, pos.add(0, -6, 0), direction, -1, corners);

						} else {
							template = world.getStructureTemplateManager().getTemplate(server,
									end.get(random.nextInt(end.size())));
						}

					} else if (choice < (chances.get("hallway") + chances.get("end") + chances.get("corner_left"))) {
						if (debug)
							System.out.println("Right Corner.");
						template = world.getStructureTemplateManager().getTemplate(server,
								corner_right.get(random.nextInt(corner_right.size())));
						// turnChange = 1;
						// corners = corners + 1;
						branch(world, random, pos, direction + 1, length + 1, corners + 1);
					} else if (choice < (chances.get("hallway") + chances.get("end") + chances.get("corner_left")
							+ chances.get("corner_right"))) {
						if (debug)
							System.out.println("Left Corner.");
						template = world.getStructureTemplateManager().getTemplate(server,
								corner_left.get(random.nextInt(corner_left.size())));
						// turnChange = -1;
						// corners = corners - 1;
						branch(world, random, pos, direction - 1, length + 1, corners - 1);

					} else if (choice < (chances.get("hallway") + chances.get("end") + chances.get("corner_left")
							+ chances.get("corner_right") + chances.get("t_split"))) {
						if (debug)
							System.out.println("T Split");
						template = world.getStructureTemplateManager().getTemplate(server,
								t_split.get(random.nextInt(t_split.size())));
						// turnChange = -1;
						// corners = corners - 1;
						branch(world, random, pos, direction - 1, length + 1, corners - 1);
						branch(world, random, pos, direction + 1, length + 1, corners + 1);
					} else if (choice < (chances.get("hallway") + chances.get("end") + chances.get("corner_left")
							+ chances.get("corner_right") + chances.get("t_split") + chances.get("threeway"))) {
						if (debug)
							System.out.println("Threeway");
						template = world.getStructureTemplateManager().getTemplate(server,
								threeway.get(random.nextInt(threeway.size())));
						// turnChange = -1;
						// corners = corners - 1;
						branch(world, random, pos, direction - 1, length + 1, corners - 1);
						branch(world, random, pos, direction, length + 1, corners);
						branch(world, random, pos, direction + 1, length + 1, corners + 1);
					}
				}
				template.addBlocksToWorld(world, copyPos, settings);

				rooms.add(pos);
				if (debug)
					System.out.println("Size of maze = " + rooms.size());

				// if (continueGen) {
				// branch(world, random, pos, direction + turnChange, length + 1, corners);
				// } else {
				// return;
				// }

			}

			private BlockPos getTopBlock(World world, BlockPos pos, Random random) {
				// Chunk chunk = world.getChunkFromBlockCoords(pos);
				// BlockPos blockpos;
				// BlockPos blockpos1;
				//
				// for(blockpos = new BlockPos(pos.getX(), chunk.getTopFilledSegment() + 16,
				// pos.getZ()); blockpos.getY() >= 0; blockpos = blockpos1) {
				// blockpos1 = blockpos.down();
				// IBlockState state = chunk.getBlockState(blockpos1);
				//
				// if(!state.getBlock().equals(Blocks.AIR))
				// break;
				// }
				//
				// return blockpos;

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
		if (!(biome == Biomes.OCEAN || biome == Biomes.DEEP_OCEAN)) {
			dungeon.generateDungeon(sWorld, random, dungeon.getTopBlock(world, basePos, random));
		}

	}

}
