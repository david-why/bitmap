package net.davidwhy.bitmap.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.math.BlockPos;

public class SemiconductorMachine {
    private Map<BlockPos, SemiconductorWire> nodes;
    private Set<SemiconductorWire> wires;
    private Set<SemiconductorWire> activeWires;
    private Set<SemiconductorWire> changedWires;
    private List<Long> pendingTicks;
    private List<SemiconductorWire> pendingWires;

    public SemiconductorMachine() {
        nodes = new HashMap<BlockPos, SemiconductorWire>();
        wires = new HashSet<SemiconductorWire>();
        activeWires = new HashSet<SemiconductorWire>();
        changedWires = new HashSet<SemiconductorWire>();
        pendingTicks = new ArrayList<Long>();
        pendingWires = new ArrayList<SemiconductorWire>();
    }

    private static long dirs[][] = { { 0x10000000000L, 0x100000L, 0x1L }, { 0x100000L, 0x10000000000L, 0x1L },
            { 0x1L, 0x100000L, 0x10000000000L }, { -0x10000000000L, 0x100000L, 0x1L },
            { -0x100000L, 0x10000000000L, 0x1L }, { -0x1L, 0x100000L, 0x10000000000L } };

    private void connectWire(long a, long b, Map<Long, Long> masterLongs, Map<Long, Set<Long>> slaveLongs) {
        Long master = masterLongs.get(a);
        Long other = masterLongs.get(b);
        if (master == other) {
            return;
        }
        Set<Long> slaveOthers = slaveLongs.get(other);
        slaveOthers.forEach((Long c) -> {
            masterLongs.put(c, master);
        });
        Set<Long> slaves = slaveLongs.get(master);
        slaves.addAll(slaveOthers);
        slaveLongs.remove(other);
    }

    public int create(Set<BlockPos> allNodes, Set<BlockPos> coopNodes, Set<BlockPos> poweredNodes) {
        Map<Long, BlockPos> allLongs = new HashMap<Long, BlockPos>();
        Map<Long, Long> masterLongs = new HashMap<Long, Long>();
        Map<Long, Set<Long>> slaveLongs = new HashMap<Long, Set<Long>>();
        Map<Long, Long> enableLongs = new HashMap<Long, Long>();
        allNodes.forEach((BlockPos pos) -> {
            long a = 0x10000000000L * (0x80000 + pos.getX()) + 0x100000L * (0x80000 + pos.getY())
                    + (0x80000 + pos.getZ());
            allLongs.put(a, pos);
            masterLongs.put(a, a);
            Set<Long> slaves = new HashSet<Long>();
            slaves.add(a);
            slaveLongs.put(a, slaves);
        });
        allLongs.forEach((Long a, BlockPos pos) -> {
            for (int i = 0; i < dirs.length; i++) {
                long b = a + dirs[i][0];
                if (allLongs.containsKey(b)) {
                    connectWire(a, b, masterLongs, slaveLongs);
                    continue;
                }
                long c = a + dirs[i][0] * 2;
                if (!allLongs.containsKey(c)) {
                    continue;
                }
                for (int j = 1; j <= 2; j++) {
                    if (allLongs.containsKey(a + dirs[i][j]) || allLongs.containsKey(a - dirs[i][j])) {
                        continue;
                    }
                    if (!allLongs.containsKey(b + dirs[i][j]) || !allLongs.containsKey(b - dirs[i][j])) {
                        continue;
                    }
                    if (!allLongs.containsKey(c + dirs[i][j]) && !allLongs.containsKey(c - dirs[i][j])) {
                        connectWire(a, c, masterLongs, slaveLongs);
                        continue;
                    }
                    if (allLongs.containsKey(c + dirs[i][j]) && allLongs.containsKey(c - dirs[i][j])) {
                        enableLongs.put(c, a);
                        continue;
                    }
                }
            }
        });

        slaveLongs.forEach((Long a, Set<Long> slaves) -> {
            Set<BlockPos> allWireNodes = new HashSet<BlockPos>();
            Set<BlockPos> coopWireNodes = new HashSet<BlockPos>();
            slaves.forEach((Long b) -> {
                BlockPos p = allLongs.get(b);
                allWireNodes.add(p);
                if (coopNodes.contains(p)) {
                    coopWireNodes.add(p);
                }
            });
            SemiconductorWire wire = new SemiconductorWire(allWireNodes, coopWireNodes);
            wires.add(wire);
            slaves.forEach((Long b) -> {
                BlockPos p = allLongs.get(b);
                nodes.put(p, wire);
            });
        });

        enableLongs.forEach((Long a, Long b) -> {
            BlockPos p = allLongs.get(masterLongs.get(a));
            BlockPos q = allLongs.get(masterLongs.get(b));
            SemiconductorWire wire = nodes.get(p);
            SemiconductorWire other = nodes.get(q);
            wire.enable(other);
        });

        poweredNodes.forEach((BlockPos pos) -> {
            SemiconductorWire wire = nodes.get(pos);
            if (wire != null) {
                wire.power(pos, true);
            }
        });

        changedWires.addAll(wires);
        return allNodes.size();
    }

    public void release(Set<BlockPos> allNodes, Set<BlockPos> coopNodes) {
        wires.forEach((SemiconductorWire wire) -> {
            if (allNodes != null) {
                allNodes.addAll(wire.allNodes);
            }
            if (coopNodes != null) {
                coopNodes.addAll(wire.coopNodes);
            }
        });
    }

    public Boolean setCoop(BlockPos pos) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null) {
            return false;
        }
        return wire.setCoop(pos);
    }

    public void unsetCoop(BlockPos pos) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null) {
            return;
        }
        wire.unsetCoop(pos);
    }

    public void power(BlockPos pos, Boolean powered) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null) {
            return;
        }
        wire.power(pos, powered);
        changedWires.add(wire);
    }

    public void power(BlockPos pos, long absTick) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null) {
            return;
        }
        wire.incPoweredCommand();
        changedWires.add(wire);
        pendingTicks.add(absTick);
        pendingWires.add(wire);
    }

    public void run(long absTick, int times, Set<BlockPos> lowNodes, Set<BlockPos> highNodes) {
        while (pendingTicks.size() > 0) {
            if (pendingTicks.get(0) > absTick) {
                break;
            }
            SemiconductorWire wire = pendingWires.get(0);
            wire.decPoweredCommand();
            changedWires.add(wire);
            pendingTicks.remove(0);
            pendingWires.remove(0);
        }

        Set<SemiconductorWire> highWires = new HashSet<SemiconductorWire>();
        Set<SemiconductorWire> lowWires = new HashSet<SemiconductorWire>();
        wires.forEach((SemiconductorWire wire) -> {
            if (wire.isHigh()) {
                highWires.add(wire);
            } else {
                lowWires.add(wire);
            }
        });

        activeWires.addAll(changedWires);
        while (times-- > 0) {
            runOnce();
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

    private void runOnce() {
        Set<SemiconductorWire> nextActiveWires = new HashSet<SemiconductorWire>();
        Set<SemiconductorWire> highWires = new HashSet<SemiconductorWire>();
        Set<SemiconductorWire> lowWires = new HashSet<SemiconductorWire>();
        activeWires.forEach((SemiconductorWire wire) -> {
            if (wire.isHigh()) {
                highWires.add(wire);
            }
            if (!wire.isHigh()) {
                lowWires.add(wire);
            }
        });
        highWires.forEach((SemiconductorWire wire) -> {
            wire.enableOthers.forEach((SemiconductorWire other) -> {
                if (other.removeIn(wire)) {
                    nextActiveWires.add(other);
                }
            });
        });
        lowWires.forEach((SemiconductorWire wire) -> {
            wire.enableOthers.forEach((SemiconductorWire other) -> {
                if (other.addIn(wire)) {
                    nextActiveWires.add(other);
                }
            });
        });
        activeWires = nextActiveWires;
    }
}