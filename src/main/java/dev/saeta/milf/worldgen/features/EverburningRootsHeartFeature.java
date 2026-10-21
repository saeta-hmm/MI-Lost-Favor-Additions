package dev.saeta.milf.worldgen.features;

import com.mojang.serialization.Codec;
import dev.saeta.milf.blocks.everburning.EverburningRootsBlock;
import dev.saeta.milf.registries.MILFBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

public class EverburningRootsHeartFeature extends Feature<NoneFeatureConfiguration> {

    private static  final int OUTER_RADIUS = 9;


    public EverburningRootsHeartFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {

        WorldGenLevel level = context.level();

        BlockPos origin = context.origin();

        int originX = origin.getX();

        int originZ = origin.getZ();

        int minBuildHeight = level.getMinBuildHeight();;
        BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, new BlockPos(originX, minBuildHeight, originZ));
        int bedrockHeight = minBuildHeight + 14;

        BlockPos bedrockPos = new BlockPos(originX, bedrockHeight, originZ);
        RandomSource randomSource = context.random();

        boolean canPlaceHeart = tryPlaceHeart(level, bedrockPos, randomSource);

        if(canPlaceHeart){
            tryPlaceSurfacePart(level, surfacePos, bedrockPos.above(7), randomSource);
            return true;
        }

        return false;
    }

    private boolean tryPlaceSurfacePart(WorldGenLevel level, BlockPos surfacePos, BlockPos bedrockPos, RandomSource randomSource){

        int maxOffset = OUTER_RADIUS + 1;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int height = surfacePos.getY() - bedrockPos.getY() + 22;

        float swirlinessRandom = 0.5f - randomSource.nextFloat();
        float swirlRandom = 0.05f - randomSource.nextFloat() / 10;

        int radiusRAndom = randomSource.nextInt(-1,1);

        for (int dx = -maxOffset; dx <= maxOffset; dx++) {
            for (int dz = -maxOffset; dz <= maxOffset; dz++) {
                for (int dy = 0; dy <= height; dy++) {

                    float centerFloat = getSpiralFloat(dx,dy,dz, height, OUTER_RADIUS - 4.5f + radiusRAndom, 5.109f + swirlinessRandom, 0.1f + swirlRandom);
                    pos.set(bedrockPos.getX() + dx, bedrockPos.getY() + dy, bedrockPos.getZ() + dz);

                    if(centerFloat<=0) {
                        if(randomSource.nextFloat() < 0.0109){
                            tryPlaceVine(level,pos);
                        }
                        continue;
                    };


                    BlockState stateToPlace;

                    if(centerFloat <= 0.5 || randomSource.nextFloat() <= 0.3109){
                        boolean isWater = level.getFluidState(pos).getType() == Fluids.WATER;
                        stateToPlace = MILFBlocks.EVERBURNING_ROOTS_VINE_BLOCK.get().defaultBlockState().setValue(EverburningRootsBlock.WATERLOGGED, isWater);
                    } else if (centerFloat <= 0.8){
                        boolean isWater = level.getFluidState(pos).getType() == Fluids.WATER;
                        stateToPlace = MILFBlocks.EVERBURNING_ROOTS.get().defaultBlockState().setValue(EverburningRootsBlock.WATERLOGGED, isWater);
                    } else {
                        stateToPlace = MILFBlocks.EVERBURNING_ROOTS_VEINS.get().defaultBlockState();
                    }

                    safeSetBlock(level, pos.immutable(), stateToPlace, (this::checkStemState));


                }
            }
        }

        return true;
    }



    private boolean tryPlaceHeart(WorldGenLevel level, BlockPos bedrockPos, RandomSource randomSource){

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int maxOffset = OUTER_RADIUS + 1;

        for (int dx = -maxOffset; dx <= maxOffset; dx++) {
            for (int dz = -maxOffset; dz <= maxOffset; dz++) {
                for (int dy = -OUTER_RADIUS; dy <= OUTER_RADIUS; dy++) {

                    pos.set(bedrockPos.getX() + dx, bedrockPos.getY() + dy, bedrockPos.getZ() + dz);

                    if(!checkHeartState(level.getBlockState(pos))) {
                        return false;
                    }
                }
            }
        }

        double outerRadiusSquare = OUTER_RADIUS * OUTER_RADIUS;

        float innerMultiplier = 1 + randomSource.nextFloat() / 3;

        for (int dx = -maxOffset; dx <= maxOffset; dx++) {
            for (int dz = -maxOffset; dz <= maxOffset; dz++) {

                double atan2 = Math.atan2(dz, dx);

                for (int dy = -OUTER_RADIUS; dy <= OUTER_RADIUS; dy++) {


                    float outerSphere = (float) (dx*dx + dy*dy + dz*dz);
                    float outerBounds = (float) (outerRadiusSquare + 32 * Math.cos(1.2* dy - atan2));

                    float middleSphere = (float) (12*dx*dx + 2*dy*dy + 12*dz*dz);
                    float middleBounds = (float) (outerRadiusSquare + 32 * Math.cos(0.7* dy - atan2));

                    float innerSphere = (float) (34*dx*dx*innerMultiplier + 14*dy*dy*innerMultiplier + 34*dz*dz*innerMultiplier);
                    float innerBounds = (float) (outerRadiusSquare + 35 * Math.sin(1.6* dy - atan2));


                    BlockState stateToPlace = null;

                    if (innerSphere <= innerBounds - 38 * innerMultiplier) {
                        stateToPlace = MILFBlocks.EVERBURNING_ROOTS_HEART.get().defaultBlockState();
                    }
                    else if (outerRadiusSquare - 25 <= middleSphere && middleSphere <= middleBounds) {
                        stateToPlace = MILFBlocks.EVERBURNING_ROOTS_VEINS.get().defaultBlockState();
                    }
                    else if (outerRadiusSquare - 25 <= outerSphere && outerSphere <= outerBounds) {
                        stateToPlace = MILFBlocks.EVERBURNING_ROOTS.get().defaultBlockState();
                    }
                    else if (middleSphere <= middleBounds * 2.2) {
                        stateToPlace = MILFBlocks.EVERBURNING_ROOTS_VINE_BLOCK.get().defaultBlockState();
                    }
                    else if (outerRadiusSquare - 32 <= outerSphere && outerSphere <= outerBounds) {
                        stateToPlace = MILFBlocks.EVERBURNING_ROOTS_VINE_BLOCK.get().defaultBlockState();
                    }
                    else if (outerSphere <= outerBounds) {
                        stateToPlace = Blocks.AIR.defaultBlockState();
                    }


                    if(stateToPlace != null){
                        pos.set(bedrockPos.getX() + dx, bedrockPos.getY() + dy, bedrockPos.getZ() + dz);

                        safeSetBlock(level, pos.immutable(), stateToPlace, state -> true);
                    }


                }
            }
        }

        for (int dx = -maxOffset; dx <= maxOffset; dx++) {
            for (int dz = -maxOffset; dz <= maxOffset; dz++) {
                for (int dy = -OUTER_RADIUS; dy <= OUTER_RADIUS; dy++) {

                    if(randomSource.nextInt(20) == 17){
                        pos.set(bedrockPos.getX() + dx, bedrockPos.getY() + dy, bedrockPos.getZ() + dz);
                        tryPlaceVine(level, pos);
                    }



                }
            }
        }

        return true;

    }

    private void tryPlaceVine(WorldGenLevel level,BlockPos.MutableBlockPos pos) {
        BlockState vine = MILFBlocks.EVERBURNING_ROOTS_VINE.get().defaultBlockState();

        boolean canPlace = false;

        for(Direction direction : Direction.values()){
            if(MultifaceBlock.canAttachTo(level, direction, pos.relative(direction), level.getBlockState(pos.relative(direction)))) {
                vine = vine.setValue(MultifaceBlock.getFaceProperty(direction), true);
                canPlace = true;
            }
        }

        if(canPlace)safeSetBlock(level, pos.immutable(), vine, BlockBehaviour.BlockStateBase::isAir);
    }

    private boolean checkStemState(BlockState state){
        return (!state.is(BlockTags.FEATURES_CANNOT_REPLACE) && state.is(BlockTags.AZALEA_ROOT_REPLACEABLE)) || state.isAir() || state.is(Blocks.WATER );
    }

    private boolean checkHeartState(BlockState state){
        return !state.is(BlockTags.FEATURES_CANNOT_REPLACE) || state.isAir();
    }

    private float getSpiralFloat(int x, int y, int z, float spiralHeight, float baseRadius, float swirliness, float swirls){

        if(y > spiralHeight - 15) return 0;

        double centerX = (swirliness * ( y / spiralHeight) * Math.sin(swirls * y));
        double centerZ = (swirliness * ( y / spiralHeight) * Math.cos(swirls * y));

        double maxRadius = baseRadius * (1 - (y / spiralHeight));

        if(maxRadius <= 0) return 0;

        double dx = x - centerX;
        double dz = z - centerZ;

        double distance = Math.sqrt(dx*dx + dz*dz);

        if(distance >= maxRadius) return 0;

        return (float) (1f - (distance / maxRadius));


    }
}
