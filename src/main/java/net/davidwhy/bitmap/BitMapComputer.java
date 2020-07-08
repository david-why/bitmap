package net.davidwhy.bitmap;

import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class BitMapComputer {

	public static int absX = 0;
	public static int absY = 10;
	public static int absZ = 0;
    public static int dataPos = -1;

    private static int fetchData() {
        if (dataPos >= 0) {
            int i = data.indexOf(',', dataPos);
            if (i < 0) {
                dataPos = -1;
            } else {
                int j = Integer.parseInt(BitMapComputer.data.substring(dataPos, i));
                dataPos = i + 1;
                return j;
            }
        }
        return -9999;
    }

    public static void genComputer(ServerWorld world) {
        for (int a = 0; a < 100; a++) {
            int x = fetchData();
            int z = fetchData();
            int c = fetchData();
            if (x == -9999 || z == -9999 || c == -9999)
                return;
            world.setBlockState(new BlockPos(absX + x, absY, absZ + z), blocks[c].getDefaultState());
        }
    }

    private static Block[] blocks = { BitMapMod.BLACK_BLOCK, BitMapMod.RED_BLOCK, BitMapMod.YELLOW_BLOCK,
            BitMapMod.GREEN_BLOCK, BitMapMod.CYAN_BLOCK, BitMapMod.BLUE_BLOCK, BitMapMod.PINK_BLOCK,
            BitMapMod.WHITE_BLOCK, };


}