
import java.util.concurrent.Semaphore;


class DiningPhilosophers{
        public static void main(String[] args) throws InterruptedException{
            
            class Fork {
                private final int id;
                private Semaphore isAvailable;
        
                public Fork(int id) {
                    this.id = id;
                    this.isAvailable = new Semaphore(1);
                }
        
                public int getId() {
                    return id;
                }
        
                // P operation (pick up the fork)
                public void pickUp() throws InterruptedException {
                    isAvailable.acquire(); 
                }
        
                // V operation (put down the fork)
                public void putDown() {
                    isAvailable.release();
                }
            }

            class Philosopher implements Runnable {
                private final int id;
                private final Fork leftFork;
                private final Fork rightFork;
        
                public Philosopher(int id, Fork leftFork, Fork rightFork){
                    this.id = id;
                    this.leftFork = leftFork;
                    this.rightFork = rightFork;
                }
        
                // Simulate thinking
                private void think() throws InterruptedException {
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
        

            Philosopher[] philosophers = new Philosopher[5];
            Fork[] forks = new Fork[5];

            // create forks 1-5 
            for (int i = 0; i < 5; i++) {
                forks[i] = new Fork(i);
            }

            // Initialize philosophers and assign them left and right forks
            for (int i = 0; i < 5; i++) {
                Fork leftFork = forks[i];
                Fork rightFork = forks[(i + 1) % 5];  // each philospher gets the fork to their left and right
                philosophers[i] = new Philosopher(i, leftFork, rightFork);
                new Thread(philosophers[i]).start();  // Start each philosopher thread
            }

            
        }

}
