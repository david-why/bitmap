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
        if (speed == 8)
            speed = 15;
        else if (speed < 900000)
            speed *= 2;
        return speed;
    }

    public static int speedDown() {
        if (speed == 15)
            speed = 8;
        else if (speed > 1)
            speed /= 2;
        return speed;
    }

    private static Map<BlockPos, SemiconductorMachine> nodes = new HashMap<BlockPos, SemiconductorMachine>();
    private static List<SemiconductorMachine> machines = new ArrayList<SemiconductorMachine>();

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

    public static int releaseMachine(BlockPos pos) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null)
            return 0;
        machines.remove(machine);
        Set<BlockPos> allNodes = machine.release();
        allNodes.forEach((BlockPos tpos) -> {
            nodes.remove(tpos);
        });
        return allNodes.size();
    }

    public static Boolean inMachine(BlockPos pos) {
        return nodes.containsKey(pos);
    }

    public static Boolean setCoopBlock(BlockPos pos) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null)
            return false;
        return machine.setCoop(pos);
    }

    public static void unsetCoopBlock(BlockPos pos) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null)
            return;
        machine.unsetCoop(pos);
    }

    public static void powerBlock(BlockPos pos, BlockPos neighborPos, int powerLevel) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null)
            return;
        machine.power(pos, neighborPos, powerLevel);
    }

    public static void powerBlock(BlockPos pos) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null)
            return;
        machine.power(pos, absTick + tickPerSecond * 3 / 2);
    }

    private static long absTick = 0;
    private static int tickPerSecond = 60;

    public static void tick(Set<BlockPos> lowNodes, Set<BlockPos> highNodes) {
        /*
         * machines.forEach((SemiconductorMachine machine) -> { machine.tick(absTick);
         * });
         */
        absTick++;
        if (speed < tickPerSecond && absTick % (tickPerSecond / speed) != 0)
            return;

        machines.forEach((SemiconductorMachine machine) -> {
            machine.run(absTick, speed < tickPerSecond ? 1 : speed / tickPerSecond, lowNodes, highNodes);
        });
    }
}