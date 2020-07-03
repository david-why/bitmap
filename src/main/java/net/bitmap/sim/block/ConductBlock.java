package net.bitmap.sim.block;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
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
// import net.minecraft.block.RedstoneLampBlock;  // Used for programming
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ConductBlock extends Block {

    private static final IntProperty COLOR;
    private static final IntProperty ON;
    private static Integer speed = 2;
    private static long lastAttack = 0;

    public ConductBlock(Settings settings) {
        super(settings);
        AttackBlockCallback.EVENT.register(this::attackCallback);
    }
    public ConductBlock() {
        super(FabricBlockSettings.copy(Blocks.STONE));
        AttackBlockCallback.EVENT.register(this::attackCallback);
    }

    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Item itemInHand = player.getStackInHand(hand).getItem();
        if (itemInHand == Items.GOLDEN_SWORD) {
            Integer onNow = (Integer)state.get(ON);
            if (onNow == 0 || onNow == 2) world.setBlockState(pos, (BlockState)state.cycle(ON), 3);
            else world.setBlockState(pos, (BlockState)state.cycle(ON).cycle(ON), 3);
        } else if (itemInHand == Items.IRON_SWORD) {
            world.setBlockState(pos, (BlockState)state.cycle(COLOR), 3);
        } else if (itemInHand == Items.STONE_SWORD) {
            if (speed == 2) speed = 5;
            else {
                if (speed < 655360) {
                    speed *= 2;
                }
                else return false;
            }
            player.sendMessage(new TranslatableText("message.speed." + speed.toString()));
        } else return false;
        return true;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COLOR);
        builder.add(ON);
    }

    private ActionResult attackCallback(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction direction) {
        Item itemInHand = player.getStackInHand(hand).getItem();
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof ConductBlock) || System.currentTimeMillis() - 500 < lastAttack) return ActionResult.PASS;
        if (itemInHand == Items.GOLDEN_SWORD) {
            Integer onNow = (Integer)state.get(ON);
            if (onNow == 1) world.setBlockState(pos, (BlockState)state.cycle(ON), 3);
            else {
                if (onNow == 2) world.setBlockState(pos, (BlockState)state.cycle(ON).cycle(ON), 3);
                else return ActionResult.PASS;
            }
        } else if (itemInHand == Items.STONE_SWORD) {
            if (speed == 5) speed = 2;
            else {
                if (speed > 1) {
                    speed /= 2;
                }
                else return ActionResult.PASS;
            }
            player.sendMessage(new TranslatableText("message.speed." + speed.toString()));
        } else return ActionResult.PASS;
        lastAttack = System.currentTimeMillis();
        return ActionResult.SUCCESS;
    }

    static {
        COLOR = IntProperty.of("color", 0, 3);
        ON = IntProperty.of("on", 0, 2);
    }

}