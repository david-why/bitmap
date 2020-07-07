package net.davidwhy.bitmap.logic;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Semiconductor {

    private static int speed = 10;

    public static int speedUp() {
        if (speed == 2)
            speed = 5;
        else if (speed < 1000000)
            speed *= 2;
        return speed;
    }

    public static int speedDown() {
        if (speed > 1)
            speed /= 2;
        return speed;
    }

    private static Map<Long, SemiconductorMachine> nodes = new HashMap<Long, SemiconductorMachine>();
    private static List<SemiconductorMachine> machines = new ArrayList<SemiconductorMachine>();

    public static int createMachine(Set<Long> allNodes, Set<Long> coopNodes, Set<Long> poweredNodes) {
        allNodes.forEach((Long pos) -> {
            releaseMachine(pos);
        });
        SemiconductorMachine machine = new SemiconductorMachine();
        allNodes.forEach((Long pos) -> {
            nodes.put(pos, machine);
        });
        machines.add(machine);
        return machine.create(allNodes, coopNodes, poweredNodes);
    }

    public static Set<Long> releaseMachine(Long pos) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null) {
            return null;
        }
        machines.remove(machine);
        Set<Long> allNodes = new HashSet<Long>();
        Set<Long> coopNodes = new HashSet<Long>();
        machine.release(allNodes, coopNodes);
        allNodes.forEach((Long a) -> {
            nodes.remove(a);
        });
        return coopNodes;
    }

    public static Boolean inMachine(Long pos) {
        return nodes.containsKey(pos);
    }

    public static Boolean setCoopBlock(Long pos) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null) {
            return false;
        }
        return machine.setCoop(pos);
    }

    public static void unsetCoopBlock(Long pos) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null) {
            return;
        }
        machine.unsetCoop(pos);
    }

    public static void powerBlock(Long pos, Boolean powered) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null) {
            return;
        }
        machine.power(pos, powered);
    }

    public static void powerBlock(Long pos) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null) {
            return;
        }
        machine.power(pos, absTick + tickPerSecond);
    }

    private static long absTick = 0;
    private static int tickPerSecond = 20;

    public static void tick(Set<Long> lowNodes, Set<Long> highNodes) {
        absTick++;
        if (speed < tickPerSecond && absTick % (tickPerSecond / speed) != 0) {
            return;
        }

        machines.forEach((SemiconductorMachine machine) -> {
            machine.run(absTick, speed < tickPerSecond ? 1 : speed / tickPerSecond, lowNodes, highNodes);
        });
    }
}
