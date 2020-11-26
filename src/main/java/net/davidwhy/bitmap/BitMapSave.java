package net.davidwhy.bitmap;

import java.io.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.davidwhy.bitmap.logic.*;

public class BitMapSave {
    private static final Logger logger = LogManager.getLogger("BitMap");
    private MinecraftServer theServer;

    public BitMapSave(MinecraftServer server) {
        try {
            theServer = server;
            File file = new File(theServer.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDir(), "bitmap.save");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Semiconductor.readObject(reader);
            reader.close();
            logger.info("bitmap.save loaded.");
        } catch (Exception e) {
            logger.warn("bitmap.save load failed.");
            Semiconductor.clear();
        }
    }

    public void save() {
        try {
            File file = new File(theServer.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDir(), "bitmap.save");
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            Semiconductor.writeObject(writer);
            writer.close();
            logger.info("bitmap.save saved.");
        } catch (Exception e) {
            logger.warn("bitmap.save save failed.");
        }
    }
}
