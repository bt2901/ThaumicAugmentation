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



/**
 * Based on a Twilight Forest maze by Benimatic.
 */
public class PyramidMain extends StructureComponent {
    
	public int width; // cells wide (x)
	public int depth; // cells deep (z)
	
    // default values
	public static int oddBias  = 3; // corridor thickness, default 3
	public static int evenBias = 1; // wall thickness here.  NYI 
	
	public static int height = 4; // wall blocks tall
	public static int head = 0;   // blocks placed above the maze
	public static int roots = 0;  // blocks placed under the maze (used for hedge mazes)

	public static int floorThickness = 1;
    
	public static int levelsTall = 7;
	
	public int worldX; // set when we first copy the maze into the world
	public int worldY;
	public int worldZ;
			
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
		
		this.rawWidth = width * 2 + 1;
		this.rawDepth = depth * 2 + 1;
		
        this.setCoordBaseMode(EnumFacing.NORTH);

        int radius = (int) ((cellsWidth + 2) * (evenBias + oddBias) * 0.5);
		this.boundingBox = new StructureBoundingBox(x-radius, y, z-radius, x + radius, y + height*(levelsTall+3), z + radius);
        int nrooms = 6;
        for (int i=0; i < levelsTall; ++i) {
            nrooms = (i > 3)? 3 : 7;
            mazes.add(new PyramidMap(cellsWidth - height*i/2, cellsDepth - height*i/2));
            PyramidMap newMaze = mazes.get(i);
            // set the seed to a fixed value based on this maze's x and z
            setFixedMazeSeed(newMaze, i);
            if (i == 0) {
                newMaze.addBonusRoom(centerRoomX-i, centerRoomZ-i, PyramidMap.ROOMCENTRAL);
                newMaze.addBonusRoom(centerRoomX, 2, PyramidMap.ENTRANCE);
                for (int j = 1; j <= nrooms; ++j) {
                    newMaze.addRandomRoom(2, 3, PyramidMap.ROOM_TRAP);
                }
            } else {
                for (int j = 0; j <= nrooms; ++j) {
                    int prev_x = mazes.get(i-1).rcoords[j * 3];
                    int prev_z = mazes.get(i-1).rcoords[j * 3 + 1];
                    int prev_t = mazes.get(i-1).rcoords[j * 3 + 2];                
                    if (prev_x != 0 && prev_z != 0 && prev_t != 0) {
                        int nextType = newMaze.matchingRoom(prev_t);
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
                ComponentPyramidRoom room = makeRoom(random, type, dx, dz, l, levelBuilder);
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
                if (type == PyramidMap.ROOM2LOW || type == PyramidMap.ROOM2SUDDEN_LOW) {
                    float r = random.nextFloat();
                    if (r > 0.75) {
                        room = makeRoom(random, PyramidMap.ROOM_VPR, dx, dz, l, levels.get(l));
                    }
                    if (r <= 0.75) {
                        room = makeRoom(random, PyramidMap.ROOM_GARDEN, dx, dz, l, levels.get(l));
                    }
                }
                if (type == PyramidMap.ENTRANCE) {
                    // if (rand.nextFloat() > 0.33) {
                    room = makeRoom(random, PyramidMap.ENTRANCE, dx, dz, entrance_mode, levels.get(l));
                    //}
                }
                if (room != null) {
                    list.add(room);
                    room.buildComponent(this, list, random);
                }
            }
        }
        
	}
    protected ComponentPyramidRoom makeRoom(Random random, int type, int dx, int dz, int i, PyramidLevel levelBuilder) {

		// TODO: seems like one of -3 is wrong, but why and which one?
		int worldX = levelBuilder.getBoundingBox().minX + dx * (evenBias + oddBias) - 3;
		int worldY = levelBuilder.getBoundingBox().minY;
		int worldZ = levelBuilder.getBoundingBox().minZ + dz * (evenBias + oddBias) - 3;
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
        int endH = (height)*(levelsTall + 2) + startH;
        // if (UnravelingConfig.debug) {
        //     endH = 1;
        // }
        for (int i=startH; i <= endH; ++i) {
            fillWithBlocks(world, sbb, i, i, i, l - i, i, l - i, 
                PyramidMaterials.outerBlock, PyramidMaterials.outerBlock, false);
        }
		return true;
	}


}

