package net.davidwhy.bitmap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BitMapMod implements ModInitializer {

        public static final String MODID = "bitmap";
        public static final Block BLACK_BLOCK = new SemiconductorBlock();
        public static final Block RED_BLOCK = new SemiconductorBlock();
        public static final Block YELLOW_BLOCK = new SemiconductorBlock();
        public static final Block GREEN_BLOCK = new SemiconductorBlock();
        public static final Block CYAN_BLOCK = new SemiconductorBlock();
        public static final Block BLUE_BLOCK = new SemiconductorBlock();
        public static final Block PINK_BLOCK = new SemiconductorBlock();
        public static final Block WHITE_BLOCK = new SemiconductorBlock();
        public static BitMapTick TICK;
        public static Logger LOGGER;
        public static BitMapComputer COMPUTER;

        @Override
        public void onInitialize() {
                Registry.register(Registries.BLOCK, new Identifier(MODID, "black_semiconductor_block"), BLACK_BLOCK);
                Registry.register(Registries.BLOCK, new Identifier(MODID, "red_semiconductor_block"), RED_BLOCK);
                Registry.register(Registries.BLOCK, new Identifier(MODID, "yellow_semiconductor_block"), YELLOW_BLOCK);
                Registry.register(Registries.BLOCK, new Identifier(MODID, "green_semiconductor_block"), GREEN_BLOCK);
                Registry.register(Registries.BLOCK, new Identifier(MODID, "cyan_semiconductor_block"), CYAN_BLOCK);
                Registry.register(Registries.BLOCK, new Identifier(MODID, "blue_semiconductor_block"), BLUE_BLOCK);
                Registry.register(Registries.BLOCK, new Identifier(MODID, "pink_semiconductor_block"), PINK_BLOCK);
                Registry.register(Registries.BLOCK, new Identifier(MODID, "white_semiconductor_block"), WHITE_BLOCK);

                Registry.register(Registries.ITEM, new Identifier(MODID, "black_semiconductor_block"),
                                new BlockItem(BLACK_BLOCK, new Item.Settings().maxCount(64)));
                Registry.register(Registries.ITEM, new Identifier(MODID, "red_semiconductor_block"),
                                new BlockItem(RED_BLOCK, new Item.Settings().maxCount(64)));
                Registry.register(Registries.ITEM, new Identifier(MODID, "yellow_semiconductor_block"),
                                new BlockItem(YELLOW_BLOCK, new Item.Settings().maxCount(64)));
                Registry.register(Registries.ITEM, new Identifier(MODID, "green_semiconductor_block"),
                                new BlockItem(GREEN_BLOCK, new Item.Settings().maxCount(64)));
                Registry.register(Registries.ITEM, new Identifier(MODID, "cyan_semiconductor_block"),
                                new BlockItem(CYAN_BLOCK, new Item.Settings().maxCount(64)));
                Registry.register(Registries.ITEM, new Identifier(MODID, "blue_semiconductor_block"),
                                new BlockItem(BLUE_BLOCK, new Item.Settings().maxCount(64)));
                Registry.register(Registries.ITEM, new Identifier(MODID, "pink_semiconductor_block"),
                                new BlockItem(PINK_BLOCK, new Item.Settings().maxCount(64)));
                Registry.register(Registries.ITEM, new Identifier(MODID, "white_semiconductor_block"),
                                new BlockItem(WHITE_BLOCK, new Item.Settings().maxCount(64)));

                LOGGER = LogManager.getLogger(MODID);
                TICK = new BitMapTick();
                COMPUTER = new BitMapComputer();

                ServerLifecycleEvents.SERVER_STARTED.register((server) -> BitMapSave.load(server));
                ServerLifecycleEvents.SERVER_STOPPED.register((server) -> BitMapSave.save(server));
        }

}
