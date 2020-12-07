package net.davidwhy.bitmap.logic;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class SemiconductorWire {

    private SemiconductorMachine machine;
    private Set<Long> allNodes;
    private Set<Long> coopNodes;
    private Set<Long> poweredNodes;
    private int poweredCommands;
    private int currentIn;
    private boolean wasHigh;

    public long wireId;
    private static long staticId = 0;

    private List<SemiconductorWire> enableOthers;
    private Set<Long> enableOtherIds;

    public SemiconductorWire(SemiconductorMachine m, Set<Long> allNodes, Set<Long> coopNodes) {
        machine = m;
        this.allNodes = allNodes;
        this.coopNodes = coopNodes;
        enableOthers = new ArrayList<SemiconductorWire>();
        enableOtherIds = new HashSet<Long>();
        poweredNodes = new HashSet<Long>();
        poweredCommands = 0;
        currentIn = 0;
        wasHigh = false;
        wireId = staticId++;
    }

    public int hashCode() {
        return (int) wireId;
    }

    public boolean equals(Object o) {
        if (o instanceof SemiconductorWire) {
            return wireId == ((SemiconductorWire) o).wireId;
        }
        return false;
    }

    public void incPoweredCommand() {
        poweredCommands++;
    }

    public void decPoweredCommand() {
        poweredCommands--;
    }

    public boolean power(long pos, boolean powered) {
        if (powered) {
            if (!poweredNodes.contains(pos)) {
                poweredNodes.add(pos);
                poweredCommands++;
                return true;
            }
        } else {
            if (poweredNodes.contains(pos)) {
                poweredNodes.remove(pos);
                poweredCommands--;
                return true;
            }
        }
        return false;
    }

    public boolean isHigh() {
        return currentIn > 0 || poweredCommands > 0;
    }

    public boolean setCoop(long pos) {
        coopNodes.add(pos);
        return isHigh();
    }

    public void unsetCoop(long pos) {
        coopNodes.remove(pos);
    }

    public boolean haveCoopNodes() {
        return !coopNodes.isEmpty();
    }

    public void enable(SemiconductorWire wire) {
        if (!enableOtherIds.contains(wire.wireId)) {
            enableOtherIds.add(wire.wireId);
            enableOthers.add(wire);
            wire.currentIn++;
        }
    }

    public void enableAgain() {
        enableOthers.clear();
        for (Long id : enableOtherIds) {
            enableOthers.add(machine.getWire(id));
        }
    }

    public boolean goHigh() {
        if (wasHigh || !isHigh()) {
            return false;
        }
        wasHigh = true;
        return true;
    }

    public boolean goLow() {
        if (!wasHigh || isHigh()) {
            return false;
        }
        wasHigh = false;
        return true;
    }

    public void highOut(Set<SemiconductorWire> activeWires) {
        for (SemiconductorWire other : enableOthers) {
            if (--other.currentIn == 0) {
                activeWires.add(other);
            }
        }
    }

    public void lowOut(Set<SemiconductorWire> activeWires) {
        for (SemiconductorWire other : enableOthers) {
            if (other.currentIn++ == 0) {
                activeWires.add(other);
            }
        }
    }

    public Long getNode() {
        for (Long pos : allNodes) {
            return pos;
        }
        return 0L;
    }

    public void exportAllNodes(Set<Long> nodes) {
        if (nodes != null) {
            nodes.addAll(allNodes);
        }
    }

    public void exportCoopNodes(Set<Long> nodes) {
        if (nodes != null) {
            nodes.addAll(coopNodes);
        }
    }

    public void writeObject(PrintWriter out) throws IOException {
        out.println(wireId);
        out.println(allNodes.size());
        for (Long pos : allNodes) {
            out.println(pos);
        }
        out.println(coopNodes.size());
        for (Long pos : coopNodes) {
            out.println(pos);
        }
        out.println(enableOtherIds.size());
        for (Long id : enableOtherIds) {
            out.println(id);
        }
        out.println(poweredNodes.size());
        for (Long pos : poweredNodes) {
            out.println(pos);
        }
        out.println(poweredCommands);
        out.println(currentIn);
        out.println(wasHigh ? "High" : "Low");
    }

    public void readObject(BufferedReader in) throws IOException, ClassNotFoundException {
        wireId = Long.parseLong(in.readLine());
        if (wireId >= staticId) {
            staticId = wireId + 1;
        }
        for (int i = Integer.parseInt(in.readLine()); i > 0; i--) {
            allNodes.add(Long.parseLong(in.readLine()));
        }
        for (int i = Integer.parseInt(in.readLine()); i > 0; i--) {
            coopNodes.add(Long.parseLong(in.readLine()));
        }
        for (int i = Integer.parseInt(in.readLine()); i > 0; i--) {
            enableOtherIds.add(Long.parseLong(in.readLine()));
        }
        for (int i = Integer.parseInt(in.readLine()); i > 0; i--) {
            poweredNodes.add(Long.parseLong(in.readLine()));
        }
        poweredCommands = Integer.parseInt(in.readLine());
        currentIn = Integer.parseInt(in.readLine());
        wasHigh = in.readLine().equals("High");
    }
}
