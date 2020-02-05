package thecodex6824.thaumicaugmentation.common.world.feature;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;


import net.minecraft.nbt.NBTTagCompound;

import thaumcraft.api.blocks.BlocksTC;
import thecodex6824.thaumicaugmentation.api.TABlocks;
import thecodex6824.thaumicaugmentation.api.block.property.ITAStoneType;


/**
 * Based on a Twilight Forest lich tower: https://github.com/TeamTwilight/twilightforest/blob/7e1ff9b73a0d755764e0bb7b345030340fca7359/src/main/java/twilightforest/structures/StructureTFHelper.java
 */

public class PyramidMaterials {
	public static final IBlockState stoneSlab = getSlab(Blocks.STONE_SLAB);
    public static final IBlockState stoneSlabTop = getSlabTop(Blocks.STONE_SLAB);
    public static final IBlockState stoneSlabDouble = Blocks.DOUBLE_STONE_SLAB.getDefaultState();


    public static final IBlockState birchSlab = getSlab(Blocks.WOODEN_SLAB);
    public static final IBlockState birchSlabTop = getSlabTop(Blocks.WOODEN_SLAB);
    public static final IBlockState birchPlanks = Blocks.PLANKS.getDefaultState();


    private static IBlockState getSlabType(Block type, BlockSlab.EnumBlockHalf side) {
        return type.getDefaultState().withProperty(BlockSlab.HALF, side);
    }


    public static IBlockState getSlab(Block type) {
        return getSlabType(type, BlockSlab.EnumBlockHalf.BOTTOM);
    }

    public static IBlockState getSlabTop(Block type) {
        return getSlabType(type, BlockSlab.EnumBlockHalf.TOP);
    }

    public static IBlockState randomPlant(int i) {
        if(i < 4) return randomSapling(i);
        else return randomMushroom(i-4);
    }

    //TODO: Flatten
    public static IBlockState randomSapling(int i) {
        return Blocks.SAPLING.getDefaultState().withProperty(BlockSapling.TYPE, BlockPlanks.EnumType.values()[i]);
    }

    public static IBlockState randomMushroom(int i) {
        if(i == 0) return Blocks.RED_MUSHROOM.getDefaultState();
        else return Blocks.BROWN_MUSHROOM.getDefaultState();
    }

	public static IBlockState wallBlock = BlocksTC.stoneAncientTile.getDefaultState();
	public static final IBlockState wallBlockAlt = TABlocks.STONE.getDefaultState().withProperty(ITAStoneType.STONE_TYPE, ITAStoneType.StoneType.ANCIENT_BRICKS);
	
	public static IBlockState headBlock = BlocksTC.stoneAncientRock.getDefaultState();
	
	// crusted stone
	// public static Block rootBlock = ConfigBlocks.blockCosmeticSolid;
	// public static int rootBlockMeta = 14;

    public static IBlockState outerBlock = headBlock; 

	public static final IBlockState Entrance = TABlocks.STONE.getDefaultState().withProperty(ITAStoneType.STONE_TYPE, ITAStoneType.StoneType.STONE_CRUSTED_GLOWING);
	

}
