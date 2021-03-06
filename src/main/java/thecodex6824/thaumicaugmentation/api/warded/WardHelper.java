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

package thecodex6824.thaumicaugmentation.api.warded;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import thecodex6824.thaumicaugmentation.api.TAConfig;

public final class WardHelper {

    private WardHelper() {}
    
    public static boolean doesPlayerHaveSpecialPermission(EntityPlayer player) {
        if (TAConfig.opWardOverride.getValue() && FMLCommonHandler.instance().getSide() == Side.SERVER) {
            if (FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOppedPlayers().
                    getEntry(player.getGameProfile()) != null) {
                
                return true;
            }
        }
        else if (TAConfig.singlePlayerWardOverride.getValue() && FMLCommonHandler.instance().getSide() == Side.CLIENT &&
                FMLClientHandler.instance().getClient().isSingleplayer()) {
            return true;
        }
        
        return false;
    }
    
}
