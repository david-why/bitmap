package net.davidwhy.bitmap.logic;

import java.util.Set;
import net.minecraft.util.math.BlockPos;

public class SemiConductorWire {
    private Set<BlockPos> nodes;
	private Set<BlockPos> coopNodes;
	private Set<SemiConductorWire> enables;
	private Boolean isHigh;
	private Set<BlockPos> poweredNeighbors;
	private int poweredCommands;
	private int currentIn;
	private int nextIn;
}