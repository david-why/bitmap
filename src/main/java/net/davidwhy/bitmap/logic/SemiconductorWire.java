package net.davidwhy.bitmap.logic;

import java.util.HashSet;
import java.util.Set;

public class SemiconductorWire {
	private Set<Long> allNodes;
	private Set<Long> coopNodes;
	private Set<SemiconductorWire> enableOthers;
	private Set<Long> poweredNodes;
	private int poweredCommands;
	private Set<SemiconductorWire> currentIn;

	private static long staticId = 0;
	private long wireId;

	public SemiconductorWire(Set<Long> allNodes, Set<Long> coopNodes) {
		this.allNodes = allNodes;
		this.coopNodes = coopNodes;
		this.enableOthers = new HashSet<SemiconductorWire>();
		this.poweredNodes = new HashSet<Long>();
		this.poweredCommands = 0;
		this.currentIn = new HashSet<SemiconductorWire>();
		this.wireId = staticId++;
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

	private Boolean addIn(SemiconductorWire wire) {
		Boolean wasEmpty = currentIn.isEmpty();
		currentIn.add(wire);
		return wasEmpty;
	}

	private Boolean removeIn(SemiconductorWire wire) {
		currentIn.remove(wire);
		return currentIn.isEmpty();
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
		return currentIn.size() > 0 || poweredNodes.size() > 0 || poweredCommands > 0;
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
		wire.currentIn.add(this);
	}

	public void highOut(Set<SemiconductorWire> activeWires) {
		enableOthers.forEach((SemiconductorWire other) -> {
			if (other.removeIn(this)) {
				activeWires.add(other);
			}
		});
	}

	public void lowOut(Set<SemiconductorWire> activeWires) {
		enableOthers.forEach((SemiconductorWire other) -> {
			if (other.addIn(this)) {
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