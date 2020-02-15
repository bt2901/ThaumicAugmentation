package thecodex6824.thaumicaugmentation.common.world.feature;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import java.util.List;
import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.template.TemplateManager;


import net.minecraft.world.gen.structure.StructureComponentTemplate;

import thecodex6824.thaumicaugmentation.api.ThaumicAugmentationAPI;


import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraft.world.storage.loot.LootTableList;

/**
 * Based on a Twilight Forest maze by Benimatic.
 */
public class PyramidMain extends StructureComponent {
    
	public int width; // cells wide (x)
	public int depth; // cells deep (z)
	
    // default values
	public static int oddBias  = 3; // corridor thickness, default 3
	public static int evenBias = 1; // wall thickness here.  NYI 
	
	// public static int height = 4; // wa////ll blocks tall
	public static int height = 6; // wall blocks tall
	public static int floorThickness = 2;
	public static int head = 0;   // blocks placed above the maze
	public static int roots = 0;  // blocks placed under the maze (used for hedge mazes)
    
	public static int levelsTall = 5;
	
	public int worldX; // set when we first copy the maze into the world
	public int worldY;
	public int worldZ;

	public long seed;
			
	protected int rawWidth;
	protected int rawDepth;
	
	public static final int OUT_OF_BOUNDS = Integer.MIN_VALUE;
    public    int cellsWidth = 23;
    public    int cellsDepth = 23;
    // public    int cellsWidth = 33;
    // public    int cellsDepth = 33;
	
    private ArrayList<PyramidMap> mazes = new ArrayList<PyramidMap>();
    
    public PyramidMain() {
        super();
    }
	public PyramidMain(World world, Random rand, int x, int y, int z) {
		super(0);

        int centerRoomX = cellsDepth/2;
        int centerRoomZ = cellsWidth/2;
		this.width = cellsWidth;
		this.depth = cellsDepth;
		this.seed = world.getSeed();
		
		this.rawWidth = width * 2 + 1;
		this.rawDepth = depth * 2 + 1;
		
        this.setCoordBaseMode(EnumFacing.NORTH);

        int radius = (int) ((cellsWidth + 2) * (evenBias + oddBias) * 0.5);
		this.boundingBox = new StructureBoundingBox(x-radius, y, z-radius, x + radius, y + height*(levelsTall+3), z + radius);
        int nrooms = 6;
        for (int i=0; i < levelsTall; ++i) {
			// TODO: clean up this
            nrooms = 7 - i; // how many rooms on this level
			int areaShrinkage = 2*i;
            mazes.add(new PyramidMap(cellsWidth - areaShrinkage, cellsDepth - areaShrinkage));
            PyramidMap newMaze = mazes.get(i);
            // set the seed to a fixed value based on this maze's x and z
            setFixedMazeSeed(newMaze, i);
            if (i == 0) {
				// TODO: i -> areaShrinkage / 2
                newMaze.addBonusRoom(centerRoomX-i, centerRoomZ-i, PyramidMap.ROOMCENTRAL);
                newMaze.addBonusRoom(centerRoomX, 2, PyramidMap.ENTRANCE);
                for (int j = 1; j <= nrooms; ++j) {
                    newMaze.addRandomRoom(2, 3, PyramidMap.ROOM_TRAP);
                }
            } else {
                for (int j = 0; j <= nrooms; ++j) {
                    int prev_x = mazes.get(i-1).rcoords[j * 3 + 0];
                    int prev_z = mazes.get(i-1).rcoords[j * 3 + 1];
                    int prev_t = mazes.get(i-1).rcoords[j * 3 + 2];                
                    if (prev_x != 0 && prev_z != 0 && prev_t != 0) {
                        int nextType = newMaze.getMatchingRoomAbove(prev_t);
                        if (nextType != 0) {
                            newMaze.addBonusRoom(prev_x - 1, prev_z - 1, nextType);
                        } else {
                            newMaze.addRandomRoom(1, 3, newMaze.randomRoomShape());
                        }
                    }
                }
            }

            // set seed again
            setFixedMazeSeed(newMaze, i);
            // make actual maze
            newMaze.generateRecursiveBacktracker(0, 0);
            if (i == 0) {
                newMaze.addTrappedCoridors();
            }
        }
	}
    			
	private void setFixedMazeSeed(PyramidMap pmap, int level) {
		pmap.setSeed(this.boundingBox.minX * 90342903 + this.boundingBox.minY * 90342903 ^ this.boundingBox.minZ + level);
	}
	
	@Override
	protected void writeStructureToNBT(NBTTagCompound par1NBTTagCompound) {
        for (int i=0; i < levelsTall; ++i) {
            String key = ("level" + i);
            if (!par1NBTTagCompound.hasKey(key)) {
                par1NBTTagCompound.setTag(key, new NBTTagCompound());
            }
            mazes.get(i).writeToNBT(par1NBTTagCompound.getCompoundTag(key));
        }
		
	}

	@Override
	protected void readStructureFromNBT(NBTTagCompound par1NBTTagCompound, TemplateManager manager) {
        mazes.clear();
        for (int i=0; i < levelsTall; ++i) {
            PyramidMap newMaze = PyramidMap.readFromNBT(par1NBTTagCompound.getCompoundTag("level" + i));
            mazes.add(newMaze);
        }
 	}
	
	public List<PyramidMap> getMazeMap() {
		return new ArrayList<PyramidMap>(this.mazes);
	}	

	
	/**
	 * Initiates construction of the Structure Component picked, at the current Location of StructGen
	 */
	@Override
	public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
		super.buildComponent(structurecomponent, list, random);
		System.out.println("Building a pyramid");
        int entrance_mode = 0;
        
        ArrayList<PyramidLevel> levels = new ArrayList<PyramidLevel>();
        int centerX = boundingBox.minX + ((boundingBox.maxX - boundingBox.minX) / 2);
        int centerZ = boundingBox.minZ + ((boundingBox.maxZ - boundingBox.minZ) / 2);
        for (int l=0; l < levelsTall; ++l) {
			// TODO: clean up this
			PyramidLevel levelBuilder = new PyramidLevel(random, 
                centerX, boundingBox.minY + (height)*l, centerZ, l, mazes.get(l));
			list.add(levelBuilder);
			levelBuilder.buildComponent(this, list, random);
            levels.add(levelBuilder);
            int[] rooms = mazes.get(l).rcoords;
            
            // add rooms where we have our coordinates
            for (int i = 0; i < rooms.length / PyramidMap.ROOM_INFO_LEN; i++) {
                int dx = rooms[i * PyramidMap.ROOM_INFO_LEN];
                int dz = rooms[i * PyramidMap.ROOM_INFO_LEN + 1];
                int type = rooms[i * PyramidMap.ROOM_INFO_LEN + 2];
        
                // add the room as a component
				ComponentPyramidRoom room = makeRoom(type, dx, dz, l, levelBuilder);
                list.add(room);
                room.buildComponent(this, list, random);
            }
			levelBuilder.describe();
        }
        // decorate rooms
        for (int l=0; l < levelsTall; ++l) {
            
            int[] rooms = mazes.get(l).rcoords;
            for (int i = 0; i < rooms.length / PyramidMap.ROOM_INFO_LEN; i++) {
                int dx = rooms[i * PyramidMap.ROOM_INFO_LEN];
                int dz = rooms[i * PyramidMap.ROOM_INFO_LEN + 1];
                int type = rooms[i * PyramidMap.ROOM_INFO_LEN + 2];
                
                ComponentPyramidRoom room = null;
                if (type == PyramidMap.ROOM_NO_CEILING_FANCY_ENTRANCE || type == PyramidMap.ROOM_NO_CEILING) {
                    float r = random.nextFloat();
                    if (r > 0.75) {
                        room = makeRoom(PyramidMap.ROOM_VPR, dx, dz, l, levels.get(l));
                    }
                    if (r <= 0.75) {
                        room = makeRoom(PyramidMap.ROOM_GARDEN, dx, dz, l, levels.get(l));
                    }
                }
                if (type == PyramidMap.CORIDOR_BLOCKED) {
					// TODO
				}
                if (type == PyramidMap.ENTRANCE) {
                    // if (rand.nextFloat() > 0.33) {
                    room = makeRoom(PyramidMap.ENTRANCE, dx, dz, entrance_mode, levels.get(l));
                    //}
                }
                if (room != null) {
                    list.add(room);
                    room.buildComponent(this, list, random);
                }				
            }
        }
        
	}
    protected ComponentPyramidRoom makeRoom(int type, int dx, int dz, int i, PyramidLevel levelBuilder) {

        Random random = new Random((i + 1) * this.seed + dx * 153 + dz * 615);

		// TODO: without asymmetric -3 the 1.12 version places rooms wrong. Can anyone explain why?
		int worldX = levelBuilder.getBoundingBox().minX + dx * (evenBias + oddBias) - 3;
		int worldY = levelBuilder.getBoundingBox().minY;
		int worldZ = levelBuilder.getBoundingBox().minZ + dz * (evenBias + oddBias) - 1;
        if (type == PyramidMap.ROOMCENTRAL) {
            return new ComponentPyramidCentralRoom(random, worldX, worldY, worldZ, i);
        }
        if (type == PyramidMap.ROOM_VPR) {
            return new ComponentGardenRoom(random, worldX, worldY, worldZ);
            // return new ComponentVoidProductionRoom(random, worldX, worldY, worldZ);
        }
        if (type == PyramidMap.ROOM_GARDEN) {
            return new ComponentGardenRoom(random, worldX, worldY, worldZ);
        }
        if (type == PyramidMap.ENTRANCE) {
            return new ComponentPyramidEntrance(random, worldX, worldY, worldZ, i);
        }
        return new ComponentPyramidRoom(random, worldX, worldY, worldZ, type);
	}

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        int l = (this.boundingBox.maxX - this.boundingBox.minX) + 2; // TODO: why +2??
        int startH = -4;
        // int endH = (height)*(levelsTall + 2) + startH;
		// debug: fill with outside walls only one level
        int endH = 3 * height + startH;
        // if (UnravelingConfig.debug) {
        //     endH = 1;
        // }
		
        for (int i=startH; i <= endH; ++i) {
            fillWithBlocks(world, sbb, i, i, i, l - i, i, l - i, 
                PyramidMaterials.outerBlock, PyramidMaterials.outerBlock, false);
        }
		return true;
	}

	public static class RoomTemplate extends StructureComponentTemplate {
            private String templateName;
            private Rotation rotation;
            private Mirror mirror;

            public RoomTemplate() { }

            public RoomTemplate(TemplateManager p_i47355_1_, String p_i47355_2_, BlockPos p_i47355_3_, Rotation p_i47355_4_) {
                this(p_i47355_1_, p_i47355_2_, p_i47355_3_, p_i47355_4_, Mirror.NONE);
            }

            public RoomTemplate(TemplateManager p_i47356_1_, String p_i47356_2_, BlockPos p_i47356_3_, Rotation p_i47356_4_, Mirror p_i47356_5_) {
                super(0);
                this.templateName = p_i47356_2_;
                this.templatePosition = p_i47356_3_;
                this.rotation = p_i47356_4_;
                this.mirror = p_i47356_5_;
                this.loadTemplate(p_i47356_1_);
            }

            private void loadTemplate(TemplateManager p_191081_1_) {
                Template template = p_191081_1_.getTemplate(
					(MinecraftServer)null, 
					new ResourceLocation(ThaumicAugmentationAPI.MODID, "structures/" + this.templateName)
				);
				System.out.println("trying to load template " + this.templateName);
                PlacementSettings placementsettings = (new PlacementSettings()).setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror);
                this.setup(template, this.templatePosition, placementsettings);
            }

            /**
             * (abstract) Helper method to write subclass data to NBT
             */
            protected void writeStructureToNBT(NBTTagCompound tagCompound)
            {
                super.writeStructureToNBT(tagCompound);
                tagCompound.setString("Template", this.templateName);
                tagCompound.setString("Rot", this.placeSettings.getRotation().name());
                tagCompound.setString("Mi", this.placeSettings.getMirror().name());
            }

            /**
             * (abstract) Helper method to read subclass data from NBT
             */
            protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager p_143011_2_)
            {
                super.readStructureFromNBT(tagCompound, p_143011_2_);
                this.templateName = tagCompound.getString("Template");
                this.rotation = Rotation.valueOf(tagCompound.getString("Rot"));
                this.mirror = Mirror.valueOf(tagCompound.getString("Mi"));
                this.loadTemplate(p_143011_2_);
            }

            protected void handleDataMarker(String function, BlockPos pos, World worldIn, Random rand, StructureBoundingBox sbb)
            {
                if (function.startsWith("Chest"))
                {
                    Rotation rotation = this.placeSettings.getRotation();
                    IBlockState iblockstate = Blocks.CHEST.getDefaultState();

                    if ("ChestWest".equals(function))
                    {
                        iblockstate = iblockstate.withProperty(BlockChest.FACING, rotation.rotate(EnumFacing.WEST));
                    }
                    else if ("ChestEast".equals(function))
                    {
                        iblockstate = iblockstate.withProperty(BlockChest.FACING, rotation.rotate(EnumFacing.EAST));
                    }
                    else if ("ChestSouth".equals(function))
                    {
                        iblockstate = iblockstate.withProperty(BlockChest.FACING, rotation.rotate(EnumFacing.SOUTH));
                    }
                    else if ("ChestNorth".equals(function))
                    {
                        iblockstate = iblockstate.withProperty(BlockChest.FACING, rotation.rotate(EnumFacing.NORTH));
                    }

                    this.generateChest(worldIn, sbb, rand, pos, LootTableList.CHESTS_WOODLAND_MANSION, iblockstate);
                }
            }
        }

}

