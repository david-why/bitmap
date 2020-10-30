package net.davidwhy.bitmap;

import java.util.HashSet;
import java.util.Set;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.davidwhy.bitmap.logic.Semiconductor;

public class BitMapTick implements ServerTickEvents.StartWorldTick {

    public BitMapTick() {
        ServerTickEvents.START_WORLD_TICK.register(this);
    }

    private int tickCount = 0;

    public void onStartTick(ServerWorld world) {
        if (world.getDimension() != DimensionType.getOverworldDimensionType()) {
            return;
        }

        if (tickCount++ % 2 == 1) {
            return;
        }

        BitMapComputer.genComputer(world);

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
