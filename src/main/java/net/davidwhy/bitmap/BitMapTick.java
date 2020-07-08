package net.davidwhy.bitmap;

import java.util.HashSet;
import java.util.Set;
import net.davidwhy.bitmap.logic.Semiconductor;
import net.davidwhy.bitmap.block.SemiconductorBlock;
import net.minecraft.block.BlockState;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class BitMapTick implements ServerTickEvents.StartWorldTick {
    public BitMapTick() {
        ServerTickEvents.START_WORLD_TICK.register(this);
    }
/*
    private static int i = 0;
    private static Block[] blocks = {
        BitMapMod.BLACK_BLOCK, 
        BitMapMod.RED_BLOCK, 
        BitMapMod.YELLOW_BLOCK, 
        BitMapMod.GREEN_BLOCK, 
        BitMapMod.CYAN_BLOCK, 
        BitMapMod.BLUE_BLOCK, 
        BitMapMod.PINK_BLOCK, 
        BitMapMod.WHITE_BLOCK, 
    };

    private void genComputer(ServerWorld world) {
        for (int a = 0; a < 100; a++) {
            if (i < 0) {
                return;
            }
            int j = BitMapComputer.data.indexOf(',', i);
            if (j < 0) {
                i=-1;
                return;
            }
            int k = BitMapComputer.data.indexOf(',', j + 1);
            if (k < 0) {
                i=-1;
                return;
            }
            int l = BitMapComputer.data.indexOf(',', k + 1);
            if (l < 0) {
                i=-1;
                return;
            }
            int x = Integer.parseInt(BitMapComputer.data.substring(i, j));
            int y = Integer.parseInt(BitMapComputer.data.substring(j + 1, k));
            int c = Integer.parseInt(BitMapComputer.data.substring(k + 1, l));
            world.setBlockState(new BlockPos(x, 10, y), blocks[c].getDefaultState());
            i = l + 1;
        }
    }
*/

    public void onStartTick(ServerWorld world) {
        if (world.dimension.getType() != DimensionType.OVERWORLD) {
            return;
        }
        //genComputer(world);

        Set<Long> lowNodes = new HashSet<Long>();
        Set<Long> highNodes = new HashSet<Long>();
        Semiconductor.tick(lowNodes, highNodes);

        lowNodes.forEach((Long a) -> {
            BlockPos pos = SemiconductorBlock.i2b(a);
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof SemiconductorBlock) {
                world.setBlockState(pos, (BlockState) state.with(SemiconductorBlock.ON, 1), 3);
            }
        });
        highNodes.forEach((Long a) -> {
            BlockPos pos = SemiconductorBlock.i2b(a);
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof SemiconductorBlock) {
                world.setBlockState(pos, (BlockState) state.with(SemiconductorBlock.ON, 2), 3);
            }
        });
    }
}
