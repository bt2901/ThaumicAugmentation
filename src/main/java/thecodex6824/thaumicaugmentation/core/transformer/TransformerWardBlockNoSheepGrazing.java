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
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import thecodex6824.thaumicaugmentation.core.ThaumicAugmentationCore;

public class TransformerWardBlockNoSheepGrazing extends Transformer {

    private static final String CLASS = "net.minecraft.entity.ai.EntityAIEatGrass";
    
    @Override
    public boolean isTransformationNeeded(String transformedName) {
        return !ThaumicAugmentationCore.getConfig().getBoolean("DisableWardFocus", "general", false, "") &&
                transformedName.equals(CLASS);
    }
    
    @Override
    public boolean transform(ClassNode classNode, String name, String transformedName) {
        try {
            MethodNode nom = TransformUtil.findMethod(classNode, "shouldExecute", "func_75250_a");
            int tallGrass = TransformUtil.findLastInstanceOfOpcode(nom, nom.instructions.size(), Opcodes.IFEQ);
            int normalGrass = TransformUtil.findLastInstanceOfOpcode(nom, nom.instructions.size(), Opcodes.IF_ACMPNE);
            if (tallGrass != -1 && normalGrass != -1) {
                AbstractInsnNode insertAfter = nom.instructions.get(tallGrass);
                AbstractInsnNode grassAfter = nom.instructions.get(normalGrass);
                nom.instructions.insert(insertAfter, new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) insertAfter).label));
                nom.instructions.insert(insertAfter, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "thecodex6824/thaumicaugmentation/common/internal/TAHooks",
                        "checkWardGeneric",
                        "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z",
                        false
                ));
                nom.instructions.insert(insertAfter, new VarInsnNode(Opcodes.ALOAD, 1));
                nom.instructions.insert(insertAfter, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/ai/EntityAIEatGrass",
                        TransformUtil.correctNameForRuntime("entityWorld", "field_151501_c"), "Lnet/minecraft/world/World;"));
                nom.instructions.insert(insertAfter, new VarInsnNode(Opcodes.ALOAD, 0));
                
                nom.instructions.insert(grassAfter, new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) grassAfter).label));
                nom.instructions.insert(grassAfter, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        "thecodex6824/thaumicaugmentation/common/internal/TAHooks",
                        "checkWardGeneric",
                        "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Z",
                        false
                ));
                nom.instructions.insert(grassAfter, new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
                        "net/minecraft/util/math/BlockPos",
                        TransformUtil.correctNameForRuntime("down", "func_177977_b"),
                        "()Lnet/minecraft/util/math/BlockPos;",
                        false
                ));
                nom.instructions.insert(grassAfter, new VarInsnNode(Opcodes.ALOAD, 1));
                nom.instructions.insert(grassAfter, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/entity/ai/EntityAIEatGrass",
                        TransformUtil.correctNameForRuntime("entityWorld", "field_151501_c"), "Lnet/minecraft/world/World;"));
                nom.instructions.insert(grassAfter, new VarInsnNode(Opcodes.ALOAD, 0));
            }
            else
                throw new TransformerException("Could not locate required instructions, locations: " + tallGrass + ", " + normalGrass);
            
            return true;
        }
        catch (Throwable anything) {
            error = new RuntimeException(anything);
            return false;
        }
    }
    
}
