package thecodex6824.thaumicaugmentation.common.world.feature;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import thecodex6824.thaumicaugmentation.api.TABlocks;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;


import net.minecraft.block.state.IBlockState;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;


import thaumcraft.common.config.ConfigBlocks;


public class ComponentPyramidCentralRoom extends ComponentPyramidRoom {
    int level;
	private static EnumFacing[] facingOrder = {EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.SOUTH};

	public ComponentPyramidCentralRoom() {
		super();
	}

	public ComponentPyramidCentralRoom(Random rand, int x, int y, int z, int i) {
		super(rand, x, y, z, PyramidMap.ROOMCENTRAL);
        this.level = i;
        this.setCoordBaseMode(facingOrder[level % 4]);
	}
    
    public void createDoorway(World world, StructureBoundingBox sbb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IBlockState what) {
        fillWithBlocks(world, sbb, minX, minY, minZ, maxX, maxY, maxZ, what, what, false);
        int cX = (minX + maxX)/2;
        int cZ = (minZ + maxZ)/2;
        setBlockState(world, Blocks.AIR.getDefaultState(), cX, minY, cZ, sbb);
        setBlockState(world, Blocks.AIR.getDefaultState(), cX, minY + 1, cZ, sbb);
    }

    public void createPlatforms(World world, StructureBoundingBox sbb) {
        int myX; 
        int myZ; 
        
        myX = 1;
        myZ = roomDepth / 2;
        fillAroundHorizontally(world, sbb, myX, 0, myZ, 1, PyramidMaterials.wallBlock);
        myX = roomWidth - 1;
        myZ = roomDepth / 2;
        fillAroundHorizontally(world, sbb, myX, 0, myZ, 1, PyramidMaterials.wallBlock);
        myX = roomWidth / 2;
        myZ = 1;
        fillAroundHorizontally(world, sbb, myX, 0, myZ, 1, PyramidMaterials.wallBlock);
        myX = roomWidth / 2;
        myZ = roomDepth - 1;
        fillAroundHorizontally(world, sbb, myX, 0, myZ, 1, PyramidMaterials.wallBlock);
    }
	
    public void createFourDoorways(World world, StructureBoundingBox sbb) {
            // Block brick = TABlocks.ebricks;
            IBlockState brick = Blocks.COBBLESTONE.getDefaultState();

            int minX; 
            int minZ; 
            int maxX; 
            int maxZ; 
            minX = 0; maxX = 0;
            minZ = roomDepth / 2 - 1;
            maxZ = roomDepth / 2 + 1;
            
            createDoorway(world, sbb, minX, 1, minZ, maxX, 3, maxZ, brick);
            minX = roomWidth; maxX = roomWidth;
            minZ = roomDepth / 2 - 1;
            maxZ = roomDepth / 2 + 1;
            createDoorway(world, sbb, minX, 1, minZ, maxX, 3, maxZ, brick);
            minX = roomWidth/2 - 1; 
            maxX = roomWidth/2 + 1;
            minZ = roomDepth; maxZ = roomDepth;
            createDoorway(world, sbb, minX, 1, minZ, maxX, 3, maxZ, brick);
            minX = roomWidth/2 - 1; 
            maxX = roomWidth/2 + 1;
            minZ = 0; maxZ = 0;
            createDoorway(world, sbb, minX, 1, minZ, maxX, 3, maxZ, brick);
    }

    public void makeStairsXPos(World world, StructureBoundingBox sbb, int minZ, int maxZ, int startX, int startY, int howLong) {
        int x = startX;
        int y = startY;
		IBlockState brick = PyramidMaterials.wallBlock;
        for (int z = minZ; z <= maxZ; ++z) {
            for (int i = 0; i <= howLong; ++i) {
                x = startX + i;
                y = (i == 0)? startY : startY + i - 1;
                setBlockState(world, brick, x, y, z, sbb);
            }
        }
    }
    
    public void makeStairsZPos(World world, StructureBoundingBox sbb, int minX, int maxX, int startZ, int startY, int howLong) {
        int z = startZ;
        int y = startY;
		IBlockState brick = PyramidMaterials.wallBlock;
        for (int x = minX; x <= maxX; ++x) {
            for (int i = 0; i <= howLong; ++i) {
                z = startZ + i;
                y = (i == 0)? startY : startY + i - 1;
                setBlockState(world, brick, x, y, z, sbb);
            }
        }
    }
    
    public void makeStairs(World world, StructureBoundingBox sbb) {
        int minVal = 1;
        int startY = 0;
        int howLong = 3;
		// TODO: try to not use this, instead using coordbase mode
        int clock = level % 8;
		makeStairsXPos(world, sbb, minVal, minVal + 1, roomWidth/2 + 1, startY, howLong);

		int maxVal = roomWidth - 1;
        startY = howLong - 1;
        int startPos = 2;
		makeStairsZPos(world, sbb, maxVal - 1, maxVal, startPos, startY, howLong);
        /*
		if (clock == 0) {
            makeStairsXPos(world, sbb, minVal, minVal + 1, roomWidth/2 + 1, startY, howLong);
        } else {
            makeStairsZPos(world, sbb, minVal, minVal + 1, roomDepth/2 + 1, startY, howLong);
        }
        startY = howLong - 1;
        int startPos = 2;
        if (clock == 0) {
            int maxVal = roomWidth - 1;
            makeStairsZPos(world, sbb, maxVal - 1, maxVal, startPos, startY, howLong);
        } else {
            int maxVal = roomDepth - 1;
            makeStairsXPos(world, sbb, maxVal - 1, maxVal, startPos, startY, howLong);
        }*/
    }
	
	/**
	 * Initiates construction of the Structure Component picked, at the current Location of StructGen
	 */
	@Override
	public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
	}

	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        fillWithAir(world, sbb, 1, 0, 1, roomWidth - 1, roomHeight - 1, roomDepth - 1);
		/*
        createPlatforms(world, sbb);
        createFourDoorways(world, sbb);
        makeStairs(world, sbb);
		*/
		TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
		
		BlockPos blockpos = new BlockPos(sbb.minX, sbb.minY + 1, sbb.minZ);
		System.out.println("Trying to add template at " + blockpos);
		Rotation rotation = Rotation.values()[0];
		PyramidMain.RoomTemplate rt = new PyramidMain.RoomTemplate(
				templateManager, 
				"sc2", 
				blockpos, 
				rotation);
		rt.addComponentParts(world, rand, sbb);

		BlockPos blockpos2 = new BlockPos(0, 1, 0);
		PyramidMain.RoomTemplate rt2 = new PyramidMain.RoomTemplate(
				templateManager, 
				"sc2", 
				blockpos2, 
				rotation);
		rt2.addComponentParts(world, rand, sbb);

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
        par1NBTTagCompound.setInteger("level", level);
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
        this.level = par1NBTTagCompound.getInteger("level");
 	}
    
}
