package net.davidwhy.bitmap.logic;

import java.io.*;
import java.util.Set;
import java.util.HashSet;

public class SemiconductorWire {

    private Set<Long> allNodes;
    private Set<Long> coopNodes;
    private Set<Long> poweredNodes;
    private int poweredCommands;
    private int currentIn;
    private boolean wasHigh;

    private static long staticId = 0;
    private long wireId;

    public Set<SemiconductorWire> enableOthers;
    public Set<Long> enableOtherNodes;

    public SemiconductorWire(Set<Long> allNodes, Set<Long> coopNodes) {
        this.allNodes = allNodes;
        this.coopNodes = coopNodes;
        enableOthers = new HashSet<SemiconductorWire>();
        enableOtherNodes = new HashSet<Long>();
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

    public void power(long pos, boolean powered) {
        if (powered) {
            if (!poweredNodes.contains(pos)) {
                poweredNodes.add(pos);
                poweredCommands++;
            }
        } else {
            if (poweredNodes.contains(pos)) {
                poweredNodes.remove(pos);
                poweredCommands--;
            }
        }
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
        if (!enableOthers.contains(wire)) {
            enableOthers.add(wire);
            wire.currentIn++;
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
        for (SemiconductorWire other: enableOthers) {
            if (--other.currentIn == 0) {
                activeWires.add(other);
            }
        }
    }

    public void lowOut(Set<SemiconductorWire> activeWires) {
        for (SemiconductorWire other: enableOthers) {
            if (other.currentIn++ == 0) {
                activeWires.add(other);
            }
        }
    }

    public void exportAllNodes(Set<Long> nodes) {
        nodes.addAll(allNodes);
    }

    public void exportCoopNodes(Set<Long> nodes) {
        nodes.addAll(coopNodes);
    }

    public void writeObject(PrintWriter out) throws IOException {
        out.println(wireId);
        out.println(allNodes.size());
        for (Long pos: allNodes) {
            out.println(pos);
        }
        out.println(coopNodes.size());
        for (Long pos: coopNodes) {
            out.println(pos);
        }
        out.println(enableOthers.size());
        for (SemiconductorWire wire: enableOthers) {
            for (Long node: wire.allNodes) {
                out.println(node);
                break;
            }
        }
        out.println(poweredNodes.size());
        for (Long pos: poweredNodes) {
            out.println(pos);
        }
        out.println(poweredCommands);
        out.println(currentIn);
        out.println(wasHigh ? 9999 : 0);
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
            enableOtherNodes.add(Long.parseLong(in.readLine()));
        }
        for (int i = Integer.parseInt(in.readLine()); i > 0; i--) {
            poweredNodes.add(Long.parseLong(in.readLine()));
        }
        poweredCommands = Integer.parseInt(in.readLine());
        currentIn = Integer.parseInt(in.readLine());
        wasHigh = (Integer.parseInt(in.readLine()) > 0);
    }
}
