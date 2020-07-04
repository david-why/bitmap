package net.davidwhy.bitmap.logic;

import java.util.Set;
import net.minecraft.util.math.BlockPos;

public class SemiConductorLine {
    private Set<BlockPos> nodes;
	private Set<BlockPos> coopNodes;
	private Set<SemiConductorLine> enables;
	private Boolean isHigh;
	private Set<BlockPos> poweredNeighbors;
	private int poweredCommands;
}