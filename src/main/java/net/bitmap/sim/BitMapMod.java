package net.bitmap.sim;

import net.bitmap.sim.block.ConductBlock;
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

    public static final Block CONDUCT1 = new ConductBlock(FabricBlockSettings.copy(Blocks.STONE));
    public static final Block CONDUCT2 = new ConductBlock(FabricBlockSettings.copy(Blocks.STONE));
    public static final Block CONDUCT3 = new ConductBlock(FabricBlockSettings.copy(Blocks.STONE));
    public static final Block CONDUCT4 = new ConductBlock(FabricBlockSettings.copy(Blocks.STONE));

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier("bitmap", "block_conduct_blocky"), CONDUCT1);
        Registry.register(Registry.BLOCK, new Identifier("bitmap", "block_conduct_blockb"), CONDUCT2);
        Registry.register(Registry.BLOCK, new Identifier("bitmap", "block_conduct_blockg"), CONDUCT3);
        Registry.register(Registry.BLOCK, new Identifier("bitmap", "block_conduct_blockr"), CONDUCT4);
        Registry.register(Registry.ITEM, new Identifier("bitmap", "item_conduct_blocky"), new BlockItem(CONDUCT1, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
        Registry.register(Registry.ITEM, new Identifier("bitmap", "item_conduct_blockb"), new BlockItem(CONDUCT2, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
        Registry.register(Registry.ITEM, new Identifier("bitmap", "item_conduct_blockg"), new BlockItem(CONDUCT3, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
        Registry.register(Registry.ITEM, new Identifier("bitmap", "item_conduct_blockr"), new BlockItem(CONDUCT4, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
    }

}
