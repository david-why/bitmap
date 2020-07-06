package net.davidwhy.bitmap;

import java.util.HashSet;
import java.util.Set;
import net.davidwhy.bitmap.logic.Semiconductor;
import net.davidwhy.bitmap.block.SemiconductorBlock;
import net.minecraft.block.BlockState;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class BitMapTick implements ServerTickEvents.StartWorldTick {
    public BitMapTick() {
        ServerTickEvents.START_WORLD_TICK.register(this);
    }

    public void onStartTick(ServerWorld world) {
        if (SemiconductorBlock.world != world)
            return;
        Set<BlockPos> lowNodes = new HashSet<BlockPos>();
        Set<BlockPos> highNodes = new HashSet<BlockPos>();
        Semiconductor.tick(lowNodes, highNodes);

        if (lowNodes.size() > 0 || highNodes.size() > 0)
            System.out.printf("* low %d high %d\n", lowNodes.size(), highNodes.size());

        lowNodes.forEach((BlockPos pos) -> {
            BlockState state = world.getBlockState(pos);
            System.out.printf("pos %s state %d\n", pos.toString(), state.get(SemiconductorBlock.ON));
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
