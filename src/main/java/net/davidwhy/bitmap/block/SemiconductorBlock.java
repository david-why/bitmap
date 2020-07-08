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
import net.minecraft.world.dimension.DimensionType;
import net.davidwhy.bitmap.logic.Semiconductor;

public class SemiconductorBlock extends Block {

    public static final IntProperty ON;

    public SemiconductorBlock(int luminance) {
        super(FabricBlockSettings.copyOf(Blocks.STONE).lightLevel(luminance));
        AttackBlockCallback.EVENT.register(this::attackCallback);
        UseBlockCallback.EVENT.register(this::useCallback);
    }

    private ActionResult useCallback(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
        if (world.dimension.getType() != DimensionType.OVERWORLD || player.isSpectator()) {
            return ActionResult.PASS;
        }
        Item itemInHand = player.getStackInHand(hand).getItem();
        BlockPos pos = hit.getBlockPos();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (!(block instanceof SemiconductorBlock) || player.isSpectator()) {
            return ActionResult.PASS;
        }
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        if (itemInHand == Items.GOLDEN_SWORD) {
            int on = (Integer) state.get(ON);
            checkMachine(player, world, pos);
            if (on == 0) {
                on = Semiconductor.setCoopBlock(b2i(pos)) ? 2 : 1;
            } else {
                Semiconductor.unsetCoopBlock(b2i(pos));
                on = 0;
            }
            world.setBlockState(pos, (BlockState) state.with(ON, on), 3);
            return ActionResult.SUCCESS;
        } else if (itemInHand == Items.IRON_SWORD) {
            player.sendMessage(new TranslatableText("message.bitmap.speed", Semiconductor.speedUp()));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private ActionResult attackCallback(PlayerEntity player, World world, Hand hand, BlockPos pos,
            Direction direction) {
        if (world.isClient || world.dimension.getType() != DimensionType.OVERWORLD || player.isSpectator()) {
            return ActionResult.PASS;
        }
        Item itemInHand = player.getStackInHand(hand).getItem();
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof SemiconductorBlock)) {
            return ActionResult.PASS;
        }
        if (itemInHand == Items.GOLDEN_SWORD) {
            checkMachine(player, world, pos);
            if ((Integer) state.get(ON) == 0) {
                return ActionResult.PASS;
            }
            Semiconductor.powerBlock(b2i(pos));
            return ActionResult.SUCCESS;
        } else if (itemInHand == Items.IRON_SWORD) {
            player.sendMessage(new TranslatableText("message.bitmap.speed", Semiconductor.speedDown()));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ON);
    }

    public int getLuminance(BlockState state) {
        return (Integer) state.get(ON) == 2 ? super.getLuminance(state) : 0;
    }

    public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction facing) {
        return (Integer) state.get(ON) == 2 ? 8 : 0;
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos neighborPos,
            boolean moved) {
        if (world.isClient || world.dimension.getType() != DimensionType.OVERWORLD) {
            return;
        }
        if (block instanceof SemiconductorBlock) {
            return;
        }
        int powerLevel = world.getReceivedRedstonePower(pos);
        Semiconductor.powerBlock(b2i(pos), powerLevel > 8);
        if (powerLevel > 8 && (Integer) state.get(ON) == 1) {
            world.setBlockState(pos, (BlockState) state.with(ON, 2), 3);
        }
    }

    public boolean emitsRedstonePower(BlockState state) {
        return (Integer) state.get(ON) == 2;
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        super.onBlockAdded(state, world, pos, oldState, moved);
        if (world.isClient || world.dimension.getType() != DimensionType.OVERWORLD
                || oldState.getBlock() instanceof SemiconductorBlock) {
            return;
        }
        releaseMachine(world, pos);
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onBlockRemoved(state, world, pos, newState, moved);
        if (world.isClient || world.dimension.getType() != DimensionType.OVERWORLD
                || newState.getBlock() instanceof SemiconductorBlock) {
            return;
        }
        releaseMachine(world, pos);
    }

    private void releaseMachine(World world, BlockPos pos) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    BlockPos u = pos.add(x, y, z);
                    Set<Long> coopNodes = Semiconductor.releaseMachine(b2i(u));
                    if (coopNodes != null) {
                        coopNodes.forEach((Long a) -> {
                            BlockPos t = i2b(a);
                            BlockState state = world.getBlockState(t);
                            if (state.getBlock() instanceof SemiconductorBlock) {
                                world.setBlockState(t, (BlockState) state.with(ON, 1), 3);
                            }
                        });
                    }
                }
            }
        }
    }

    private void checkMachine(PlayerEntity player, World world, BlockPos pos) {
        if (Semiconductor.inMachine(b2i(pos))) {
            return;
        }
        Set<Long> allNodes = new HashSet<Long>();
        Set<Long> coopNodes = new HashSet<Long>();
        Set<Long> poweredNodes = new HashSet<Long>();
        Set<BlockPos> badNodes = new HashSet<BlockPos>();
        Set<BlockPos> tmpNodes = new HashSet<BlockPos>();
        tmpNodes.add(pos);
        while (tmpNodes.size() > 0) {
            Set<BlockPos> tmpNodes2 = new HashSet<BlockPos>();
            tmpNodes.forEach((BlockPos t) -> {
                BlockState tstate = world.getBlockState(t);
                if (tstate.getBlock() instanceof SemiconductorBlock) {
                    if (!allNodes.contains(b2i(t))) {
                        allNodes.add(b2i(t));
                        if (allNodes.size() % 10000 == 0) {
                            player.sendMessage(new TranslatableText("message.bitmap.parsed", allNodes.size()));
                        }
                        if (tstate.get(ON) > 0) {
                            coopNodes.add(b2i(t));
                        }
                        if (world.getReceivedRedstonePower(t) > 8) {
                            poweredNodes.add(b2i(t));
                        }
                        for (int x = -1; x <= 1; x++) {
                            for (int y = -1; y <= 1; y++) {
                                for (int z = -1; z <= 1; z++) {
                                    if (x == 0 && y == 0 && z == 0)
                                        continue;
                                    BlockPos u = t.add(x, y, z);
                                    if (!allNodes.contains(b2i(u)) && !badNodes.contains(u))
                                        tmpNodes2.add(u);
                                }
                            }
                        }
                    }
                } else {
                    badNodes.add(t);
                }
            });
            tmpNodes = tmpNodes2;
        }

        int retc = Semiconductor.createMachine(allNodes, coopNodes, poweredNodes);
        if (retc > 0) {
            player.sendMessage(new TranslatableText("message.bitmap.create", retc));
        }
    }

    public static long b2i(BlockPos pos) {
        return 0x10000000000L * (0x80000 + pos.getX()) + 0x100000L * (0x80000 + pos.getY()) + (0x80000 + pos.getZ());
    }

    public static BlockPos i2b(long a) {
        return new BlockPos((0xfffff & (a >> 40)) - 0x80000, (0xfffff & (a >> 20)) - 0x80000, (0xfffff & a) - 0x80000);
    }

    static {
        ON = IntProperty.of("on", 0, 2);
    }

}
