package net.davidwhy.bitmap.logic;

import java.util.Set;
import net.minecraft.util.math.BlockPos;

public class SemiconductorWire {
    public Set<BlockPos> allNodes;
	public Set<BlockPos> coopNodes;
	public Set<SemiconductorWire> enables;
	public Set<BlockPos> poweredNeighbors;
	public int poweredCommands;
	public int currentIn;
	public int nextIn;
}