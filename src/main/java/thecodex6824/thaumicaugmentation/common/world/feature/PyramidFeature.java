package thecodex6824.thaumicaugmentation.common.world.feature;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureData;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PyramidFeature extends MapGenStructure {
	
    public PyramidFeature() {}

	@Override
	protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
		System.out.println("checking if canSpawnStructureAtCoords. chunkX = " + chunkX + "; chunkZ = " + chunkZ);
		System.out.println("World coordinates are " + (16 * chunkX + 8) + "; " + (16 * chunkZ + 8));
		System.out.println("Result: " + (chunkZ % 10 == 0 && chunkX % 10 == 0));
		return (chunkZ % 10 == 0 && chunkX % 10 == 0);
	}
    @Override
    public String getStructureName() {
        return "Ziggurath";
    }
	
	// TODO: what does it do?
    @Nullable
    @Override
    public BlockPos getNearestStructurePos(World worldIn, BlockPos pos, boolean findUnexplored) {
		System.out.println("getNearestStructurePos: " + (pos));
        this.world = worldIn;
		int averageSpacing = 1; // 20
		int unknown = 11; // or 8?
		BlockPos ans = findNearestStructurePosBySpacing(worldIn, this, pos, averageSpacing, unknown, 10387313, true, 100, findUnexplored);
		System.out.println("result: " + (ans));
        return ans;
    }

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
		
		return new PyramidFeatureStart(world, rand, chunkX, chunkZ);
	}
	
		
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public int getSpawnListIndexAt(int par1, int par2, int par3)
    {
    	int highestFoundIndex = 0;
        return highestFoundIndex;
    }

}
