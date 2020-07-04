package net.davidwhy.bitmap.block;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.davidwhy.bitmap.logic.SemiConductor;

public class SemiConductorBlock extends Block {

    private static final IntProperty ON;

    public SemiConductorBlock() {
        super(FabricBlockSettings.copy(Blocks.STONE));
        AttackBlockCallback.EVENT.register(this::attackCallback);
        UseBlockCallback.EVENT.register(this::useCallback);
    }

    private ActionResult useCallback(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
        Item itemInHand = player.getStackInHand(hand).getItem();
        BlockPos pos = hit.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof SemiConductorBlock))
            return ActionResult.PASS;
        if (world.isClient)
            return ActionResult.SUCCESS;
        if (itemInHand == Items.GOLDEN_SWORD) {
            world.setBlockState(pos, (BlockState) state.with(ON, (Integer) state.get(ON) == 0 ? 1 : 0), 3);
            return ActionResult.SUCCESS;
        } else if (itemInHand == Items.STONE_SWORD) {
            player.sendMessage(new TranslatableText("message.speed", SemiConductor.speedUp()));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ON);
    }

    private ActionResult attackCallback(PlayerEntity player, World world, Hand hand, BlockPos pos,
            Direction direction) {
        if (world.isClient)
            return ActionResult.PASS;
        Item itemInHand = player.getStackInHand(hand).getItem();
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof SemiConductorBlock))
            return ActionResult.PASS;
        if (itemInHand == Items.GOLDEN_SWORD) {
            Integer onNow = (Integer) state.get(ON);
            if (onNow == 0)
                return ActionResult.PASS;
            world.setBlockState(pos, (BlockState) state.with(ON, onNow == 1 ? 2 : 1), 3);
            return ActionResult.SUCCESS;

        } else if (itemInHand == Items.STONE_SWORD) {
            player.sendMessage(new TranslatableText("message.speed", SemiConductor.speedDown()));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        return (Integer) state.get(ON) == 2 ? 8 : 0;
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos,
            boolean moved) {
        if (!world.isClient) {
            if (world.getReceivedRedstonePower(pos) > 8) {
                if ((Integer) state.get(ON) == 1)
                    world.setBlockState(pos, (BlockState) state.with(ON, 2), 3);
            } else {
                if ((Integer) state.get(ON) == 2)
                    world.setBlockState(pos, (BlockState) state.with(ON, 1), 3);
            }
        }
    }

    public boolean emitsRedstonePower(BlockState state) {
        System.out.println((Integer) state.get(ON));
        return (Integer) state.get(ON) == 2;
    }

    static {
        ON = IntProperty.of("on", 0, 2);
    }

}