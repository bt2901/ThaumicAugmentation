package thecodex6824.thaumicaugmentation.common.world.feature;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureStrongholdPieces;

import thecodex6824.thaumicaugmentation.common.world.WorldProviderEmptiness;


public class PyramidFeatureStart extends StructureStart {
			
    static
    {
    	MapGenStructureIO.registerStructure(PyramidFeatureStart.class, "Ziggurath");
        MapGenStructureIO.registerStructureComponent(ComponentPyramidRoom.class, "ZRoom");
        // MapGenStructureIO.registerStructureComponent(ComponentPyramidCentralRoom.class, "ZRoomCentral");
        MapGenStructureIO.registerStructureComponent(PyramidMain.class, "ZMain");
        MapGenStructureIO.registerStructureComponent(ComponentPyramidEntrance.class, "ZEnter");
        MapGenStructureIO.registerStructureComponent(PyramidLevel.class, "ZLevel");
        // MapGenStructureIO.registerStructureComponent(ComponentPyramidStairs.class, "ZStairs");
        // MapGenStructureIO.registerStructureComponent(ComponentVoidProductionRoom.class, "ZVPR");
        MapGenStructureIO.registerStructureComponent(ComponentGardenRoom.class, "ZGarden");
        // MapGenStructureIO.registerStructureComponent(ComponentPyramidTrap.class, "ZRoomTrap");
        // MapGenStructureIO.registerStructureComponent(ComponentCoridorTrap.class, "ZCoridorTrap");
    }
    
    public PyramidFeatureStart() {}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PyramidFeatureStart(World world, Random rand, int chunkX, int chunkZ)  {
		int x = (chunkX * 16) + 8;
		int z = (chunkZ * 16) + 8;
		// int yBase = WorldProviderEmptiness().getAverageGroundLevel();
		int yBase = 56;
		int y = yBase - 3*PyramidMain.height/2; //1.5 stores under the ground level

		StructureComponent firstComponent = new PyramidMain(world, rand, x, y, z);
        components.add(firstComponent);
        firstComponent.buildComponent(firstComponent, components, rand);

		updateBoundingBox();
	}
	    
    /**
     * Check if the component is within the chunk bounding box, but check as if it was one larger
     */
	@SuppressWarnings("unused")
	private boolean isIntersectingLarger(StructureBoundingBox chunkBB, StructureComponent component) {
		StructureBoundingBox compBB = component.getBoundingBox();
		
		// don't bother checking Y
        return (compBB.maxX + 1) >= chunkBB.minX && (compBB.minX - 1) <= chunkBB.maxX && (compBB.maxZ + 1) >= chunkBB.minZ && (compBB.minZ - 1) <= chunkBB.maxZ;

	}

    /*
	public void func_143022_a(NBTTagCompound par1NBTTagCompound) {
        super.func_143022_a(par1NBTTagCompound);
        //par1NBTTagCompound.setBoolean("Conquered", this.isConquered);
    }

    public void func_143017_b(NBTTagCompound nbttagcompound) {
        super.func_143017_b(nbttagcompound);
        // this.isConquered = nbttagcompound.getBoolean("Conquered");
    }
    protected void setupComponents(World world) {
        TemplateManager templateManager = world.getSaveHandler().getStructureTemplateManager();
        MinecraftServer server = world.getMinecraftServer();

        for (StructureComponent component : components)
            if (component instanceof StructureTFComponentTemplate)
                ((StructureTFComponentTemplate) component).setup(templateManager, server);
    }*/

 
}
