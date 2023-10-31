import java.util.*;
import java.lang.Math;

public class glm98Project1 {

    public static Scanner reader = new Scanner(System.in);

    public static void main(String[] args) {
        int userResponse;
        do {
            do{   
                System.out.println("Which problem would you like to solve?\n1)1 2)2 3)Exit");

                while(!reader.hasNextInt()) {
                    System.out.println("Please enter a number.");
                    reader.next();
                }
                userResponse = reader.nextInt();
            } while(userResponse != 1 && userResponse != 2 && userResponse != 3);

            if(userResponse == 1)
                problem1();
            else if(userResponse == 2)
                problem2();
        }while(userResponse != 3);
    }

    /*Cut and dry. I know I don't have to *actually* keep track of the processes, just print them.
     * I wanted to do it like this though.
     */
    public static void problem1() {
        //initial setup
        final double AVG_ARRIVAL_RATE = 1 / 2.0f; //1/rate
        final double AVG_SERVICE_TIME = 1; //second
        final int FINAL_PROCESS_COUNT = 1000;
        final int INITIAL_PID = 1;
        double totalIntraArrival = 0;
        double totalService = 0;
        List<Process> processes = new ArrayList<Process>();

        //create workload
        for(int i = INITIAL_PID; i <= FINAL_PROCESS_COUNT; i++) {
            double serviceTime = generateTime(AVG_SERVICE_TIME);
            double intraArrivalTime = generateTime(AVG_ARRIVAL_RATE);

            totalIntraArrival += intraArrivalTime;
            totalService += serviceTime;

            processes.add(new Process(i, totalIntraArrival, serviceTime));
        }

        //print workload and metrics
        for(Process process : processes) {
            process.print();
        }

        System.out.println("Actual average arrival rate: " + totalIntraArrival / FINAL_PROCESS_COUNT);
        System.out.println("Actual average service time: " + totalService / FINAL_PROCESS_COUNT);
    }

    /*I genuinely don't know how we were supposed to do this without knowing about Discrete Time Event Simulation.
     * That's the method that makes the most sense to me to model what this problem's asking for (and the only one I can think of for that matter),
     *  and I already knew about it because I took OS last semester. DTES wasn't brought up in lecture until the original due date of this project,
     * which strikes me as very odd.
     */
    public static void problem2() {
        final double FAILURE_RATE = 1 / 500f;//1 failure per 500 hours
        final int DOWNTIME = 10;//hours
        final int TOTAL_HOURS_SIMULATED = 24 * 365 * 20; //24 hours a day, 365 days a year, for 20 years
        int userResponse;
        int totalTimeToFullFail = 0;
        int simulationsRun = 0;
        

        do {
            //initial set up
            double server1Hours = 0;
            double server2Hours = 0;
            boolean server1Up = true;
            boolean server2Up = true;
            boolean firstFullFailOccurred = false;
            PriorityQueue<Event> queue = new PriorityQueue<Event>(new EventComparator());

            //build event queue
            while(server1Hours < TOTAL_HOURS_SIMULATED || server2Hours < TOTAL_HOURS_SIMULATED) {
                Random rng = new Random(System.currentTimeMillis());
                server1Hours += generateTime(FAILURE_RATE, rng);
                server2Hours += generateTime(FAILURE_RATE, rng);

                if(server1Hours < TOTAL_HOURS_SIMULATED) {
                    queue.add(new Event(1, server1Hours, "Server 1 has gone down at time " + server1Hours));
                    server1Hours += DOWNTIME;
                    queue.add(new Event(1, server1Hours, "Server 1 is back up at time " + server1Hours));
                }

                if(server2Hours < TOTAL_HOURS_SIMULATED) {
                    queue.add(new Event(2, server2Hours, "Server 2 has gone down at time " + server2Hours));
                    server2Hours += DOWNTIME;
                    queue.add(new Event(2, server2Hours, "Server 2 is back up at time " + server2Hours));
                }
            }

            //pop events from the head of the queue, which will always be the next failure or repair to happen
            while(!queue.isEmpty()) {
                Event event = queue.poll();

                if(event.server == 1)
                    server1Up = !server1Up;
                if(event.server == 2)
                    server2Up = !server2Up;

                System.out.println(event.message);

                //if both servers are down and this is the first full failure, update those metrics
                if(!server1Up && !server2Up && !firstFullFailOccurred) {
                    totalTimeToFullFail += event.timestamp;
                    firstFullFailOccurred = true;
                    System.out.println("System has experienced its first full failure.");
                }
            }

            simulationsRun++;
            
            //print average results
            if(!firstFullFailOccurred)
                System.out.println("System has not gone completely down! Wow!");
            else
                System.out.println("Average time to full system failure: " + totalTimeToFullFail / simulationsRun);
            
            do{   
                System.out.println("\nRun simulation again? 1) Yes 2) No");
                while(!reader.hasNextInt()) {
                    System.out.println("Please enter a number.");
                    reader.next();
                }
                userResponse = reader.nextInt();
            } while(userResponse != 1 && userResponse != 2);

        }while(userResponse == 1);
    }
/*Generates interarrival times for a poisson distribution or service rates via exponential */
    public static double generateTime(double rate) {
        return (double) (-(1/rate) * Math.log(1 - Math.random()));
    }

/*As above but explicitly seeded for thoroughness's sake in problem 2. Still Uniform. */
    public static double generateTime(double rate, Random rng) {
        return (double) (-(1/rate) * Math.log(1 - rng.nextFloat()));
    }
}

/*Represents an event, either server going down or up */
class Event {
        int server;
        double timestamp;
        String message;

        public Event(int server, double timestamp, String message) {
            this.server = server;
            this.timestamp = timestamp;
            this.message = message;
        }
    }

/*Comparator to allow queue sorting */
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

/*Represents a process. Kinda self explanatory */
class Process {
        int pid;
        double arrivalTime;
        double serviceTime;

        public Process(int pid, double arrivalTime, double serviceTime) {
            this.pid = pid;
            this.arrivalTime = arrivalTime;
            this.serviceTime = serviceTime;
        }

        public void print() {
            System.out.println("< " + pid + ", " + arrivalTime + ", " + serviceTime + " >");
        }
    }

