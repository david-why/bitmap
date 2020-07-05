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
import net.davidwhy.bitmap.logic.Semiconductor;

public class SemiconductorBlock extends Block {

    private static final IntProperty ON;

    public SemiconductorBlock() {
        super(FabricBlockSettings.copy(Blocks.STONE));
        AttackBlockCallback.EVENT.register(this::attackCallback);
        UseBlockCallback.EVENT.register(this::useCallback);
    }

    private ActionResult useCallback(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
        Item itemInHand = player.getStackInHand(hand).getItem();
        BlockPos pos = hit.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof SemiconductorBlock) || player.isSpectator())
            return ActionResult.PASS;
        if (world.isClient)
            return ActionResult.SUCCESS;
        if (itemInHand == Items.GOLDEN_SWORD) {
            int on = (Integer) state.get(ON);
            checkMachine(world, pos);
            if (on == 0) {
                on = Semiconductor.setCoopBlock(pos) ? 2 : 1;
            } else {
                Semiconductor.unsetCoopBlock(pos);
                on = 0;
            }
            world.setBlockState(pos, (BlockState) state.with(ON, on), 3);
            return ActionResult.SUCCESS;
        } else if (itemInHand == Items.STONE_SWORD) {
            player.sendMessage(new TranslatableText("message.speed", Semiconductor.speedUp()));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ON);
    }

    private ActionResult attackCallback(PlayerEntity player, World world, Hand hand, BlockPos pos,
            Direction direction) {
        if (world.isClient || player.isSpectator())
            return ActionResult.PASS;
        Item itemInHand = player.getStackInHand(hand).getItem();
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof SemiconductorBlock))
            return ActionResult.PASS;
        if (itemInHand == Items.GOLDEN_SWORD) {
            checkMachine(world, pos);
            if ((Integer) state.get(ON) == 0)
                return ActionResult.PASS;
            Semiconductor.powerBlock(pos, 30);
            world.setBlockState(pos, (BlockState) state.with(ON, 2), 3);
            return ActionResult.SUCCESS;
        } else if (itemInHand == Items.STONE_SWORD) {
            player.sendMessage(new TranslatableText("message.speed", Semiconductor.speedDown()));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        return (Integer) state.get(ON) == 2 ? 8 : 0;
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos,
            boolean moved) {
        if (world.isClient)
            return;

        if (world.getReceivedRedstonePower(pos) > 8) {
            if ((Integer) state.get(ON) == 1)
                world.setBlockState(pos, (BlockState) state.with(ON, 2), 3);
        } else {
            if ((Integer) state.get(ON) == 2)
                world.setBlockState(pos, (BlockState) state.with(ON, 1), 3);
        }
    }

    public boolean emitsRedstonePower(BlockState state) {
        return (Integer) state.get(ON) == 2;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        super.onBlockAdded(state, world, pos, oldState, moved);
        if (world.isClient || oldState.getBlock() instanceof SemiconductorBlock)
            return;
        Semiconductor.releaseMachine(pos);
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onBlockRemoved(state, world, pos, newState, moved);
        if (world.isClient || newState.getBlock() instanceof SemiconductorBlock)
            return;
        Semiconductor.releaseMachine(pos);
    }

    private void checkMachine(World world, BlockPos pos) {
        ;
    }

    static {
        ON = IntProperty.of("on", 0, 2);
    }

}
