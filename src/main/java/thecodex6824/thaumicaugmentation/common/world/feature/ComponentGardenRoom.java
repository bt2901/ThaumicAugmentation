package thecodex6824.thaumicaugmentation.common.world.feature;

import net.minecraft.block.BlockMushroom;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSlab;
import java.util.List;
import java.util.Random;

import net.minecraft.block.state.IBlockState;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;


import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import net.minecraft.nbt.NBTTagCompound;

import thaumcraft.api.blocks.BlocksTC;

// based on lichtower / ComponentTFTowerWing
public class ComponentGardenRoom extends ComponentPyramidRoom {

    public ComponentGardenRoom() {
        super();
    }

    public ComponentGardenRoom(Random rand, int x, int y, int z) {
        super(rand, x, y, z, PyramidMap.ROOM_NO_CEILING_FANCY_ENTRANCE);
        this.roomHeight = PyramidMain.height;
    }


    /**
     * Initiates construction of the Structure Component picked, at the current Location of StructGen
     */
    @Override
    public void buildComponent(StructureComponent structurecomponent, List list, Random random) {
            ;
    }
	/**
	 * Makes a planter.  Depending on the situation, it can be filled with trees, flowers, or crops
	 */
	protected void decoratePlanter(World world, Random rand, StructureBoundingBox sbb, int cx, int cz, boolean isBig) {

		surroundBlockCardinal(world, PyramidMaterials.stoneSlab, cx, 1, cz, sbb);

		if (isBig) {
			surroundBlockCorners(world, PyramidMaterials.stoneSlabDouble, cx, 1, cz, sbb);
		}

		// place a cute planted thing
		setBlockState(world, Blocks.GRASS.getDefaultState(), cx, 1, cz, sbb);

		int i = rand.nextInt(6);
		boolean isTree = i > 4;
		final IBlockState plant = isTree ? PyramidMaterials.randomSapling(i) : PyramidMaterials.randomMushroom(i);


		setBlockState(world, plant, cx, 2, cz, sbb);
		final BlockPos pos = getBlockPosWithOffset(cx, 2, cz);

		if(isTree) //grow tree
			((BlockSapling) Blocks.SAPLING).grow(world, pos, plant, world.rand);
		else //grow sapling
			plant.getBlock().updateTick(world, pos, plant, world.rand);


		// otherwise, place the block into a flowerpot
		IBlockState whatHappened = this.getBlockStateFromPos(world, cx, 2, cz, sbb);
		if (whatHappened.getBlock() == plant.getBlock() || whatHappened.getBlock() == Blocks.AIR)
			setBlockState(world, Blocks.FLOWER_POT.getDefaultState(), cx, 2, cz, sbb);
	}

    public void fillPillars(World world, StructureBoundingBox sbb, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, IBlockState what) {

        fillWithBlocks(world, sbb, minX, minY, minZ, minX, maxY, minZ, what, what, false);
        fillWithBlocks(world, sbb, maxX, minY, minZ, maxX, maxY, minZ, what, what, false);
        fillWithBlocks(world, sbb, minX, minY, maxZ, minX, maxY, maxZ, what, what, false);
        fillWithBlocks(world, sbb, maxX, minY, maxZ, maxX, maxY, maxZ, what, what, false);
    }
    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox sbb) {
        IBlockState brick = BlocksTC.stoneAncientTile.getDefaultState();
        int pace = PyramidMain.oddBias + PyramidMain.evenBias;
        int platf = 2;
		IBlockState glassBlock = Blocks.GLASS.getDefaultState();

		fillWithAir(world, sbb, 1, 1, 1, roomWidth - 1, roomHeight - 1, roomDepth - 1);
        fillWithBlocks(world, sbb, pace, 1, pace, 2*pace, platf-1, 2*pace, brick, brick, false);
        int gardenType = rand.nextInt(4);
        switch (gardenType) {
        case 0:
            int cx = roomWidth/2;
            int cz = roomDepth/2;
            decoratePlanter(world, rand, sbb, cx, cz, true); 
            return true;
        case 1:
            fillWithBlocks(world, sbb, pace+1, 1, pace+1, 2*pace-1, platf-1, 2*pace-1, 
				Blocks.SAND.getDefaultState(), Blocks.SAND.getDefaultState(), false);
            fillWithBlocks(world, sbb, pace+1, platf, pace+1, 2*pace-1, platf, 2*pace-1, 
				BlocksTC.cinderpearl.getDefaultState(), BlocksTC.cinderpearl.getDefaultState(), false);
            return true;
        case 2:
            fillWithBlocks(world, sbb, pace+1, 1, pace+1, 2*pace-1, platf-1, 2*pace-1, 
				Blocks.SOUL_SAND.getDefaultState(), Blocks.SOUL_SAND.getDefaultState(), false);
            
            fillPillars(world, sbb, pace, 1, pace, 2*pace, platf, 2*pace, brick);
            fillWithBlocks(world, sbb, pace, 1, pace, 2*pace, platf, 2*pace, glassBlock, Blocks.NETHER_WART.getDefaultState(), false);
            
            return true;
        case 3:
            fillWithBlocks(world, sbb, pace, 1, pace, 2*pace, platf, 2*pace, 
				glassBlock, glassBlock, false);
            fillPillars(world, sbb, pace, 1, pace, 2*pace, platf, 2*pace, brick);
            fillWithBlocks(world, sbb, pace+1, 1, pace+1, 2*pace-1, platf, 2*pace-1, 
				Blocks.WATER.getDefaultState(), Blocks.WATER.getDefaultState(), false);
            return true;
        }        
        return true;
    }
}
