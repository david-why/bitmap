package net.davidwhy.bitmap.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.math.BlockPos;

public class SemiconductorMachine {
    public Map<BlockPos, SemiconductorWire> nodes;
    public List<SemiconductorWire> wires;
    public List<SemiconductorWire> changedWires;
    public List<Long> pendingTicks;
    public List<SemiconductorWire> pendingWires;

    public SemiconductorMachine() {
        nodes = new HashMap<BlockPos, SemiconductorWire>();
        wires = new ArrayList<SemiconductorWire>();
        changedWires = new ArrayList<SemiconductorWire>();
        pendingTicks = new ArrayList<Long>();
        pendingWires = new ArrayList<SemiconductorWire>();
    }

    public int create(Set<BlockPos> allNodes, Set<BlockPos> coopNodes) {
        SemiconductorWire wire = new SemiconductorWire(allNodes, coopNodes);
        wires.add(wire);
        changedWires.add(wire);
        allNodes.forEach((BlockPos pos) -> {
            nodes.put(pos, wire);
        });
        return allNodes.size();
    }

    public Set<BlockPos> release() {
        Set<BlockPos> allNodes = new HashSet<BlockPos>();
        nodes.forEach((pos, wire) -> {
            allNodes.add(pos);
        });
        return allNodes;
    }

    public Boolean setCoop(BlockPos pos) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null)
            return false;
        return wire.setCoop(pos);
    }

    public void unsetCoop(BlockPos pos) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null)
            return;
        wire.unsetCoop(pos);
    }

    public void power(BlockPos pos, BlockPos neighborPos, int powerLevel) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null)
            return;
        if (wire.power(neighborPos, powerLevel))
            changedWires.add(wire);
    }

    public void power(BlockPos pos, long absTick) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null)
            return;
        if (wire.incPoweredCommand())
            changedWires.add(wire);
        pendingTicks.add(absTick);
        pendingWires.add(wire);
    }

    /*
     * public void tick(long absTick) {
     * 
     * }
     */

    public void run(long absTick, int times, Set<BlockPos> lowNodes, Set<BlockPos> highNodes) {
        while (pendingTicks.size() > 0) {
            if (pendingTicks.get(0) > absTick)
                break;
            SemiconductorWire wire =  pendingWires.get(0);
            if (wire.decPoweredCommand())
                changedWires.add(wire);
            pendingTicks.remove(0);
            pendingWires.remove(0);
        }

        List<SemiconductorWire> highWires = new ArrayList<SemiconductorWire>();
        List<SemiconductorWire> lowWires = new ArrayList<SemiconductorWire>();
        wires.forEach((SemiconductorWire wire) -> {
            if (wire.isHigh())
                highWires.add(wire);
            else
                lowWires.add(wire);
        });

        while (times-- > 0) {
            ;
        }

        highWires.forEach((SemiconductorWire wire) -> {
            if (!wire.isHigh()) {
                lowNodes.addAll(wire.coopNodes);
            }
        });
        lowWires.forEach((SemiconductorWire wire) -> {
            if (wire.isHigh()) {
                highNodes.addAll(wire.coopNodes);
            }
        });
        changedWires.forEach((SemiconductorWire wire) -> {
            if (wire.isHigh()) {
                highNodes.addAll(wire.coopNodes);
            } else {
                lowNodes.addAll(wire.coopNodes);
            }
        });
        changedWires.clear();
    }
}