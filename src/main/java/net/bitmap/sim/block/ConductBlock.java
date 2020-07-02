package net.bitmap.sim.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
// import net.minecraft.block.RedstoneLampBlock;  // Used for programming
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ConductBlock extends Block {

    private static final IntProperty COLOR;
    private static final BooleanProperty REDSTONE;

    public ConductBlock(Settings settings) {
        super(settings);
    }
    public ConductBlock() {
        super(FabricBlockSettings.copy(Blocks.STONE));
    }
    
    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Item itemInHand = player.getStackInHand(hand).getItem();
        if (itemInHand == Items.REDSTONE) {
            world.setBlockState(pos, (BlockState)state.cycle(COLOR), 3);
        } else if (itemInHand == Items.STONE_SWORD) {
            world.setBlockState(pos, (BlockState)state.cycle(REDSTONE), 3);
        }
        return true;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COLOR);
        builder.add(REDSTONE);
    }

    static {
        COLOR = IntProperty.of("color", 0, 5);
        REDSTONE = BooleanProperty.of("redstone_compat");
    }

}