/**
 *  Thaumic Augmentation
 *  Copyright (c) 2019 TheCodex6824.
 *
 *  This file is part of Thaumic Augmentation.
 *
 *  Thaumic Augmentation is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Thaumic Augmentation is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Thaumic Augmentation.  If not, see <https://www.gnu.org/licenses/>.
 */

package thecodex6824.thaumicaugmentation.init;

import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ScanBlock;
import thaumcraft.api.research.ScanBlockState;
import thaumcraft.api.research.ScanEntity;
import thaumcraft.api.research.ScanItem;
import thaumcraft.api.research.ScanningManager;
import thecodex6824.thaumicaugmentation.api.TABlocks;
import thecodex6824.thaumicaugmentation.api.TAConfig;
import thecodex6824.thaumicaugmentation.api.ThaumicAugmentationAPI;
import thecodex6824.thaumicaugmentation.api.block.property.ITAStoneType;
import thecodex6824.thaumicaugmentation.api.block.property.ITAStoneType.StoneType;
import thecodex6824.thaumicaugmentation.common.entity.EntityDimensionalFracture;
import thecodex6824.thaumicaugmentation.common.research.ScanEntityWithPeacefulFallback;
import thecodex6824.thaumicaugmentation.common.research.ScanTool;

public final class ResearchHandler {

    private ResearchHandler() {}
    
    public static void init() {
        ResearchCategories.registerCategory("THAUMIC_AUGMENTATION", "FIRSTSTEPS", new AspectList(),
                new ResourceLocation(ThaumicAugmentationAPI.MODID, "textures/gui/base_research_icon.png"),
                new ResourceLocation(ThaumicAugmentationAPI.MODID, "textures/gui/research_background.jpg"), new ResourceLocation("thaumcraft", "textures/gui/gui_research_back_over.png")
        );
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicAugmentationAPI.MODID, "research/misc.json"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicAugmentationAPI.MODID, "research/foci.json"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicAugmentationAPI.MODID, "research/gauntlets.json"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicAugmentationAPI.MODID, "research/warded.json"));
        ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicAugmentationAPI.MODID, "research/void.json"));
        if (!TAConfig.disableWardFocus.getValue()) {
            if (ThaumicAugmentationAPI.isCoremodAvailable())
                ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicAugmentationAPI.MODID, "research/ward_foci_coremod.json"));
            else
                ThaumcraftApi.registerResearchLocation(new ResourceLocation(ThaumicAugmentationAPI.MODID, "research/ward_foci_no_coremod.json"));
        }
        
        ScanningManager.addScannableThing(new ScanBlock("f_LEAFSILVERWOOD", new Block[] {BlocksTC.leafSilverwood}));
        ScanningManager.addScannableThing(new ScanItem("f_LEAFSILVERWOOD", new ItemStack(BlocksTC.leafSilverwood)));
        
        ScanningManager.addScannableThing(new ScanEntity("m_DIMENSIONALFRACTURE", EntityDimensionalFracture.class, false));
        ScanningManager.addScannableThing(new ScanBlockState("!VOIDSTONE", TABlocks.STONE.getDefaultState().withProperty(
                ITAStoneType.STONE_TYPE, StoneType.STONE_VOID)));
        ScanningManager.addScannableThing(new ScanBlockState("!VOIDSTONETAINTED", TABlocks.STONE.getDefaultState().withProperty(
                ITAStoneType.STONE_TYPE, StoneType.STONE_TAINT_NODECAY)));
        ScanningManager.addScannableThing(new ScanBlockState("!VOIDSTONETAINTEDSOIL", TABlocks.STONE.getDefaultState().withProperty(
                ITAStoneType.STONE_TYPE, StoneType.SOIL_STONE_TAINT_NODECAY)));
        ScanningManager.addScannableThing(new ScanItem("!VOIDSTONE", new ItemStack(TABlocks.STONE)));
        ScanningManager.addScannableThing(new ScanItem("!VOIDSTONETAINTED", new ItemStack(TABlocks.STONE, 1, StoneType.STONE_TAINT_NODECAY.getMeta())));
        ScanningManager.addScannableThing(new ScanItem("!VOIDSTONETAINTEDSOIL", new ItemStack(TABlocks.STONE, 1, StoneType.SOIL_STONE_TAINT_NODECAY.getMeta())));
        
        ScanningManager.addScannableThing(new ScanTool("f_STRONGPICKAXE", "pickaxe", 3));
        ScanningManager.addScannableThing(new ScanItem("f_FLINTANDSTEEL", new ItemStack(Items.FLINT_AND_STEEL)));
        ScanningManager.addScannableThing(new ScanEntityWithPeacefulFallback("m_CREEPER", new ScanEntity("m_CREEPER", EntityCreeper.class, true),
                new ScanItem("m_CREEPER", new ItemStack(Items.GUNPOWDER))));
    }

}
