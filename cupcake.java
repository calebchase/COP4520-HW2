import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class cupcake {
    public static void main(String[] args) {
        int guestCount = getThreadCount();
        ExecutorService exService = Executors.newFixedThreadPool(guestCount);
        AtomicInteger selectedGuest = new AtomicInteger();
        AtomicBoolean isCupcake = new AtomicBoolean();
        AtomicInteger leaderCount = new AtomicInteger();
        AtomicBoolean isParty = new AtomicBoolean();

        selectedGuest.set((int) (Math.random() * guestCount));
        isCupcake.set(true);
        leaderCount.set(0);
        isParty.set(true);

        // have the first guest be the leader
        exService.execute(
            new Leader(0, selectedGuest, isCupcake, isParty, leaderCount, guestCount)
        );

        // dispatch the remaining guests
        for (int i = 1; i < guestCount; i++) {
            exService.execute(
                new Guest(i, selectedGuest, isCupcake, isParty, guestCount)
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

        if (leaderCount.get() == guestCount) {
            System.out.println("All guests entered.");
        }

        else {
            System.out.println("Not all guests entered or unable to tell if all guests entered.");
        }
    }

    static int getThreadCount(){
        Scanner scan = new Scanner(System.in);
        
        System.out.println("Enter number of guests. This should be at most the max number of threads.");
        int threadCount = scan.nextInt();

        return threadCount;
    }
}

class Leader implements Runnable {
    int name;
    int guestCount;
    boolean firstEnter = true;
    AtomicInteger selectedGuest;
    AtomicBoolean isCupcake;
    AtomicInteger leaderCount;
    AtomicBoolean isParty;

    Leader(int name, AtomicInteger selectedGuest, AtomicBoolean isCupcake, AtomicBoolean isParty, AtomicInteger leaderCount, int guestCount) {
        this.name = name;
        this.selectedGuest = selectedGuest;
        this.isCupcake = isCupcake;
        this.leaderCount = leaderCount;
        this.isParty = isParty;
        this.guestCount = guestCount;
    }

    @Override
    public void run() {
        while(isParty.get()) {
            // if it is not their turn, continue
            if (selectedGuest.get() != name) continue;

            // the leader was able to enter for the first time
            if (firstEnter) {
                leaderCount.incrementAndGet();
                isCupcake.set(false);
            }

            System.out.println("Leader sees cupcake: " + isCupcake.get());

            // the leader sees no guest so a person must have been there for the first time
            if (!isCupcake.get()) {
                leaderCount.incrementAndGet();
                isCupcake.set(true);
            }

            System.out.println("Cupcake is now: " + isCupcake.get());

            selectNewGuest();
        }
    }

    void selectNewGuest() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Select another guest? 1(yes) 2(no)");
        int response = scan.nextInt();

        if (response == 1)
            selectedGuest.set((int) (Math.random() * guestCount));
        else
            isParty.set(false);

    } 
}

class Guest implements Runnable {
    int name;
    int guestCount;
    boolean firstCupcake = true;
    AtomicInteger selectedGuest;
    AtomicBoolean isCupcake;
    AtomicBoolean isParty;

    Guest(int name, AtomicInteger selectedGuest, AtomicBoolean isCupcake, AtomicBoolean isParty, int guestCount) {
        this.name = name;
        this.selectedGuest = selectedGuest;
        this.isCupcake = isCupcake;
        this.isParty = isParty;
        this.guestCount = guestCount;
    }

    @Override
    public void run() {
        while(isParty.get()) {
            if (selectedGuest.get() != name) continue;

            System.out.println("Guest " + name + " sees cupcake: " + isCupcake.get());

            // it is the first time the guests has entered
            if (isCupcake.get() && firstCupcake) {
                isCupcake.set(false);
                firstCupcake = false;
            }

            System.out.println("Cupcake is now: " + isCupcake.get());

            selectNewGuest();
        }
    }


    void selectNewGuest() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Select another guest? 1(yes) 2(no)");
        int response = scan.nextInt();

        if (response == 1)
            selectedGuest.set((int) (Math.random() * guestCount));
        else
            isParty.set(false);
    }
}