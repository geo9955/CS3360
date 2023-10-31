import java.io.FileWriter;
import java.util.*;
import java.lang.Math;
import java.io.File;
import java.io.IOException;

public class glm98Project2 {

    static int ARRIVAL_RATE;
    static double SERVICE_RATE;
    static final int FINAL_PROCESS_COUNT = 10000;
    static double simulationTime = 0;
    static double activeTime = 0;
    static double totalTurnaroundTime = 0;
    static int pid = 0;
    static double weightedTotalWaitingCPU = 0;
    static int processesHandled = 0;
    static PriorityQueue<Event> eventQueue = new PriorityQueue<Event>(new EventComparator());
    static LinkedList<Process> readyQueue = new LinkedList<Process>();
    boolean cpuIdle = true;

    public static void main(String[] args) {
        glm98Project2 o = new glm98Project2();

        if(args.length == 0) {
            System.out.println("Parameter usage: (Average arrival rate) (Average CPU service time) (Average disk service time)");
            System.out.println("Example command: \njava glm98Project2 10 0.04");
            return;
        }

        if(args.length != 2) {
            System.out.println("Incorrect number of parameters: Expected 2, received" +  args.length + ".");
            System.out.println("Run this program with no parameters to see the expected format.");
            return;
        }

        try {
            //Since these are command line arguments I can't feasibly make them properly final, but I can act like it
            ARRIVAL_RATE = Integer.parseInt(args[0]);
            SERVICE_RATE = 1 / Double.parseDouble(args[1]);
        } catch(NumberFormatException e) {
            System.out.println("Invalid parameter type. The first parameter must be an integer, and the second must be a float or double.");
            return;
        }

        double initialArrivalTime = generateTime(ARRIVAL_RATE);
        eventQueue.add(o.new ArrivalEvent(initialArrivalTime, o.new Process(initialArrivalTime, pid)));

        while(processesHandled < FINAL_PROCESS_COUNT)
            eventQueue.poll().handle();

        double meanTurnaround = totalTurnaroundTime / FINAL_PROCESS_COUNT;
        double meanThroughput = FINAL_PROCESS_COUNT / simulationTime;
        double meanUtilization = activeTime / simulationTime;
        double meanInQueue = weightedTotalWaitingCPU / simulationTime;

        System.out.println("Average turnaround time: " + meanTurnaround);
        System.out.println("Average throughput: " + meanThroughput);
        System.out.println("Average utilization: " + meanUtilization);
        System.out.println("Average # of processes in ready queue: " + meanInQueue);
        System.out.println("---------------------------------------------------------");

        try {

            FileWriter writer = new FileWriter("logfile.csv");
            writer.write(meanTurnaround + ", " + meanThroughput + ", " + meanUtilization + ", " + meanInQueue + "\n");
            writer.flush();
            writer.close();
        } catch(IOException e) {
            System.out.println("Error creating log data");
        }

        //CPUCompletionEvent event = o.new CPUCompletionEvent(10, o.new Process(10, 10));
       // g.eventQueue.add(new ArrivalEvent(10, new Process(10, g.pid)));
        //System.out.println("Timestamp: " + g.eventQueue.peek().timestamp);

        /*
        System.out.println(args.length);
        System.out.println("Lambda: " + args[0]);
        System.out.println("Mu: " + args[1]);*/
    }

    public static double generateTime(double rate) {
        return (-(1/rate) * Math.log(1 - Math.random()));
    }

    class CompletionEvent extends Event {
        CompletionEvent(double timestamp, Process process) {
            super(timestamp, process);
        }

        @Override
        void handle() {
            activeTime += process.serviceTime;
            double timePassed = timestamp - simulationTime;
            simulationTime = timestamp;
            processesHandled++;
            totalTurnaroundTime += (simulationTime - process.arrivalTime);
            weightedTotalWaitingCPU += (timePassed * readyQueue.size());

            if(readyQueue.isEmpty())
                cpuIdle = true;
            else {
                Process newProcess = readyQueue.poll();
                eventQueue.add(new CompletionEvent(simulationTime + newProcess.serviceTime, newProcess));
            }
        }
    }

    class ArrivalEvent extends Event {
        ArrivalEvent(double timestamp, Process process) {
            super(timestamp, process);
        }

        @Override
        void handle() {
            double timePassed = timestamp - simulationTime;
            simulationTime = timestamp;
            weightedTotalWaitingCPU += (timePassed * readyQueue.size());

            if(cpuIdle) {
                cpuIdle = false;
                double newEventTime = simulationTime + process.serviceTime;
                eventQueue.add(new CompletionEvent(newEventTime, process));
            }
            else readyQueue.add(process);

            pid++;
            double newProcessTime = simulationTime + generateTime(ARRIVAL_RATE);
            eventQueue.add(new ArrivalEvent(newProcessTime, new Process(newProcessTime, pid)));
        }
    }

    class Process {
        double arrivalTime, serviceTime;
        int id;

        public Process(double arrivalTime, int id) {
            this.arrivalTime = arrivalTime;
            this.id = id;
            serviceTime = generateTime(SERVICE_RATE);
        }

    }

    abstract class Event {
        double timestamp;
        Process process;

        public Event(double timestamp, Process process) {
            this.timestamp = timestamp;
            this.process = process;
        }

        abstract void handle();
    }

    static class EventComparator implements Comparator<Event> {
        @Override
        public int compare(Event e1, Event e2) {
            if(e1.timestamp < e2.timestamp)
                return -1;
            if(e1.timestamp > e2.timestamp)
                return 1;
            return 0;
        }
    }
}

/*class g {
    static boolean cpu_idle = true;
    static double simulationTime = 0;
    static double cpuUsageTime = 0;
    static double totalTurnaroundTime = 0;
    static int pid = 0;
    static int totalWaitingCPU = 0;
    static int waitingChecksPerformed = 0;
    static int processesHandled = 0;
    static PriorityQueue<Event> eventQueue = new PriorityQueue<Event>(new EventComparator());
    static Queue<Process> readyQueue = new PriorityQueue<Process>();
}*/
/*
class CPUCompletionEvent extends Event {
    CPUCompletionEvent(double timestamp, Process process) {
        super(timestamp, process);
    }

    @Override
    void handle() {
    }
}

class ArrivalEvent extends Event {
    ArrivalEvent(double timestamp, Process process) {
        super(timestamp, process);
    }

    @Override
    void handle() {

    }
}

class Process {
    double timestamp,
            lastCPUStartTime;
    int id;

    public Process(double timestamp, int id) {
        this.timestamp = timestamp;
        this.id = id;
        lastCPUStartTime = 0;
    }

}

abstract class Event {
    double timestamp;
    Process process;

    public Event(double timestamp, Process process) {
        this.timestamp = timestamp;
        this.process = process;
    }

    abstract void handle();
}

class EventComparator implements Comparator<Event> {
    @Override
    public int compare(Event e1, Event e2) {
        if(e1.timestamp < e2.timestamp)
            return -1;
        if(e1.timestamp > e2.timestamp)
            return 1;
        return 0;
    }
}

 */