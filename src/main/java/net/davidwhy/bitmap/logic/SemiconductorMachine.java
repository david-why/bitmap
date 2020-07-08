package net.davidwhy.bitmap.logic;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SemiconductorMachine {

    private Map<Long, SemiconductorWire> nodes;
    private Set<SemiconductorWire> wires;
    private Set<SemiconductorWire> activeWires;
    private Set<SemiconductorWire> changedWires;
    private List<Long> pendingTicks;
    private List<SemiconductorWire> pendingWires;

    private static long staticId = 0;
    private long machineId;

    public SemiconductorMachine() {
        nodes = new HashMap<Long, SemiconductorWire>();
        wires = new HashSet<SemiconductorWire>();
        activeWires = new HashSet<SemiconductorWire>();
        changedWires = new HashSet<SemiconductorWire>();
        pendingTicks = new ArrayList<Long>();
        pendingWires = new ArrayList<SemiconductorWire>();
        this.machineId = staticId++;
    }

    public int hashCode() {
        return (int) machineId;
    }

    public boolean equals(Object o) {
        if (o instanceof SemiconductorMachine) {
            return machineId == ((SemiconductorMachine) o).machineId;
        }
        return false;
    }

    private static long dirs[][] = { { 0x10000000000L, 0x100000L, 0x1L }, { 0x100000L, 0x10000000000L, 0x1L },
            { 0x1L, 0x100000L, 0x10000000000L }, { -0x10000000000L, 0x100000L, 0x1L },
            { -0x100000L, 0x10000000000L, 0x1L }, { -0x1L, 0x100000L, 0x10000000000L } };

    private void connectWire(long a, long b, Map<Long, Long> masterLongs, Map<Long, Set<Long>> slaveLongs) {
        long master = masterLongs.get(a);
        long other = masterLongs.get(b);
        if (master != other) {
            Set<Long> slaveOthers = slaveLongs.get(other);
            slaveOthers.forEach((Long c) -> {
                masterLongs.put(c, master);
            });
            Set<Long> slaves = slaveLongs.get(master);
            slaves.addAll(slaveOthers);
            slaveLongs.remove(other);
        }
    }

    public int create(Set<Long> allNodes, Set<Long> coopNodes, Set<Long> poweredNodes) {
        Map<Long, Long> masterLongs = new HashMap<Long, Long>();
        Map<Long, Set<Long>> slaveLongs = new HashMap<Long, Set<Long>>();
        Map<Long, Set<Long>> enableLongs = new HashMap<Long, Set<Long>>();
        allNodes.forEach((Long a) -> {
            masterLongs.put(a, a);
            Set<Long> slaves = new HashSet<Long>();
            slaves.add(a);
            slaveLongs.put(a, slaves);
        });
        allNodes.forEach((Long a) -> {
            for (int i = 0; i < dirs.length; i++) {
                long b = a + dirs[i][0];
                if (allNodes.contains(b)) {
                    connectWire(a, b, masterLongs, slaveLongs);
                    continue;
                }
                long c = a + dirs[i][0] * 2;
                if (!allNodes.contains(c)) {
                    continue;
                }
                for (int j = 1; j <= 2; j++) {
                    if (allNodes.contains(a + dirs[i][j]) || allNodes.contains(a - dirs[i][j])) {
                        continue;
                    }
                    if (!allNodes.contains(b + dirs[i][j]) || !allNodes.contains(b - dirs[i][j])) {
                        continue;
                    }
                    if (!allNodes.contains(c + dirs[i][j]) && !allNodes.contains(c - dirs[i][j])) {
                        connectWire(a, c, masterLongs, slaveLongs);
                        continue;
                    }
                    if (allNodes.contains(c + dirs[i][j]) && allNodes.contains(c - dirs[i][j])) {
                        if (enableLongs.containsKey(c)) {
                            enableLongs.get(c).add(a);
                        } else {
                            Set<Long> enables = new HashSet<Long>();
                            enables.add(a);
                            enableLongs.put(c, enables);
                        }
                        continue;
                    }
                }
            }
        });

        slaveLongs.forEach((Long a, Set<Long> slaves) -> {
            Set<Long> allWireNodes = new HashSet<Long>();
            Set<Long> coopWireNodes = new HashSet<Long>();
            slaves.forEach((Long b) -> {
                allWireNodes.add(b);
                if (coopNodes.contains(b)) {
                    coopWireNodes.add(b);
                }
            });
            SemiconductorWire wire = new SemiconductorWire(allWireNodes, coopWireNodes);
            wires.add(wire);
            slaves.forEach((Long b) -> {
                nodes.put(b, wire);
            });
        });

        enableLongs.forEach((Long a, Set<Long> enables) -> {
            Long p = masterLongs.get(a);
            SemiconductorWire wire = nodes.get(p);
            enables.forEach((Long c) -> {
                Long q = masterLongs.get(c);
                SemiconductorWire other = nodes.get(q);
                wire.enable(other);
            });
        });

        poweredNodes.forEach((Long a) -> {
            SemiconductorWire wire = nodes.get(a);
            if (wire != null) {
                wire.power(a, true);
            }
        });

        changedWires.addAll(wires);
        return allNodes.size();
    }

    public void release(Set<Long> allNodes, Set<Long> coopNodes) {
        wires.forEach((SemiconductorWire wire) -> {
            if (allNodes != null) {
                wire.exportAllNodes(allNodes);
            }
            if (coopNodes != null) {
                wire.exportCoopNodes(coopNodes);
            }
        });
    }

    public Boolean setCoop(Long pos) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null) {
            return false;
        }
        return wire.setCoop(pos);
    }

    public void unsetCoop(Long pos) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null) {
            return;
        }
        wire.unsetCoop(pos);
    }

    public void power(Long pos, Boolean powered) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null) {
            return;
        }
        wire.power(pos, powered);
        changedWires.add(wire);
    }

    public void power(Long pos, long absTick) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null) {
            return;
        }
        wire.incPoweredCommand();
        changedWires.add(wire);
        pendingTicks.add(absTick);
        pendingWires.add(wire);
    }

    public void run(long absTick, int times, Set<Long> lowNodes, Set<Long> highNodes) {
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
                wire.exportCoopNodes(lowNodes);
            }
        });
        lowWires.forEach((SemiconductorWire wire) -> {
            if (wire.isHigh()) {
                wire.exportCoopNodes(highNodes);
            }
        });
        changedWires.forEach((SemiconductorWire wire) -> {
            if (wire.isHigh()) {
                wire.exportCoopNodes(highNodes);
            } else {
                wire.exportCoopNodes(lowNodes);
            }
        });
        changedWires.clear();
    }

    private void runOnce() {
        Set<SemiconductorWire> highWires = new HashSet<SemiconductorWire>();
        Set<SemiconductorWire> lowWires = new HashSet<SemiconductorWire>();
        activeWires.forEach((SemiconductorWire wire) -> {
            if (wire.isHigh()) {
                highWires.add(wire);
            } else {
                lowWires.add(wire);
            }
        });
        activeWires.clear();
        highWires.forEach((SemiconductorWire wire) -> {
            wire.highOut(activeWires);
        });
        lowWires.forEach((SemiconductorWire wire) -> {
            wire.lowOut(activeWires);
        });
    }
}