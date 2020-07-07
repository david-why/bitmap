package net.davidwhy.bitmap.logic;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.util.math.BlockPos;

public class SemiconductorWire {
	public Set<BlockPos> allNodes;
	public Set<BlockPos> coopNodes;
	public Set<SemiconductorWire> enableOthers;
	public Set<SemiconductorWire> enableByOthers;
	public Set<BlockPos> poweredNodes;
	public int poweredCommands;
	public Set<SemiconductorWire> currentIn;

	private static long staticId = 0;
	private long wireId;

	public SemiconductorWire(Set<BlockPos> allNodes, Set<BlockPos> coopNodes) {
		this.allNodes = allNodes;
		this.coopNodes = coopNodes;
		this.enableOthers = new HashSet<SemiconductorWire>();
		this.enableByOthers = new HashSet<SemiconductorWire>();
		this.poweredNodes = new HashSet<BlockPos>();
		this.poweredCommands = 0;
		this.currentIn = new HashSet<SemiconductorWire>();
		this.wireId = staticId++;
	}


    public int hashCode() {
        return (int)wireId;
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

	public Boolean power(BlockPos pos, Boolean powered) {
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

    public Boolean setCoop(BlockPos pos) {
		coopNodes.add(pos);
		return isHigh();
	}

	public void unsetCoop(BlockPos pos) {
		coopNodes.remove(pos);
	}

	public void enable(SemiconductorWire wire) {
		enableOthers.add(wire);
		wire.enableByOthers.add(this);
		wire.currentIn.add(this);
	}
}