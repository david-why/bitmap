package net.davidwhy.bitmap.logic;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.math.BlockPos;

public class SemiconductorMachine {
    public Map<BlockPos, SemiconductorWire> nodes;
    public List<SemiconductorWire> lines;
    public List<Long> pendingTicks;
    public List<SemiconductorWire> pendingWires;

    public int create(Set<BlockPos> allNodes, Set<BlockPos> coopNodes) {
        return 0;
    }

    public int release() {
        return 0;
    }

    public Boolean setCoop(BlockPos pos) {
        return false;
    }

    public void unsetCoop(BlockPos pos) {

    }

    public void power(BlockPos pos, BlockPos poweredNeighbor, int powerLevel) {

    }

    public void power(BlockPos pos, long absTicks) {

    }

    public void tick(long absTick) {

    }

    public void run(long absTick, int times, Set<BlockPos> lowNodes, Set<BlockPos> highNodes) {

    }
}