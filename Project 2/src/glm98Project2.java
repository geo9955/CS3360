import java.util.*;
import java.lang.Math;

public class glm98Project2 {
    public static void main(String[] args) {
        System.out.println("Hello world");
    }
}

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