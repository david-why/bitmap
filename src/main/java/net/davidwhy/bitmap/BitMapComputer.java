package net.davidwhy.bitmap;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class BitMapComputer {

    private int absX = 0;
    private int absY = 10;
    private int absZ = 0;
    private int dataPos = -1;
    private List<Integer> data = new ArrayList<Integer>();

    public BitMapComputer() {
        try {
            InputStream input = getClass().getResourceAsStream("/data/bitmap/bmp_computer.png");
            BufferedImage image = ImageIO.read(input);
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    int color = image.getRGB(x, y) & 0x00ffffff;
                    if (color != 0) {
                        int r = (color & 0x00ff0000) >> 16;
                        int g = (color & 0x0000ff00) >> 8;
                        int b = (color & 0x000000ff);
                        int c = 0;
                        int d = 0x00ffffff;
                        for (int i = 0; i < blockColors.length; i++) {
                            int e = (blockColors[i][0] - r) * (blockColors[i][0] - r)
                                    + (blockColors[i][1] - g) * (blockColors[i][1] - g)
                                    + (blockColors[i][2] - b) * (blockColors[i][2] - b);
                            if (e < d) {
                                d = e;
                                c = i;
                            }
                        }
                        if (c > 0) {
                            if (r < 192 && g < 192 && b < 192) {
                                c += 8;
                            }
                            data.add(x);
                            data.add(y);
                            data.add(c);
                        }
                    }
                }
            }
            BitMapMod.LOGGER.info("bmp_computer.png loaded, " + Integer.toString(data.size() / 3) + " dots.");
        } catch (Exception e) {
            BitMapMod.LOGGER.warn("bmp_computer.png load failed.");
            data.clear();
        }
    }

    private int fetchData() {
        if (dataPos >= 0 && dataPos < data.size()) {
            return data.get(dataPos++);
        }
        return -9999;
    }

    public boolean startGen(int x, int y, int z) {
        if (dataPos < 0 || dataPos >= data.size()) {
            absX = x;
            absY = y;
            absZ = z;
            dataPos = 0;
            return true;
        }
        return false;
    }

    public void genComputer(ServerWorld world) {
        for (int a = 0; a < 100; a++) {
            int x = fetchData();
            if (x == -9999)
                return;
            int z = fetchData();
            if (z == -9999)
                return;
            int c = fetchData();
            if (c == -9999)
                return;
            world.setBlockState(new BlockPos(absX + x, absY, absZ + z), blocks[c].getDefaultState());
        }
    }

    private static Block[] blocks = { BitMapMod.BLACK_BLOCK, BitMapMod.RED_BLOCK, BitMapMod.YELLOW_BLOCK,
            BitMapMod.GREEN_BLOCK, BitMapMod.CYAN_BLOCK, BitMapMod.BLUE_BLOCK, BitMapMod.PINK_BLOCK,
            BitMapMod.WHITE_BLOCK, Blocks.BLACK_CONCRETE, Blocks.RED_CONCRETE, Blocks.YELLOW_CONCRETE,
            Blocks.LIME_CONCRETE, Blocks.CYAN_CONCRETE, Blocks.BLUE_CONCRETE, Blocks.PINK_CONCRETE,
            Blocks.WHITE_CONCRETE };

    private static int[][] blockColors = { { 0, 0, 0 }, { 255, 0, 0 }, { 255, 255, 0 }, { 0, 255, 0 }, { 0, 255, 255 },
            { 0, 0, 255 }, { 255, 0, 255 }, { 255, 255, 255 } };

}