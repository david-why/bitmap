package net.davidwhy.bitmap;

import java.io.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.davidwhy.bitmap.logic.Semiconductor;

public class BitMapSave {

    public static void load(MinecraftServer server) {
        try {
            File file = server.getSavePath(WorldSavePath.ROOT).resolve("bitmap.save").toFile();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Semiconductor.readObject(reader);
            reader.close();
            BitMapMod.LOGGER.info("bitmap.save loaded.");
        } catch (Exception e) {
            BitMapMod.LOGGER.warn("bitmap.save load failed.");
            Semiconductor.clear();
        }
    }

    public static void save(MinecraftServer server) {
        try {
            File file = server.getSavePath(WorldSavePath.ROOT).resolve("bitmap.save").toFile();
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            Semiconductor.writeObject(writer);
            writer.close();
            BitMapMod.LOGGER.info("bitmap.save saved.");
        } catch (Exception e) {
            BitMapMod.LOGGER.warn("bitmap.save save failed.");
        }
    }
}
