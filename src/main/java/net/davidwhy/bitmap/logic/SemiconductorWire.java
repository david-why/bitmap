package net.davidwhy.bitmap.logic;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.util.math.BlockPos;

public class SemiconductorWire {
	private Set<BlockPos> allNodes;
	private Set<BlockPos> coopNodes;
	public Set<SemiconductorWire> enables;
	private Set<BlockPos> poweredNeighbors;
	private int poweredCommands;
	public int currentIn;
	public int nextIn;

	public SemiconductorWire(Set<BlockPos> allNodes, Set<BlockPos> coopNodes) {
		this.allNodes = allNodes;
		this.coopNodes = coopNodes;
		this.enables = new HashSet<SemiconductorWire>();
		this.poweredNeighbors = new HashSet<BlockPos>();
		this.poweredCommands = 0;
		this.currentIn = this.nextIn = 0;
	}

	public Boolean incPoweredCommand() {
		Boolean wasHigh = isHigh();
		poweredCommands++;
		return wasHigh != isHigh();
	}

	public Boolean decPoweredCommand() {
		Boolean wasHigh = isHigh();
		poweredCommands--;
		return wasHigh != isHigh();
	}

	public Boolean power(BlockPos neighborPos, int powerLevel) {
		Boolean wasHigh = isHigh();
		if (powerLevel > 8) {
			poweredNeighbors.add(neighborPos);
		} else {
			poweredNeighbors.remove(neighborPos);
		}
		return wasHigh != isHigh();
	}

	public Boolean isHigh() {
		return enables.size() > 0 || poweredNeighbors.size() > 0 || poweredCommands > 0; 
	}

    public Boolean setCoop(BlockPos pos) {
		coopNodes.add(pos);
		return isHigh();
	}

	public void unsetCoop(BlockPos pos) {
		coopNodes.remove(pos);
	}

	public void addAllNodes(Set<BlockPos> theNodes) {
		theNodes.addAll(allNodes);
	}

	public void addCoopNodes(Set<BlockPos> theNodes) {
		theNodes.addAll(coopNodes);
	}
}