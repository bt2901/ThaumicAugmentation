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

package thecodex6824.thaumicaugmentation.core.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import thecodex6824.thaumicaugmentation.core.ThaumicAugmentationCore;

public class TransformerWardBlockRandomTick extends Transformer {

    private static final String CLASS = "net.minecraft.world.WorldServer";
    
    @Override
    public boolean isTransformationNeeded(String transformedName) {
        return !ThaumicAugmentationCore.getConfig().getBoolean("DisableWardFocus", "general", false, "") &&
                transformedName.equals(CLASS);
    }
    
    @Override
    public boolean transform(ClassNode classNode, String name, String transformedName) {
        try {
            MethodNode update = TransformUtil.findMethod(classNode, "updateBlocks", "func_147456_g");
            int ret = TransformUtil.findFirstInstanceOfMethodCall(update, 0, "randomTick", "func_180645_a", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V",
                    "net/minecraft/block/Block");
            int blockpos = TransformUtil.findLastInstanceOfMethodCall(update, ret, "<init>", "<init>", "(III)V", "net/minecraft/util/math/BlockPos");
            // this is complex enough that looping to find more places to insert calls placed by other ASM
            // would be a really bad idea
            if (ret != -1 && blockpos != -1) {
                AbstractInsnNode insertAfter = update.instructions.get(ret).getPrevious();
                AbstractInsnNode blockPosInit = update.instructions.get(blockpos);
                LabelNode label = new LabelNode();
                update.instructions.insert(insertAfter.getNext(), label);
                update.instructions.insert(blockPosInit, new VarInsnNode(Opcodes.ALOAD, 19));
                update.instructions.insert(blockPosInit, new VarInsnNode(Opcodes.ASTORE, 19));
                update.instructions.insert(insertAfter, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/world/WorldServer",
                        TransformUtil.correctNameForRuntime("rand", "field_73012_v"), "Ljava/util/Random;"));
                update.instructions.insert(insertAfter, new VarInsnNode(Opcodes.ALOAD, 0));
                update.instructions.insert(insertAfter, new VarInsnNode(Opcodes.ALOAD, 17));
                update.instructions.insert(insertAfter, new VarInsnNode(Opcodes.ALOAD, 19));
                update.instructions.insert(insertAfter, new VarInsnNode(Opcodes.ALOAD, 0));
                update.instructions.insert(insertAfter, new VarInsnNode(Opcodes.ALOAD, 18));
                update.instructions.insert(insertAfter, new JumpInsnNode(Opcodes.IFEQ, label));
                update.instructions.insert(insertAfter, new VarInsnNode(Opcodes.ILOAD, 20));
                update.instructions.insert(insertAfter, new InsnNode(Opcodes.POP));
                update.instructions.insert(insertAfter, new VarInsnNode(Opcodes.ISTORE, 20));
                update.instructions.insert(insertAfter, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "thecodex6824/thaumicaugmentation/common/internal/TAHooks",
                        "checkWardRandomTick",
                        "(Lnet/minecraft/world/WorldServer;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)Z",
                        false
                ));
                return true;
            }
            else
                throw new TransformerException("Could not locate required instructions, locations: " + ret + ", " + blockpos);
        }
        catch (Throwable anything) {
            error = new RuntimeException(anything);
            return false;
        }
    }
    
}
