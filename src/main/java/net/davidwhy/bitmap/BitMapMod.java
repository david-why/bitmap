package net.davidwhy.bitmap;

import net.davidwhy.bitmap.block.SemiConductorBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BitMapMod implements ModInitializer {

    public static final Block YELLOW_SEMICONDUCTOR_BLOCK = new SemiConductorBlock(FabricBlockSettings.copy(Blocks.STONE));
    public static final Block RED_SEMICONDUCTOR_BLOCK = new SemiConductorBlock(FabricBlockSettings.copy(Blocks.STONE));
    public static final Block GREEN_SEMICONDUCTOR_BLOCK = new SemiConductorBlock(FabricBlockSettings.copy(Blocks.STONE));
    public static final Block BLUE_SEMICONDUCTOR_BLOCK = new SemiConductorBlock(FabricBlockSettings.copy(Blocks.STONE));

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier("bitmap", "yellow_semiconductor_block"), YELLOW_SEMICONDUCTOR_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier("bitmap", "red_semiconductor_block"), RED_SEMICONDUCTOR_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier("bitmap", "green_semiconductor_block"), GREEN_SEMICONDUCTOR_BLOCK);
        Registry.register(Registry.BLOCK, new Identifier("bitmap", "blue_semiconductor_block"), BLUE_SEMICONDUCTOR_BLOCK);
        Registry.register(Registry.ITEM, new Identifier("bitmap", "yellow_semiconductor_block"), new BlockItem(YELLOW_SEMICONDUCTOR_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
        Registry.register(Registry.ITEM, new Identifier("bitmap", "red_semiconductor_block"), new BlockItem(RED_SEMICONDUCTOR_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
        Registry.register(Registry.ITEM, new Identifier("bitmap", "green_semiconductor_block"), new BlockItem(GREEN_SEMICONDUCTOR_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
        Registry.register(Registry.ITEM, new Identifier("bitmap", "blue_semiconductor_block"), new BlockItem(BLUE_SEMICONDUCTOR_BLOCK, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
    }

}
