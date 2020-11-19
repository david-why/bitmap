package net.davidwhy.bitmap;

import java.io.*;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.davidwhy.bitmap.logic.*;

public class BitMapSave {
    private final Path configFile;
    private static final Logger logger = LogManager.getLogger("BitMap");

    public BitMapSave(MinecraftServer server) {
        configFile = server.getSavePath(WorldSavePath.ROOT).resolve("bitmap.save");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile.toFile()));
            Semiconductor.readObject(reader);
            reader.close();
            logger.info("bitmap.save loaded.");
        } catch (Exception e) {
            logger.warn("bitmap.save load failed.");
        }
    }

    public void save() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(configFile.toFile()));
            Semiconductor.writeObject(writer);
            writer.close();
            logger.info("bitmap.save saved.");
        } catch (Exception e) {
            logger.warn("bitmap.save save failed.");
        }
    }
}
