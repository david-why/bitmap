package net.davidwhy.bitmap.logic;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

public class SemiConductorMachine {
    private Map<BlockPos, SemiconductorWire> nodes;
    private List<SemiconductorWire> lines;
    private List<Long> pendingTicks;
    private List<SemiconductorWire> pendingWires;

    public int create(Set<BlockPos> nodes, Set<BlockPos> coopNodes) {
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

    public void tick() {

    }

    public void run(long absTick, int times, Set<BlockPos> lowNodes, Set<BlockPos> highNodes) {

    }
}