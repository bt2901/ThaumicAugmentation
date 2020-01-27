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

package thecodex6824.thaumicaugmentation.api;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

/**
 * Holds all of the sounds for Thaumic Augmentation.
 * @author TheCodex6824
 */
public final class TASounds {

    private TASounds() {}
    
    private static SoundEvent create(String sound) {
        ResourceLocation path = new ResourceLocation(ThaumicAugmentationAPI.MODID, sound);
        return new SoundEvent(path).setRegistryName(path);
    }
    
    public static final SoundEvent EMPTINESS_AMBIENCE = create("e_ambience");
    public static final SoundEvent EMPTINESS_MUSIC = create("e_music");
    public static final SoundEvent RIFT_ENERGY_ZAP = create("rift_energy_zap");
    public static final SoundEvent FOCUS_WATER_IMPACT = create("focus_water_impact");
    
    /**
     * Returns all of the sounds in the mod.
     * @return All the sounds
     */
    public static SoundEvent[] getAllSounds() {
        return new SoundEvent[] {EMPTINESS_AMBIENCE, EMPTINESS_MUSIC, RIFT_ENERGY_ZAP, FOCUS_WATER_IMPACT};
    }
    
}
