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

package thecodex6824.thaumicaugmentation.common.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.golems.seals.SealHandler;
import thecodex6824.thaumicaugmentation.common.item.prefab.ItemTABase;

public class ItemSealCopier extends ItemTABase implements ISealDisplayer {

    public ItemSealCopier() {
        super();
        setMaxStackSize(1);
        addPropertyOverride(new ResourceLocation("holding"), new IItemPropertyGetter() {
            @Override
            public float apply(ItemStack stack, World world, EntityLivingBase entity) {
                if (stack.hasTagCompound() && stack.getTagCompound().hasKey("seal", NBT.TAG_COMPOUND))
                    return 1;
                else
                    return 0;
            }
        });
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing Side, float hitX,
            float hitY, float hitZ, EnumHand hand) {

        if (!world.isRemote) {
            if (!player.isSneaking()) {
                ItemStack held = player.getHeldItem(hand);
                RayTraceResult result = player.rayTrace(10.0F, 1.0F);
                // seals don't seem to actually exist in a physical state, so raytracing and events don't work (directly)
                ISealEntity seal = SealHandler.getSealEntity(world.provider.getDimension(), new SealPos(result.getBlockPos(), result.sideHit));
                if (seal != null) {
                    if (held.hasTagCompound() && held.getTagCompound().hasKey("seal", NBT.TAG_COMPOUND)) {
                        if (held.getTagCompound().getString("sealType").equals(seal.getSeal().getKey()) && (!seal.isLocked() || 
                                seal.getOwner().isEmpty() || seal.getOwner().equals(player.getUniqueID().toString()))) {

                            SealPos oldPos = seal.getSealPos();
                            seal.readNBT(held.getTagCompound().getCompoundTag("seal"));
                            seal.getSealPos().face = oldPos.face;
                            seal.getSealPos().pos = oldPos.pos;
                            return EnumActionResult.SUCCESS;
                        }
                    }
                    else if (!seal.isLocked() || seal.getOwner().isEmpty() || seal.getOwner().equals(player.getUniqueID().toString())) {
                        NBTTagCompound newCompound = new NBTTagCompound();
                        newCompound.setTag("seal", seal.writeNBT());
                        // so we don't have to depend on Thaumcraft's internals for the type check
                        newCompound.setString("sealType", seal.getSeal().getKey());
                        held.setTagCompound(newCompound);
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
            else {
                player.getHeldItem(hand).setTagCompound(null);
                return EnumActionResult.SUCCESS;
            }
        }

        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (player.isSneaking()) {
            player.getHeldItem(hand).setTagCompound(null);
            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }

        return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("sealType", NBT.TAG_STRING)) {
            tooltip.add(new TextComponentTranslation("thaumicaugmentation.text.stored_seal", 
                    new TextComponentTranslation(ItemsTC.seals.getTranslationKey(GolemHelper.getSealStack(
                    stack.getTagCompound().getString("sealType"))) + ".name").getFormattedText()).getFormattedText());
        }
    }

}
