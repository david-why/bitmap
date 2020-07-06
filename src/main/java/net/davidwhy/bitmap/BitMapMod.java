package net.davidwhy.bitmap;

import net.davidwhy.bitmap.block.SemiconductorBlock;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class BitMapMod implements ModInitializer {

        public static final String MODID = "bitmap";
        public static final Block YELLOW_BLOCK = new SemiconductorBlock(0);
        public static final Block RED_BLOCK = new SemiconductorBlock(0);
        public static final Block GREEN_BLOCK = new SemiconductorBlock(0);
        public static final Block BLUE_BLOCK = new SemiconductorBlock(0);
        public static final BitMapTick TICK = new BitMapTick();

        @Override
        public void onInitialize() {
                Registry.register(Registry.BLOCK, new Identifier(MODID, "yellow_semiconductor_block"), YELLOW_BLOCK);
                Registry.register(Registry.BLOCK, new Identifier(MODID, "red_semiconductor_block"), RED_BLOCK);
                Registry.register(Registry.BLOCK, new Identifier(MODID, "green_semiconductor_block"), GREEN_BLOCK);
                Registry.register(Registry.BLOCK, new Identifier(MODID, "blue_semiconductor_block"), BLUE_BLOCK);
                Registry.register(Registry.ITEM, new Identifier(MODID, "yellow_semiconductor_block"), 
                                new BlockItem(YELLOW_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
                Registry.register(Registry.ITEM, new Identifier(MODID, "red_semiconductor_block"),
                                new BlockItem(RED_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
                Registry.register(Registry.ITEM, new Identifier(MODID, "green_semiconductor_block"),
                                new BlockItem(GREEN_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
                Registry.register(Registry.ITEM, new Identifier(MODID, "blue_semiconductor_block"),
                                new BlockItem(BLUE_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
        }

}

