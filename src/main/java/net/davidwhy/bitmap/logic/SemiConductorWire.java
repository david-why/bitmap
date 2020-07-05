package net.davidwhy.bitmap.logic;

import java.util.Set;
import net.minecraft.util.math.BlockPos;

public class SemiconductorWire {
    private Set<BlockPos> nodes;
	private Set<BlockPos> coopNodes;
	private Set<SemiconductorWire> enables;
	private Boolean isHigh;
	private Set<BlockPos> poweredNeighbors;
	private int poweredCommands;
	private int currentIn;
	private int nextIn;
}