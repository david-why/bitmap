package net.davidwhy.bitmap.logic;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class Semiconductor {

    public static int speed = 10;

    public static int speedUp() {
        if (speed == 2 || speed == 20 || speed == 200 || speed == 2000 || speed == 20000 || speed == 200000 || speed == 2000000) {
            speed = speed / 2 * 5;
        } else if (speed < 1000000) {
            speed *= 2;
        }
        return speed;
    }

    public static int speedDown() {
        if (speed == 5000000 || speed == 500000 || speed == 50000 || speed == 5000 || speed == 500 || speed == 50 || speed == 5) {
            speed = speed / 5 * 2;
        } else if (speed > 1) {
            speed /= 2;
        }
        return speed;
    }

    public static Map<Long, SemiconductorMachine> nodes = new HashMap<Long, SemiconductorMachine>();
    public static Set<SemiconductorMachine> machines = new HashSet<SemiconductorMachine>();

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

    public static boolean inMachine(long pos) {
        return nodes.containsKey(pos);
    }

    public static boolean setCoopBlock(long pos) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null) {
            return false;
        }
        return machine.setCoop(pos);
    }

    public static void unsetCoopBlock(long pos) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null) {
            return;
        }
        machine.unsetCoop(pos);
    }

    public static void powerBlock(long pos, boolean powered) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null) {
            return;
        }
        machine.power(pos, powered);
    }

    public static void powerBlock(long pos) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null) {
            return;
        }
        machine.power(pos, absTick + tickPerSecond);
    }

    private static long absTick = 0;
    private static int tickPerSecond = 10;

    public static void tick(Set<Long> lowNodes, Set<Long> highNodes) {
        absTick++;
        if (speed < tickPerSecond && absTick % (tickPerSecond / speed) != 0) {
            return;
        }

        machines.forEach((SemiconductorMachine machine) -> {
            machine.run(absTick, speed < tickPerSecond ? 1 : speed / tickPerSecond, lowNodes, highNodes);
        });
    }

    public static void writeObject(ObjectOutputStream stream)
            throws IOException {
        stream.writeInt(speed);
    }

    public static void readObject(ObjectInputStream stream)
            throws IOException {
        speed = stream.readInt();
    }
}
