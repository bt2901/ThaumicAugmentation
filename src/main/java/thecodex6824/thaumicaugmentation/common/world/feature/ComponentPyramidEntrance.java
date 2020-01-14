package thecodex6824.thaumicaugmentation.common.world.feature;

import java.util.List;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;


import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

import net.minecraft.nbt.NBTTagCompound;

import thaumcraft.api.blocks.BlocksTC;
import thecodex6824.thaumicaugmentation.api.TABlocks;


public class ComponentPyramidEntrance extends ComponentPyramidRoom {
    int level;

	public ComponentPyramidEntrance() {
		super();
	}

	public ComponentPyramidEntrance(Random rand, int x, int y, int z, int mode) {
		super(rand, x, y, z, PyramidMap.ENTRANCE);
        this.level = 0;
        this.setCoordBaseMode(EnumFacing.HORIZONTALS[mode]);
	}
    
    public void createDoorway(World world, StructureBoundingBox sbb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IBlockState what) {
        fillWithBlocks(world, sbb, minX, minY, minZ, maxX, maxY, maxZ, what, what, false);
        int cX = (minX + maxX)/2;
        int cZ = (minZ + maxZ)/2;

        setBlockState(world, Blocks.AIR.getDefaultState(), cX, minY, cZ, sbb);
        setBlockState(world, Blocks.AIR.getDefaultState(), cX, minY + 1, cZ, sbb);
    }

	/**
	 * Initiates construction of the Structure Component picked, at the current Location of StructGen
	 */
	@Override
	public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
	}
    
    public boolean replaceIfNotPartOfStructure(World world, StructureBoundingBox sbb, int x, int y, int z) {
		IBlockState b = getBlockStateFromPos(world, x, y, z, sbb);
		
        if (b == PyramidMaterials.wallBlock || b == PyramidMaterials.headBlock || b == PyramidMaterials.outerBlock) {
            return false;
        }
        setBlockState(world, Blocks.AIR.getDefaultState(), x, y, z, sbb);
        return true;
    }


	@Override
	public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        int z0 = 3*roomDepth + 1;
        fillWithAir(world, sbb, 1, 1, z0, roomWidth - 1, roomHeight - 1, z0 + roomDepth - 2);
        int pace = PyramidMain.evenBias + PyramidMain.oddBias;
        int z = z0 - roomDepth;
        fillWithBlocks(world, sbb, pace + 1,     1, z, 2 * pace - 1, roomHeight - 1, z0,
            Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);

        for (int z2 = z; z2 < z + roomDepth; ++z2) {
            for (int ylevel = 1; ylevel < PyramidMain.height; ++ylevel) {
                replaceIfNotPartOfStructure(world, sbb, pace, ylevel, z2);
                replaceIfNotPartOfStructure(world, sbb, pace - 1, ylevel, z2);
                replaceIfNotPartOfStructure(world, sbb, 2*pace, ylevel, z2);
                replaceIfNotPartOfStructure(world, sbb, 2*pace + 1, ylevel, z2);
            }
        }
		// BlocksTC.stoneAncientTile
		// BlocksTC.slabAncient
        setBlockState(world, BlocksTC.stoneAncientTile.getDefaultState(), pace, 1, z + 5, sbb);
        setBlockState(world, BlocksTC.stoneAncientTile.getDefaultState(), 2*pace, 1, z + 5, sbb);
        fillWithBlocks(world, sbb, pace - 1, 0, z + 5, 2 * pace + 1, 0, z+4,
            BlocksTC.stoneAncientTile.getDefaultState(), BlocksTC.stoneAncientTile.getDefaultState(), false);
            
        for (int ylevel = 1; ylevel <= PyramidMain.height; ++ylevel) {
            z -= 4;
			
            fillWithBlocks(world, sbb, pace - 1, 0, z, 2 * pace + 1, ylevel, z + 3, 
                TABlocks.STONE.getDefaultState(), TABlocks.STONE.getDefaultState(), false);
            fillWithWalls(world, sbb, pace - 1, ylevel, z, 2 * pace + 1, roomHeight, z + 3);
            fillWithBlocks(world, sbb, pace - 1, ylevel, z + 2, 2 * pace + 1, ylevel + 1, z + 3, 
                BlocksTC.slabAncient.getDefaultState(), BlocksTC.slabAncient.getDefaultState(), false);
            fillWithAir(world, sbb, pace - 1, ylevel + 1, z, 2 * pace + 1, roomHeight, z + 4);
        }
        fillWithAir(world, sbb, pace - 1, PyramidMain.height + 1, z, 2 * pace + 1, PyramidMain.height + 2, z + 8);
		return true;
	}
	/**
	 * Save to NBT
	@Override
	protected void func_143012_a(NBTTagCompound par1NBTTagCompound) {
		super.func_143012_a(par1NBTTagCompound);
	}

    // Load from NBT
	@Override
	protected void func_143011_b(NBTTagCompound par1NBTTagCompound) {
        super.func_143011_b(par1NBTTagCompound);
 	}
	 */
    
}
