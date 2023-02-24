import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class vase {
    public static void main(String[] args) {
        int threadCount = getThreadCount();
        int maxSleepMilli = 2000;
        ExecutorService exService = Executors.newFixedThreadPool(threadCount);
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
        AtomicBoolean isRoomOpen = new AtomicBoolean();
        AtomicBoolean printLock = new AtomicBoolean();

        isRoomOpen.set(true);
        printLock.set(false);

        // dispatch threads
        for (int i = 0; i < threadCount; i++) {
            exService.execute(
                new Guest(i, getIdleTimeMilli(maxSleepMilli), queue, isRoomOpen, printLock)
            );
        }

        // wait for threads to finish
        exService.shutdown();
        try {
            exService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    static int getThreadCount(){
        int threadCount;
        Scanner scan = new Scanner(System.in);
        
        System.out.println("Enter number of guests. This should be at most the max number of threads.");
        threadCount = scan.nextInt();
        scan.close();

        return threadCount;
    }

    static int getIdleTimeMilli(int max) {
        return 500 + ((int) (Math.random() * max));
    }
}

class Guest implements Runnable {
    int name;
    int sleepTimeMilli;
    BlockingQueue<Integer> queue;
    AtomicBoolean isRoomOpen;
    AtomicBoolean printLock;

    Guest(int name, int sleepTimeMilli, BlockingQueue<Integer> queue, AtomicBoolean isRoomOpen, AtomicBoolean printLock) {
       this.name = name;
       this.sleepTimeMilli = sleepTimeMilli;
       this.queue = queue;
       this.isRoomOpen = isRoomOpen;
       this.printLock = printLock;
    }

    @Override
    public void run() {
        while(true) {
            // The guest does not enter the queue at first
            waitAround("house");
            // The quests enters the queue
            enterQueue();
            // The guests waits until it is there turn to enter
            tryEnterRoom();
        }
    }

    void waitAround(String location) {
        try {
            Thread.sleep(sleepTimeMilli);
        }
        catch(InterruptedException e) {

        }
    }

    void enterQueue() {
        try {
            queue.put(name);
            printEvent(name + " enters queue", isRoomOpen.get());
        }
        catch(InterruptedException e) {

        }
    }

    void tryEnterRoom() {
        while(true) {
            // if they are not the first person or the room is not open, continue
            if (queue.peek() != name || !isRoomOpen.get()) continue;
            // do not change data during print opperation
            if (printLock.get()) continue;

            // exit the queue and enter the room
            queue.remove();
            isRoomOpen.set(false);
            printEvent(name + " enters room", isRoomOpen.get());

            // spend time in room
            waitAround("room");

            // leave room
            printEvent(name + " leaves room", true);
            isRoomOpen.set(true);

            break;
        }
    }

    void printEvent(String event, boolean isRoomOpen) {
        printLock.set(true);
        String output = "";
        
        output += ("Event: " + event + "\n");
        if (isRoomOpen)
            output += ("Room:  open" + "\n");
        else
            output += ("Room:  closed" + "\n");

        output += ("Queue: " + queue.toString());

        System.out.println(output + "\n" + "========================");

        printLock.set(false);
    }
}