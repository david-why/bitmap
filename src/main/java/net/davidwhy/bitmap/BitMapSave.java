package net.davidwhy.bitmap;

import java.io.*;
import java.nio.file.Path;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.davidwhy.bitmap.logic.*;

public class BitMapSave {
    private final Path configFile;

    public BitMapSave(MinecraftServer server) {
        configFile = server.getSavePath(WorldSavePath.ROOT).resolve("bitmap.save");
        try {
            FileInputStream fis = new FileInputStream(configFile.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis);
            Semiconductor.readObject(ois);
            ois.close();
            fis.close();
        } catch (Exception e) {
        }
    }

    public void stop() {
        try {
            FileOutputStream fos = new FileOutputStream(configFile.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            Semiconductor.writeObject(oos);
            oos.close();
            fos.close();
        } catch (Exception e) {
        }
    }
}
