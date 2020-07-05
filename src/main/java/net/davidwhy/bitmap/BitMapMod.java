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
    public static final Block YELLOW_SEMICONDUCTOR_BLOCK = new SemiconductorBlock();
    public static final Block RED_SEMICONDUCTOR_BLOCK = new SemiconductorBlock();
    public static final Block GREEN_SEMICONDUCTOR_BLOCK = new SemiconductorBlock();
    public static final Block BLUE_SEMICONDUCTOR_BLOCK = new SemiconductorBlock();

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier(MODID, "yellow_semiconductor_block"),
                YELLOW_SEMICONDUCTOR_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "red_semiconductor_block"), 
                RED_SEMICONDUCTOR_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "green_semiconductor_block"),
                GREEN_SEMICONDUCTOR_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier(MODID, "blue_semiconductor_block"),
                BLUE_SEMICONDUCTOR_BLOCK);
        Registry.register(Registry.ITEM, new Identifier(MODID, "yellow_semiconductor_block"),
                new BlockItem(YELLOW_SEMICONDUCTOR_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
        Registry.register(Registry.ITEM, new Identifier(MODID, "red_semiconductor_block"),
                new BlockItem(RED_SEMICONDUCTOR_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
        Registry.register(Registry.ITEM, new Identifier(MODID, "green_semiconductor_block"),
                new BlockItem(GREEN_SEMICONDUCTOR_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
        Registry.register(Registry.ITEM, new Identifier(MODID, "blue_semiconductor_block"),
                new BlockItem(BLUE_SEMICONDUCTOR_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
    }

}
