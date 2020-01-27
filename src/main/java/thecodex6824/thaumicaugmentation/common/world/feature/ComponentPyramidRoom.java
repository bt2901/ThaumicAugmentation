package thecodex6824.thaumicaugmentation.common.world.feature;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;


import net.minecraft.nbt.NBTTagCompound;

import thecodex6824.thaumicaugmentation.common.world.feature.PyramidMaterials;


import net.minecraft.world.gen.structure.template.TemplateManager;


public class ComponentPyramidRoom extends StructureComponent {
    int roomHeight;
    int roomWidth;
    int roomDepth;
    int type;

	public ComponentPyramidRoom() {
		super();
	}
    
	public ComponentPyramidRoom(Random rand, int x, int y, int z, int type) {
		super(type);

        // this.setCoordBaseMode(EnumFacing.HORIZONTALS[rand.nextInt(4)]);
        this.setCoordBaseMode(EnumFacing.HORIZONTALS[2]);
        this.type = type;
        roomWidth = (PyramidMain.oddBias + PyramidMain.evenBias) * 3;
        roomDepth = (PyramidMain.oddBias + PyramidMain.evenBias) * 3;
        roomHeight = PyramidMain.height;
        if (type == PyramidMap.ROOM_NO_CEILING) {
            roomHeight = PyramidMain.height * 2;
        }
        this.boundingBox = new StructureBoundingBox(x, y, z, x + roomWidth, y + roomHeight, z + roomDepth);
        if (type == PyramidMap.ENTRANCE) {
            this.boundingBox = new StructureBoundingBox(x, y, z - 3 * roomDepth, x + roomWidth, y + roomHeight, z + roomDepth);
        }
	}
	
	public BlockPos getBlockPosWithOffset(int x, int y, int z) {
		return new BlockPos(
				getXWithOffset(x, z),
				getYWithOffset(y),
				getZWithOffset(x, z)
		);
	}
    public void fillWithWalls(World world, StructureBoundingBox sbb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
       fillWithBlocks(world, sbb, minX, minY, minZ, maxX, maxY, maxZ, PyramidMaterials.wallBlock, PyramidMaterials.wallBlock, false);
    }
    public void fillAroundHorizontally(World world, StructureBoundingBox sbb, int myX, int myY, int myZ, int radius, IBlockState what) {
       fillWithBlocks(world, sbb, myX - radius, myY, myZ - radius, myX + radius, myY, myZ + radius, what, what, false);
    }
	public void surroundBlockCardinal(World world, IBlockState block, int x, int y, int z, StructureBoundingBox sbb) {
		setBlockState(world, block, x + 0, y, z - 1, sbb);
		setBlockState(world, block, x + 0, y, z + 1, sbb);
		setBlockState(world, block, x - 1, y, z + 0, sbb);
		setBlockState(world, block, x + 1, y, z + 0, sbb);
	}
	public void surroundBlockCorners(World world, IBlockState block, int x, int y, int z, StructureBoundingBox sbb) {
		setBlockState(world, block, x - 1, y, z - 1, sbb);
		setBlockState(world, block, x - 1, y, z + 1, sbb);
		setBlockState(world, block, x + 1, y, z - 1, sbb);
		setBlockState(world, block, x + 1, y, z + 1, sbb);
	}

    public void makeFancyEntrance(World world, StructureBoundingBox sbb) {
        int pace = PyramidMain.oddBias + PyramidMain.evenBias;
        int h = 2;

        // setBlockState(world, PyramidMaterials.Entrance, 0, h, pace - 2, sbb);
        // setBlockState(world, PyramidMaterials.Entrance, 0, h, 2*pace - 2, sbb);
        setBlockState(world, PyramidMaterials.Entrance, 0, h, pace, sbb);
        setBlockState(world, PyramidMaterials.Entrance, 0, h, 2*pace, sbb);

        setBlockState(world, PyramidMaterials.Entrance, pace, h, 0, sbb);
        setBlockState(world, PyramidMaterials.Entrance, 2*pace, h, 0, sbb);

        setBlockState(world, PyramidMaterials.Entrance, roomWidth, h, pace, sbb);
        setBlockState(world, PyramidMaterials.Entrance, roomWidth, h, 2*pace, sbb);

        setBlockState(world, PyramidMaterials.Entrance, pace, h, roomDepth, sbb);
        setBlockState(world, PyramidMaterials.Entrance, 2*pace, h, roomDepth, sbb);
    }


	/**
	 * Initiates construction of the Structure Component picked, at the current Location of StructGen
	 */
	@Override
	public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
        if (type == PyramidMap.ROOM_NO_CEILING_FANCY_ENTRANCE || type == PyramidMap.ROOM_NO_CEILING) {
        }
        /*
        if (type == PyramidMap.ROOM_TRAP) {
            int randInt = random.nextInt(2);
            for (int i=0; i < 4; ++i) {
                ComponentPyramidTrap trapBuilder = new ComponentPyramidTrap(random, 
                    boundingBox.minX, boundingBox.minY, boundingBox.minZ, i, randInt);
                list.add(trapBuilder);
                trapBuilder.buildComponent(this, list, random);
            }
        }*/
	}

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        
        // if (type == PyramidMap.ROOM2HIGH || type == PyramidMap.ROOM_NO_CEILING_FANCY_ENTRANCE || type == PyramidMap.ROOMCENTRAL || type == PyramidMap.ROOM_NO_CEILING) {
        if (type == PyramidMap.ROOM_NO_CEILING_FANCY_ENTRANCE || type == PyramidMap.ROOM2HIGH || type == PyramidMap.ROOM) {
            makeFancyEntrance(world, sbb);
        }
        if (type == PyramidMap.ROOM_NO_CEILING) {
        }
        if (type == PyramidMap.ROOM_VIRTUAL) {
            fillWithAir(world, sbb, 1, 0, 1, roomWidth - 1, roomHeight - 1, roomDepth - 1);
        }
        if (type == PyramidMap.ROOM2HIGH || type == PyramidMap.ROOM_VIRTUAL) {
            fillWithWalls(world, sbb, 0, 0, 0, roomWidth, 0, roomDepth);
            fillWithAir(world, sbb, 1, 0, 1, roomWidth - 1, 0, roomDepth - 1);
        }
        if (type == PyramidMap.ROOM_VIRTUAL) {
            // if (rand.nextFloat() > 0.33) {
                fillWithAir(world, sbb, roomWidth, 1, 1, roomWidth + PyramidMain.oddBias, roomHeight - 1, roomDepth - 1);
            //}
        }
        if (type == PyramidMap.ROOM_NO_CEILING_FANCY_ENTRANCE || type == PyramidMap.ROOM_NO_CEILING) {
            fillWithAir(world, sbb, 1, roomHeight, 1, roomWidth - 1, roomHeight, roomDepth - 1);
        }
        return true;
	}
	/**
	 * Save to NBT
	 */
	@Override
	protected void writeStructureToNBT(NBTTagCompound par1NBTTagCompound) {
		
        par1NBTTagCompound.setInteger("roomHeight", roomHeight);
        par1NBTTagCompound.setInteger("roomDepth", roomDepth);
        par1NBTTagCompound.setInteger("roomWidth", roomWidth);
        par1NBTTagCompound.setInteger("type", type);
	}

	/**
	 * Load from NBT
	 */
	@Override
	protected void readStructureFromNBT(NBTTagCompound par1NBTTagCompound, TemplateManager manager) {
		this.roomHeight = par1NBTTagCompound.getInteger("roomHeight");
        this.roomWidth = par1NBTTagCompound.getInteger("roomWidth");
        this.roomDepth = par1NBTTagCompound.getInteger("roomDepth");
        this.type = par1NBTTagCompound.getInteger("type");
 	}
}
