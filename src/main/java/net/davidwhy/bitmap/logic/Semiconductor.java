package net.davidwhy.bitmap.logic;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import net.minecraft.util.math.BlockPos;

public class Semiconductor {

    private static int speed = 2;

    public static int speedUp() {
        if (speed == 2)
            speed = 5;
        else if (speed < 655360)
            speed *= 2;
        return speed;
    }

    public static int speedDown() {
        if (speed == 5)
            speed = 2;
        else if (speed > 1)
            speed /= 2;
        return speed;
    }

    private static Map<BlockPos, SemiconductorMachine> nodes = new HashMap<BlockPos, SemiconductorMachine>();
    private static List<SemiconductorMachine> machines = new ArrayList<SemiconductorMachine>();

    // 生成机器，参数是所有方块集合和互操作型方块集合
    // 生成机器时，会先释放每个方块对应的所有旧机器
    public static int createMachine(Set<BlockPos> allNodes, Set<BlockPos> coopNodes) {
        allNodes.forEach((BlockPos pos) -> {
            releaseMachine(pos);
        });
        SemiconductorMachine machine = new SemiconductorMachine();
        allNodes.forEach((BlockPos pos) -> {
            nodes.put(pos, machine);
        });
        machines.add(machine);
        return machine.create(allNodes, coopNodes);
    }

    // 释放机器（包括相邻18个方块对应的机器），返回释放的方块数，如果返回0表示释放失败
    // 放置或消除方块时，应该先释放对应的机器
    public static int releaseMachine(BlockPos pos) {
        if (!nodes.containsKey(pos))
            return 0;
        System.out.println("Released machine");
        return 0;
    }

    // 判断方块是否有对应的机器
    // 如果没有，应该收集所有关联的方块，然后创建机器
    public static Boolean inMachine(BlockPos pos) {
        return nodes.containsKey(pos);
    }

    // 将方块改成互操作型，返回当前电平
    public static Boolean setCoopBlock(BlockPos pos) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null)
            return false;
        return machine.setCoop(pos);
    }

    // 将方块改成非互操作型
    public static void unsetCoopBlock(BlockPos pos) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null)
            return;
        machine.unsetCoop(pos);
    }

    // 邻居红石方块供能，强制高电平。powerLevel如果是0表示停止供能
    public static void powerBlock(BlockPos pos, BlockPos poweredNeighbor, int powerLevel) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null)
            return;
        machine.power(pos, poweredNeighbor, powerLevel);
    }

    // ATTACK供能，强制高电平一段时间
    public static void powerBlock(BlockPos pos, int keepTicks) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null)
            return;
        machine.power(pos, absTick + keepTicks);
    }

    private static long absTick = 0;

    // tick回调。返回变成低电平或高电平的互操作型方块
    public static void tick(Set<BlockPos> lowNodes, Set<BlockPos> highNodes) {
        absTick++;
        machines.forEach((SemiconductorMachine machine) -> {
            machine.tick(absTick);
        });
        machines.forEach((SemiconductorMachine machine) -> {
            machine.run(absTick, 1, lowNodes, highNodes);
        });
    }
}