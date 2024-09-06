import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

class DiningPhilosophers {
    public static void main(String[] args) throws InterruptedException {

        class Fork {
            private final AtomicInteger id;  //maintaining the order of Forks
            private Semaphore isAvailable;

            public Fork(int id) {
                this.id = new AtomicInteger(id);  
                this.isAvailable = new Semaphore(1);
            }

            public int getId() {
                return id.get();  
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
            private final AtomicInteger id;  //maintaining the order of Philosophers
            private final Fork leftFork;
            private final Fork rightFork;

            public Philosopher(int id, Fork leftFork, Fork rightFork) {
                this.id = new AtomicInteger(id); 
                this.leftFork = leftFork;
                this.rightFork = rightFork;
            }

            private void think() throws InterruptedException {
                Thread.sleep((long) (Math.random() * 1000)); 
            }

            private void eat() throws InterruptedException {
                System.out.println("Philosopher " + id.get() + " is eating.");  
                Thread.sleep((long) (Math.random() * 1000));  
            }

            @Override
            public void run() {
                try {
                    while (true) {
                        think();  

                        if (leftFork.getId() < rightFork.getId()) {
                            synchronized (leftFork) {
                                synchronized (rightFork) {
                                    eat();  
                                }
                            }
                        } else {
                            synchronized (rightFork) {
                                synchronized (leftFork) {
                                    eat();  
                                }
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        Fork[] forks = new Fork[5];
        Philosopher[] philosophers = new Philosopher[5];
        Thread[] threads = new Thread[5];

        for (int i = 1; i <= 5; i++) {
            forks[i - 1] = new Fork(i);
        }

        for (int i = 1; i <= 5; i++) {
            Fork leftFork = forks[i - 1];
            Fork rightFork = forks[i % 5];  
            philosophers[i - 1] = new Philosopher(i, leftFork, rightFork);
            threads[i - 1] = new Thread(philosophers[i - 1]); 
            threads[i - 1].start(); 
        }

        for (int i = 0; i < 5; i++) {
            threads[i].join();  
        }
    }
}