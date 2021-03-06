package net.davidwhy.bitmap.logic;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class SemiconductorMachine {

    private Map<Long, SemiconductorWire> nodes;
    private Map<Long, SemiconductorWire> wires;
    private Set<SemiconductorWire> activeWires;
    private List<Long> pendingTicks;
    private List<SemiconductorWire> pendingWires;

    private static long staticId = 0;
    private long machineId;

    public SemiconductorMachine() {
        nodes = new HashMap<Long, SemiconductorWire>();
        wires = new HashMap<Long, SemiconductorWire>();
        activeWires = new HashSet<SemiconductorWire>();
        pendingTicks = new ArrayList<Long>();
        pendingWires = new ArrayList<SemiconductorWire>();
        machineId = staticId++;
    }

    public long getMachineId() {
        return machineId;
    }

    public int hashCode() {
        return (int)machineId;
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
            for (long c : slaveOthers) {
                masterLongs.put(c, master);
            }
            Set<Long> slaves = slaveLongs.get(master);
            slaves.addAll(slaveOthers);
            slaveLongs.remove(other);
        }
    }

    public int create(Set<Long> allNodes, Set<Long> coopNodes, Set<Long> poweredNodes) {
        Map<Long, Long> masterLongs = new HashMap<Long, Long>();
        Map<Long, Set<Long>> slaveLongs = new HashMap<Long, Set<Long>>();
        Map<Long, Set<Long>> enableLongs = new HashMap<Long, Set<Long>>();
        for (long a : allNodes) {
            masterLongs.put(a, a);
            Set<Long> slaves = new HashSet<Long>();
            slaves.add(a);
            slaveLongs.put(a, slaves);
        }
        for (long a : allNodes) {
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
        }

        for (Set<Long> slaves : slaveLongs.values()) {
            Set<Long> allWireNodes = new HashSet<Long>();
            Set<Long> coopWireNodes = new HashSet<Long>();
            for (long b : slaves) {
                allWireNodes.add(b);
                if (coopNodes.contains(b)) {
                    coopWireNodes.add(b);
                }
            }
            SemiconductorWire wire = new SemiconductorWire(this, allWireNodes, coopWireNodes);
            wires.put(wire.getWireId(), wire);
            for (long b : slaves) {
                nodes.put(b, wire);
            }
        }

        for (long a : enableLongs.keySet()) {
            SemiconductorWire wire = nodes.get(masterLongs.get(a));
            for (long c : enableLongs.get(a)) {
                SemiconductorWire other = nodes.get(masterLongs.get(c));
                wire.enable(other);
            }
        }

        for (long a : poweredNodes) {
            SemiconductorWire wire = nodes.get(a);
            if (wire != null) {
                wire.power(a, true);
            }
        }

        activeWires.addAll(wires.values());
        return allNodes.size();
    }

    public void release(Set<Long> allNodes, Set<Long> coopNodes) {
        for (SemiconductorWire wire : wires.values()) {
            wire.exportAllNodes(allNodes);
            wire.exportCoopNodes(coopNodes);
        }
    }

    public boolean setCoop(long pos) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null) {
            return false;
        }
        return wire.setCoop(pos);
    }

    public void unsetCoop(long pos) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null) {
            return;
        }
        wire.unsetCoop(pos);
    }

    public void power(long pos, boolean powered) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null) {
            return;
        }
        if (wire.power(pos, powered)) {
            activeWires.add(wire);
        }
    }

    public void power(long pos, long absTick) {
        SemiconductorWire wire = nodes.get(pos);
        if (wire == null) {
            return;
        }
        wire.incPoweredCommand();
        activeWires.add(wire);
        pendingTicks.add(absTick);
        pendingWires.add(wire);
    }

    public void run(long absTick, int times, Set<Long> lowNodes, Set<Long> highNodes) {
        while (pendingTicks.size() > 0 && pendingTicks.get(0) <= absTick) {
            SemiconductorWire wire = pendingWires.get(0);
            wire.decPoweredCommand();
            activeWires.add(wire);
            pendingTicks.remove(0);
            pendingWires.remove(0);
        }

        Set<SemiconductorWire> highNotifyWires = new HashSet<SemiconductorWire>();
        Set<SemiconductorWire> lowNotifyWires = new HashSet<SemiconductorWire>();
        List<SemiconductorWire> highWires = new ArrayList<SemiconductorWire>();
        List<SemiconductorWire> lowWires = new ArrayList<SemiconductorWire>();

        while (times-- > 0) {
            for (SemiconductorWire wire : activeWires) {
                if (wire.goHigh()) {
                    highWires.add(wire);
                    if (wire.haveCoopNodes()) {
                        if (highNotifyWires.add(wire)) {
                            lowNotifyWires.remove(wire);
                        }
                    }
                }
                if (wire.goLow()) {
                    lowWires.add(wire);
                    if (wire.haveCoopNodes()) {
                        if (lowNotifyWires.add(wire)) {
                            highNotifyWires.remove(wire);
                        }
                    }
                }
            }
            activeWires.clear();

            for (SemiconductorWire wire : highWires) {
                wire.highOut(activeWires);
            }
            highWires.clear();

            for (SemiconductorWire wire : lowWires) {
                wire.lowOut(activeWires);
            }
            lowWires.clear();
        }

        for (SemiconductorWire wire : lowNotifyWires) {
            wire.exportCoopNodes(lowNodes);
        }
        for (SemiconductorWire wire : highNotifyWires) {
            wire.exportCoopNodes(highNodes);
        }
    }

    public void exportAllNodes(Set<Long> nodes) {
        for (SemiconductorWire wire : wires.values()) {
            wire.exportAllNodes(nodes);
        }
    }

    public void exportCoopNodes(Set<Long> nodes) {
        for (SemiconductorWire wire : wires.values()) {
            wire.exportCoopNodes(nodes);
        }
    }

    public SemiconductorWire getWire(long wireId) {
        return wires.get(wireId);
    }

    public void writeObject(PrintWriter out) throws IOException {
        out.println(machineId);
        out.println(wires.size());
        for (SemiconductorWire wire : wires.values()) {
            wire.writeObject(out);
        }
        out.println(pendingWires.size());
        for (SemiconductorWire wire : pendingWires) {
            out.println(wire.getWireId());
        }
    }

    public void readObject(BufferedReader in) throws IOException {
        machineId = Long.parseLong(in.readLine());
        if (machineId >= staticId) {
            staticId = machineId + 1;
        }
        for (int i = Integer.parseInt(in.readLine()); i > 0; i--) {
            SemiconductorWire wire = new SemiconductorWire(this, new HashSet<Long>(), new HashSet<Long>());
            wire.readObject(in);
            wires.put(wire.getWireId(), wire);
            activeWires.add(wire);
            Set<Long> x = new HashSet<Long>();
            wire.exportAllNodes(x);
            for (long pos : x) {
                nodes.put(pos, wire);
            }
        }
        for (SemiconductorWire wire : wires.values()) {
            wire.enableAgain();
        }
        for (int i = Integer.parseInt(in.readLine()); i > 0; i--) {
            pendingTicks.add(0L);
            pendingWires.add(getWire(Long.parseLong(in.readLine())));
        }
    }
}
