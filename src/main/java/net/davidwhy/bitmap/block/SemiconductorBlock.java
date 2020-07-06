package net.davidwhy.bitmap.block;

import java.util.HashSet;
import java.util.Set;
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

    public static final IntProperty ON;
    public static World world = null;

    public SemiconductorBlock(int luminance) {
        super(FabricBlockSettings.copyOf(Blocks.STONE).lightLevel(luminance));
        AttackBlockCallback.EVENT.register(this::attackCallback);
        UseBlockCallback.EVENT.register(this::useCallback);
    }

    private ActionResult useCallback(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
        Item itemInHand = player.getStackInHand(hand).getItem();
        BlockPos pos = hit.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (!(block instanceof SemiconductorBlock) || player.isSpectator())
            return ActionResult.PASS;
        SemiconductorBlock.world = world;
        if (world.isClient)
            return ActionResult.SUCCESS;
        if (itemInHand == Items.GOLDEN_SWORD) {
            int on = (Integer) state.get(ON);
            checkMachine(player, world, pos);
            if (on == 0) {
                on = Semiconductor.setCoopBlock(pos) ? 2 : 1;
            } else {
                Semiconductor.unsetCoopBlock(pos);
                on = 0;
            }
            world.setBlockState(pos, (BlockState) state.with(ON, on), 3);
            return ActionResult.SUCCESS;
        } else if (itemInHand == Items.STONE_SWORD) {
            player.sendMessage(new TranslatableText("message.bitmap.speed", Semiconductor.speedUp()));
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
        SemiconductorBlock.world = world;
        if (itemInHand == Items.GOLDEN_SWORD) {
            checkMachine(player, world, pos);
            if ((Integer) state.get(ON) == 0)
                return ActionResult.PASS;
            Semiconductor.powerBlock(pos);
            return ActionResult.SUCCESS;
        } else if (itemInHand == Items.STONE_SWORD) {
            player.sendMessage(new TranslatableText("message.bitmap.speed", Semiconductor.speedDown()));
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

        int powerLevel = world.getReceivedRedstonePower(pos);
        Semiconductor.powerBlock(pos, neighborPos, powerLevel);
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

    private void checkMachine(PlayerEntity player, World world, BlockPos pos) {
        if (Semiconductor.inMachine(pos))
            return;
        Set<BlockPos> allNodes = new HashSet<BlockPos>();
        Set<BlockPos> coopNodes = new HashSet<BlockPos>();
        Set<BlockPos> badNodes = new HashSet<BlockPos>();
        Set<BlockPos> tmpNodes = new HashSet<BlockPos>();
        tmpNodes.add(pos);
        while (tmpNodes.size() > 0) {
            Set<BlockPos> tmpNodes2 = new HashSet<BlockPos>();
            tmpNodes.forEach((BlockPos tpos) -> {
                BlockState tstate = world.getBlockState(tpos);
                if (tstate.getBlock() instanceof SemiconductorBlock) {
                    if (!allNodes.contains(tpos)) {
                        allNodes.add(tpos);
                        if (tstate.get(ON) > 0)
                            coopNodes.add(tpos);
                        for (int x = -1; x <= 1; x++)
                            for (int y = -1; y <= 1; y++)
                                for (int z = -1; z <= 1; z++) {
                                    if (x == 0 && y == 0 && z == 0)
                                        continue;
                                    BlockPos npos = tpos.add(x, y, z);
                                    if (!allNodes.contains(npos) && !badNodes.contains(npos))
                                        tmpNodes2.add(npos);
                                }
                    }
                } else {
                    badNodes.add(tpos);
                }
            });
            tmpNodes = tmpNodes2;
        }

        int retc = Semiconductor.createMachine(allNodes, coopNodes);
        if (retc > 0) {
            player.sendMessage(new TranslatableText("message.bitmap.create", retc));
        }
        ;
    }

    static {
        ON = IntProperty.of("on", 0, 2);
    }

}
