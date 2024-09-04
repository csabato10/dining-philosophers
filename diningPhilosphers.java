class Philosopher implements Runnable {
    private final int id;
    private final Fork leftFork;
    private final Fork rightFork;

    public Philosopher(int id, Fork leftFork, Fork rightFork) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    // Simulate thinking
    private void think() throws InterruptedException {
        System.out.println("Philosopher " + id + " is thinking.");
        Thread.sleep((long)(Math.random() * 1000));  // Simulate thinking time
    }

    // Simulate eating
    private void eat() throws InterruptedException {
        System.out.println("Philosopher " + id + " is eating.");
        Thread.sleep((long)(Math.random() * 1000));  // Simulate eating time
    }

    @Override
    public void run() {
        try {
            while (true) {
                think();  // Philosopher thinks before trying to eat

                // Enforce an order to avoid deadlock by always picking up the lower-numbered fork first
                if (leftFork.getId() < rightFork.getId()) {
                    synchronized (leftFork) {
                        synchronized (rightFork) {
                            eat();  // Eat only if both forks are available
                        }
                    }
                } else {
                    synchronized (rightFork) {
                        synchronized (leftFork) {
                            eat();  // Eat only if both forks are available
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Fork {
    private final int id;
    private boolean isAvailable;

    public Fork(int id) {
        this.id = id;
        this.isAvailable = true;
    }

    public int getId() {
        return id;
    }

    // P operation (pick up the fork)
    public synchronized void pickUp() throws InterruptedException {
        while (!isAvailable) {
            wait();  // Wait until the fork is available
        }
        isAvailable = false;  // Fork is now taken
    }

    // V operation (put down the fork)
    public synchronized void putDown() {
        isAvailable = true;  // Fork is now available
        notifyAll();  // Notify waiting philosophers
    }
}

public class DiningPhilosophers {
    public static void main(String[] args) {
        int numPhilosophers = 5;
        Philosopher[] philosophers = new Philosopher[numPhilosophers];
        Fork[] forks = new Fork[numPhilosophers];

        // Initialize forks
        for (int i = 0; i < numPhilosophers; i++) {
            forks[i] = new Fork(i);
        }

        // Initialize philosophers and assign them left and right forks
        for (int i = 0; i < numPhilosophers; i++) {
            Fork leftFork = forks[i];
            Fork rightFork = forks[(i + 1) % numPhilosophers];  // Circular assignment of forks
            philosophers[i] = new Philosopher(i, leftFork, rightFork);
            new Thread(philosophers[i]).start();  // Start each philosopher thread
        }
    }
}
