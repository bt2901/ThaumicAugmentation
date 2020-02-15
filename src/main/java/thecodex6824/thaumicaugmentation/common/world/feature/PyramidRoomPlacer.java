package thecodex6824.thaumicaugmentation.common.world.feature;

import java.util.Random;
import java.util.List;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.template.TemplateManager;


import net.minecraft.nbt.NBTTagCompound;

import thaumcraft.api.blocks.BlocksTC;
import thecodex6824.thaumicaugmentation.api.TABlocks;
import thecodex6824.thaumicaugmentation.api.block.property.ITAStoneType;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;


public class PyramidRoomPlacer {

	private List<PyramidMap> mazes;
	private TemplateManager templateManager;
	private long seed;


	public PyramidRoomPlacer(List<PyramidMap> mazemap, TemplateManager templateManager, long seed) {
		this.mazes = mazemap;
		this.templateManager = templateManager;
		this.seed = seed;
	}
	
	/**
	 * Initiates construction of the Structure Component picked, at the current Location of StructGen
	 */
	public void buildRooms(List list, Random random, List<StructureBoundingBox> levelBoxes) {
		System.out.println("Building a pyramid");
        int entrance_mode = 0;
        
        for (int l=0; l < PyramidMain.levelsTall; ++l) {
			// TODO: clean up this
            int[] rooms = mazes.get(l).rcoords;
            
            // add rooms where we have our coordinates
            for (int i = 0; i < rooms.length / PyramidMap.ROOM_INFO_LEN; i++) {
                int dx = rooms[i * PyramidMap.ROOM_INFO_LEN];
                int dz = rooms[i * PyramidMap.ROOM_INFO_LEN + 1];
                int type = rooms[i * PyramidMap.ROOM_INFO_LEN + 2];
        
                // add the room as a component
				StructureComponent room = makeRoom(type, dx, dz, l, levelBoxes.get(l));
                list.add(room);
                room.buildComponent(room, list, random);
            }
        }
        // decorate rooms
        for (int l=0; l < PyramidMain.levelsTall; ++l) {
            
            int[] rooms = mazes.get(l).rcoords;
            for (int i = 0; i < rooms.length / PyramidMap.ROOM_INFO_LEN; i++) {
                int dx = rooms[i * PyramidMap.ROOM_INFO_LEN];
                int dz = rooms[i * PyramidMap.ROOM_INFO_LEN + 1];
                int type = rooms[i * PyramidMap.ROOM_INFO_LEN + 2];
                
                StructureComponent room = null;
                if (type == PyramidMap.ROOM_NO_CEILING_FANCY_ENTRANCE || type == PyramidMap.ROOM_NO_CEILING) {
                    float r = random.nextFloat();
                    if (r > 0.75) {
                        room = makeRoom(PyramidMap.ROOM_VPR, dx, dz, l, levelBoxes.get(l));
                    }
                    if (r <= 0.75) {
                        room = makeRoom(PyramidMap.ROOM_GARDEN, dx, dz, l, levelBoxes.get(l));
                    }
                }
                if (type == PyramidMap.CORIDOR_BLOCKED) {
					// TODO
				}
                if (type == PyramidMap.ENTRANCE) {
                    // if (rand.nextFloat() > 0.33) {
                    room = makeRoom(PyramidMap.ENTRANCE, dx, dz, entrance_mode, levelBoxes.get(l));
                    //}
                }
                if (room != null) {
                    list.add(room);
                    room.buildComponent(room, list, random);
                }				
            }
        }
        
	}
    protected StructureComponent makeRoom(int type, int dx, int dz, int i, StructureBoundingBox levelBox) {

        Random random = new Random((i + 1) * this.seed + dx * 153 + dz * 615);

		// TODO: without asymmetric -3 the 1.12 version places rooms wrong. Can anyone explain why?
		int lengthDoubled = (PyramidMain.evenBias + PyramidMain.oddBias);
		int worldX = levelBox.minX + dx * lengthDoubled - 3;
		int worldY = levelBox.minY;
		int worldZ = levelBox.minZ + dz * lengthDoubled - 1;
        if (type == PyramidMap.ROOMCENTRAL) {

			BlockPos blockpos = new BlockPos(worldX, worldY, worldZ);
			System.out.println("Trying to add template at " + blockpos);
			Rotation rotation = Rotation.values()[0];
			PyramidMain.RoomTemplate rt = new PyramidMain.RoomTemplate(
					templateManager, 
					"entrance", 
					// "sc2", 
					blockpos, 
					rotation);
            return rt;
        }
        if (type == PyramidMap.ROOM_VPR) {
            return new ComponentGardenRoom(random, worldX, worldY, worldZ);
            // return new ComponentVoidProductionRoom(random, worldX, worldY, worldZ);
        }
        if (type == PyramidMap.ROOM_GARDEN) {
            return new ComponentGardenRoom(random, worldX, worldY, worldZ);
        }
        if (type == PyramidMap.ENTRANCE) {
            return new ComponentPyramidEntrance(random, worldX, worldY, worldZ, i);
        }
        return new ComponentPyramidRoom(random, worldX, worldY, worldZ, type);
	}
}
