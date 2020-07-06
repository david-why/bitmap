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

    public void onStartTick(ServerWorld world) {
        if (world.dimension.getType() != DimensionType.OVERWORLD)
            return;
        Set<BlockPos> lowNodes = new HashSet<BlockPos>();
        Set<BlockPos> highNodes = new HashSet<BlockPos>();
        Semiconductor.tick(lowNodes, highNodes);

        lowNodes.forEach((BlockPos pos) -> {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof SemiconductorBlock)
                world.setBlockState(pos, (BlockState) state.with(SemiconductorBlock.ON, 1), 3);
        });
        highNodes.forEach((BlockPos pos) -> {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof SemiconductorBlock)
                world.setBlockState(pos, (BlockState) state.with(SemiconductorBlock.ON, 2), 3);
        });
    }
}
