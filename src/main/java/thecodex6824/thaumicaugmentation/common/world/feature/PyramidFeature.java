package thecodex6824.thaumicaugmentation.common.world.feature;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import net.minecraft.util.math.ChunkPos;

import java.util.Random;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import thecodex6824.thaumicaugmentation.common.world.WorldProviderEmptiness;


public class PyramidFeature extends MapGenStructure {
	
    public PyramidFeature() {}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
		return (chunkZ % 32 == 0 && chunkX % 32 == 0);
	}
    @Override
    public String getStructureName() {
        return "Ziggurath";
    }
	
	// TODO: what does it do?
    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
        this.world = worldIn;
		int averageSpacing = 20;
		int unknown = 11; // or 8?
		BlockPos ans = findNearestStructurePosBySpacing(worldIn, this, pos, averageSpacing, unknown, 10387313, true, 100, findUnexplored);
        return ans;
    }
	/*
	@Override
	public synchronized boolean generateStructure(World world, Random rand, ChunkPos chunkpos) {
		System.out.println("generateStructure at chunk " + chunkpos);
        ObjectIterator objectiterator = this.structureMap.values().iterator();
		
		StructureStart structurestart = (StructureStart)objectiterator.next();
        int i = (chunkpos.x << 4) + 8;
        int j = (chunkpos.z << 4) + 8;

		boolean flag = (structurestart.isSizeableStructure() && structurestart.isValidForPostProcess(chunkpos) && structurestart.getBoundingBox().intersectsWith(i, j, i + 15, j + 15));
		System.out.println("check is " + flag);

		return super.generateStructure(world, rand, chunkpos);
	}
	*/


	@Override
	protected StructureStart getStructureStart(int chunkX, int chunkZ) {
		// fix rand
        this.rand.setSeed(world.getSeed());
        long rand1 = this.rand.nextLong();
        long rand2 = this.rand.nextLong();
        long chunkXr1 = (long)(chunkX) * rand1;
        long chunkZr2 = (long)(chunkZ) * rand2;
        this.rand.setSeed(chunkXr1 ^ chunkZr2 ^ world.getSeed());
        this.rand.nextInt();
		
		return new PyramidFeature.Start(world, rand, chunkX, chunkZ);
	}
	
		
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public int getSpawnListIndexAt(int par1, int par2, int par3)
    {
    	int highestFoundIndex = 0;
        return highestFoundIndex;
    }
    public static class Start extends StructureStart {
			
		static {
			MapGenStructureIO.registerStructure(PyramidFeature.Start.class, "ZiggurathStart");
			MapGenStructureIO.registerStructureComponent(ComponentPyramidRoom.class, "ZRoom");
			MapGenStructureIO.registerStructureComponent(ComponentPyramidCentralRoom.class, "ZRoomCentral");
			MapGenStructureIO.registerStructureComponent(PyramidMain.class, "ZMain");
			MapGenStructureIO.registerStructureComponent(ComponentPyramidEntrance.class, "ZEnter");
			MapGenStructureIO.registerStructureComponent(PyramidLevel.class, "ZLevel");
			// MapGenStructureIO.registerStructureComponent(ComponentPyramidStairs.class, "ZStairs");
			// MapGenStructureIO.registerStructureComponent(ComponentVoidProductionRoom.class, "ZVPR");
			MapGenStructureIO.registerStructureComponent(ComponentGardenRoom.class, "ZGarden");
			// MapGenStructureIO.registerStructureComponent(ComponentPyramidTrap.class, "ZRoomTrap");
			// MapGenStructureIO.registerStructureComponent(ComponentCoridorTrap.class, "ZCoridorTrap");
		}
		
		public Start() {}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Start(World world, Random rand, int chunkX, int chunkZ)  {
			System.out.println("Creating a StructureStart at [" +  chunkX + ", " + chunkZ + "]");
			int x = (chunkX * 16) + 8;
			int z = (chunkZ * 16) + 8;
			// int yBase = WorldProviderEmptiness().getAverageGroundLevel();
			int yBase = 56;
			int y = yBase - 3*PyramidMain.height/2; //1.5 stores under the ground level

			PyramidMain firstComponent = new PyramidMain(world, rand, x, y, z);
			// this.components.addAll(list);
			components.add((StructureComponent)firstComponent);
			firstComponent.buildComponent(firstComponent, components, rand);
			
			List<PyramidMap> mazemap = firstComponent.getMazeMap();
			
			// add grid
			// add rooms
			// add templates

			updateBoundingBox();
		}		
	}
}
