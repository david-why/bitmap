package net.davidwhy.bitmap.logic;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class Semiconductor {

    private static int speed = 10;

    public static int speedUp() {
        if (speed == 2 || speed == 20 || speed == 200 || speed == 2000 || speed == 20000 || speed == 200000
                || speed == 2000000) {
            speed = speed / 2 * 5;
        } else if (speed < 1000000) {
            speed *= 2;
        }
        return speed;
    }

    public static int speedDown() {
        if (speed == 5000000 || speed == 500000 || speed == 50000 || speed == 5000 || speed == 500 || speed == 50
                || speed == 5) {
            speed = speed / 5 * 2;
        } else if (speed > 1) {
            speed /= 2;
        }
        return speed;
    }

    private static Map<Long, SemiconductorMachine> nodes = new HashMap<Long, SemiconductorMachine>();
    private static Set<SemiconductorMachine> machines = new HashSet<SemiconductorMachine>();

    public static void clear() {
        speed = 10;
        nodes.clear();
        machines.clear();
    }

    public static int createMachine(Set<Long> allNodes, Set<Long> coopNodes, Set<Long> poweredNodes) {
        for (long pos : allNodes) {
            releaseMachine(pos, null, null);
        }
        SemiconductorMachine machine = new SemiconductorMachine();
        for (long pos : allNodes) {
            nodes.put(pos, machine);
        }
        machines.add(machine);
        return machine.create(allNodes, coopNodes, poweredNodes);
    }

    public static int releaseMachine(Long pos, Set<Long> allNodes, Set<Long> coopNodes) {
        SemiconductorMachine machine = nodes.get(pos);
        if (machine == null) {
            return 0;
        }
        machines.remove(machine);
        machine.release(allNodes, coopNodes);
        if (allNodes == null) {
            return -1;
        }
        for (long a : allNodes) {
            nodes.remove(a);
        }
        return allNodes.size();
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
    private static final int tickPerSecond = 20;

    public static void tick(Set<Long> lowNodes, Set<Long> highNodes) {
        absTick++;
        if (speed < tickPerSecond && absTick % (tickPerSecond / speed) != 0) {
            return;
        }

        for (SemiconductorMachine machine : machines) {
            machine.run(absTick, speed < tickPerSecond ? 1 : speed / tickPerSecond, lowNodes, highNodes);
        }
    }

    private static final int version = 61;

    public static void writeObject(PrintWriter out) throws IOException {
        out.println(version);
        out.println(speed);
        out.println(machines.size());
        for (SemiconductorMachine machine : machines) {
            machine.writeObject(out);
        }
    }

    public static void readObject(BufferedReader in) throws IOException, ClassNotFoundException {
        nodes.clear();
        machines.clear();

        if (Integer.parseInt(in.readLine()) != version) {
            throw new IOException();
        }

        speed = Integer.parseInt(in.readLine());
        for (int i = Integer.parseInt(in.readLine()); i > 0; i--) {
            SemiconductorMachine machine = new SemiconductorMachine();
            machine.readObject(in);
            machines.add(machine);
            Set<Long> x = new HashSet<Long>();
            machine.exportAllNodes(x);
            for (long pos : x) {
                nodes.put(pos, machine);
            }
        }
    }
}
