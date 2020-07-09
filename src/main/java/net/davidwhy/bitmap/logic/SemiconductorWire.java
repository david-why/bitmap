package net.davidwhy.bitmap.logic;

import java.util.Set;
import java.util.HashSet;

public class SemiconductorWire {

    private Set<Long> allNodes;
    private Set<Long> coopNodes;
    private Set<SemiconductorWire> enableOthers;
    private Set<Long> poweredNodes;
    private int poweredCommands;
    private int currentIn;
    private Boolean wasHigh;

    private static long staticId = 0;
    private long wireId;

    public SemiconductorWire(Set<Long> allNodes, Set<Long> coopNodes) {
        this.allNodes = allNodes;
        this.coopNodes = coopNodes;
        enableOthers = new HashSet<SemiconductorWire>();
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

    public Boolean power(Long pos, Boolean powered) {
        Boolean wasHigh = isHigh();
        if (powered) {
            poweredNodes.add(pos);
        } else {
            poweredNodes.remove(pos);
        }
        return wasHigh != isHigh();
    }

    public Boolean isHigh() {
        return currentIn > 0 || poweredNodes.size() > 0 || poweredCommands > 0;
    }

    public Boolean setCoop(Long pos) {
        coopNodes.add(pos);
        return isHigh();
    }

    public void unsetCoop(Long pos) {
        coopNodes.remove(pos);
    }

    public void enable(SemiconductorWire wire) {
        enableOthers.add(wire);
        wire.currentIn++;
    }

    public Boolean goHigh() {
        if (wasHigh || !isHigh()) {
            return false;
        }
        wasHigh = true;
        return true;
    }

    public Boolean goLow() {
        if (!wasHigh || isHigh()) {
            return false;
        }
        wasHigh = false;
        return true;
    }

    public void highOut(Set<SemiconductorWire> activeWires) {
        enableOthers.forEach((SemiconductorWire other) -> {
            if (--other.currentIn == 0) {
                activeWires.add(other);
            }
        });
    }

    public void lowOut(Set<SemiconductorWire> activeWires) {
        enableOthers.forEach((SemiconductorWire other) -> {
            if (other.currentIn++ == 0) {
                activeWires.add(other);
            }
        });
    }

    public void exportAllNodes(Set<Long> nodes) {
        nodes.addAll(allNodes);
    }

    public void exportCoopNodes(Set<Long> nodes) {
        nodes.addAll(coopNodes);
    }
}