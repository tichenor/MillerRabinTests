import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;

/**
 * MillerRabinSerial is a (multi)threaded implementation of the Miller-Rabin
 * primality test (https://en.wikipedia.org/wiki/Miller%E2%80%93Rabin_primality_test).
 * The implementation uses an ExecutorService interface that manages the execution of tasks.
 * Instead of the tasks (instances of MillerRabinCallable) using the Runnable interface,
 * they use the Callable interface, which means the tasks can return a result once they
 * are completed. The tasks in this implementation correspond to single Miller-Rabin trials.
 * See the docstring of the serial version for more information on the Miller-Rabin test.
 *
 * The results of the trials (booleans) are stored in a HashSet using the Future interface.
 * I'm not entirely sure how Future works but it represents a future result of an
 * asynchronous computation. The ExecutorService (sort of like a multiprocessing.Pool
 * in Python if I understand it correctly) uses the submit()-method, passing a Callable
 * as an argument, and returns a FutureTask object which is then added to the HashSet.
 * Once the tasks are completed, one can iterate over the HashSet and use the get()-method
 * on the FutureTasks to read the result of each finished task.
 *
 * IMPORTANT: The way multithreading is implemented here is that the program waits for
 * all the threads to finish their work before presenting the results. In the case that
 * one is testing a composite number, the program will execute all trials before finishing,
 * even if the first trial proves that the number is composite. Hence this threaded
 * implementation is only really faster than the serial version if the number being tested
 * is a prime, since then all trials would need to be executed. Thus the threaded version
 * is mostly useful as an mediocre example of multithreading, or when testing a number that is
 * already suspected to be prime. Some large known primes can be found in the Primes
 * class for testing purposes.
 *
 * References:
 * Handbook of Applied Cryptography Chapter 4.2 (free at http://cacr.uwaterloo.ca/hac/)
 * Oracle's java documentation (https://docs.oracle.com/en/java)
 * Various threads on StackOverflow
 * Various tutorials on multithreading on the internet
 *
 * @author Arvid Ehrlén
 * @since 2020-04-14
 */
public class MillerRabinThreaded {

    private final ExecutorService threadPool;
    private final Set<Future<Boolean>> results;

    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = BigInteger.TWO;
    private static final BigInteger THREE = new BigInteger("3");

    /**
     * A class whose instances are meant to be executed by another thread.
     * Implements the Callable interface, containing a single method call() taking no
     * arguments and returning a result. It is similar to Runnable interface
     * except that it can return a result. An instance of this class runs one trial
     * of the Miller-Rabin test with parameters specified in the constructor.
     */
    private static class MillerRabinCallable implements Callable<Boolean> {

        private final BigInteger n;
        private final BigInteger d;
        private final int s;

        /**
         * Constructor method for a Callable task representing a single trial of a test.
         * @param n The integer to be tested.
         * @param d An odd integer such that n - 1 = 2^s * d.
         * @param s See above.
         */
        public MillerRabinCallable(BigInteger n, BigInteger d, int s) {
            // The parameters are the same as the variables n, d, s in the serial implementation.
            this.n = n;
            this.d = d;
            this.s = s;
        }

        /**
         * Compute the result of a single trial and return the result. Returns
         * true if the trial didn't detect that the integer is composite, and
         * false if the integer is proved composite.
         */
        @Override
        public Boolean call() {
            // Run a trial of the Miller-Rabin test.
            // This is the same as one iteration of the trials run in MillerRabinSerial.
            BigInteger a = Utility.uniformRandom(TWO, n.subtract(ONE));
            BigInteger x = a.modPow(d, n); // x = a^d modulo n
            if (x.equals(ONE) || x.equals(n.subtract(ONE))) {
                return true;
            }
            int r = 1;
            for (; r < s; r++) { // r runs through 1,2,...,s-1
                x = x.modPow(TWO, n); // square x and reduce mod n
                if (x.equals(ONE)) {
                    return false;
                }
                if (x.equals(n.subtract(ONE))) {
                    return true;
                }
            }
            if (r == s) { // if none of the steps satisfied x = n - 1
                return false;
            }
            // Task finished without detecting compositeness.
            return true;
        }
    }

    /**
     * Constructor method for initializing a test. Creates a new thread pool
     * with a fixed number of threads and initializes the HashSet that will
     * store the results of the tests.
     * @param numThreads The number of threads in the pool.
     */
    public MillerRabinThreaded(int numThreads) {
        // Create a thread pool with the specified number of threads.
        threadPool = Executors.newFixedThreadPool(numThreads);
        // Store the results of asynchronous computation of Miller-Rabin trials.
        results = new HashSet<>();
    }

    /**
     * Perform a Miller-Rabin test on an integer with the specified number of trials (iterations).
     * Each trial consists of generating a random integer and checking whether it is a Miller-Rabin
     * witness. The integer to be tested must be odd and at least 5. The method creates a Callable
     * task for each trial to be performed and submits the tasks to the ExecutorService thread pool.
     * @param n Integer to be tested for primality.
     * @param numIter Number of trials to perform.
     * @return 'true' if no trial proves compositness, and 'false' otherwise.
     */
    boolean isProbablePrime(BigInteger n, int numIter) {
        // Handle base cases
        if (n.compareTo(THREE) < 0) {
            throw new IllegalArgumentException("Input n must be greater or equal to 3.");
        }
        if (n.mod(TWO).equals(ZERO)) {
            throw new IllegalArgumentException("Input n must be an odd integer.");
        }

        int s = 0; // s will count the number of factors of 2 in the even integer n - 1
        BigInteger d = n.subtract(ONE); // d = n - 1

        while (d.mod(TWO).equals(ZERO)) { // while d is even
            s++;
            d = d.divide(TWO);
        }

        // Now n - 1 = 2^s * d, where d is an odd integer.

        // Begin multithreading here
        for (int i = 0; i < numIter; i++) {
            // Create a task for each iteration (trial).
            Callable<Boolean> task = new MillerRabinCallable(n, d, s);
            // Submit the task for execution. The submit method returns a Future
            // representing the pending results from the task.
            Future<Boolean> taskResult = threadPool.submit(task);
            results.add(taskResult); // Store the pending result.
        }
        // Make the executor not accept any new tasks (probably not needed).
        threadPool.shutdown();
        // Wait for threads to finish tasks (max 60 seconds by default, increase if needed).
        try {
            threadPool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // Happens if the thread pool is interrupted while waiting.
            e.printStackTrace();
        }
        // Retrieve the results of the trials.
        // If none of the trials proves compositeness, return 'true'.
        boolean isProbablePrime = true;
        try {
            for (Future<Boolean> res : results) {
                boolean trialResult = res.get(); // Attempt to retrieve the result of a completed task.
                if (!trialResult) {
                    isProbablePrime = false; // One of the trials proved n was composite.
                    break;
                }
            }
        } catch (ExecutionException | CancellationException | InterruptedException e) {
            // This happens when get() is called on a Future when the computation of a task
            // threw an exception (ExecutionException), or the computation was cancelled (CancellationException),
            // or if the thread working on the task was interrupted (InterruptedException).
            e.printStackTrace();
        }
        return isProbablePrime;
    }

    public static void main(String[] args) {
        // Example on how to run tests.

        // Number of threads to use. Change this to test computation time.
        int numThreads = 8;
        // Integer to be tested (when running a single test).
        BigInteger testInteger = Primes.M_25; // Se the Primes class for more primes.
        // Number of trials to run in each test. Increase this to improve accuracy and test computation time.
        int numTrials = 10;

        // Run a single test and print the results by uncommenting the following.
        // Pair<Long, Boolean> result = Tester.runSingleTest(testInteger, numThreads, numTrials);
        // Tester.printSingleResult(result, numThreads, numTrials);

        // Run a full test on Mersenne primes (15th up to and including 23th) and print results.
        // List<Triplet<String, Long, Boolean>> results = Tester.runFullTest(numThreads, numTrials);
        // Tester.printFullResult(results, numThreads, numTrials);

        // Write the results of a full test to a simple text file.
        // Utility.writeToFile(results, "threaded-m15-23", numThreads, numTrials);

    }

}
