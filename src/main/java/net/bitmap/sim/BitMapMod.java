package net.bitmap.sim;

import net.bitmap.sim.block.ConductBlock;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BitMapMod implements ModInitializer {

    public static final Block CONDUCT = new ConductBlock();

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier("bitmap", "block_conduct_block"), CONDUCT);
        Registry.register(Registry.ITEM, new Identifier("bitmap", "item_conduct_block"), new BlockItem(CONDUCT, new Item.Settings().group(ItemGroup.REDSTONE).maxCount(64)));
    }

}
