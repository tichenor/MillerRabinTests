import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Tester {

    /**
     * Run a single Miller-Rabin test with one thread using the serial implementation and measure the execution
     * time.
     * @param integer The integer to be tested.
     * @param numTrials The number of trials to perform in the test.
     * @return A pair consisting of the duration of the test in milliseconds, and the test result (true/false).
     */
    public static Pair<Long, Boolean> runSingleTest(BigInteger integer, int numTrials) {
        boolean isProbablePrime;
        System.out.println("Starting test...");
        long startTime = System.nanoTime();
        isProbablePrime = MillerRabinSerial.isProbablePrime(integer, numTrials);
        long endTime = System.nanoTime();
        System.out.println("Test finished.");
        long duration = TimeUnit.NANOSECONDS.toMillis((endTime - startTime));
        return new Pair<>(duration, isProbablePrime);
    }

    /**
     * Run a single Miller-Rabin test with one or more threads and measure the execution time. This method
     * uses the threaded implementation of the test regardless of the number of threads specified.
     * This method functions as an outer method to the algorithm performing the actual computation in that it
     * measures execution time and compiles the result.
     * @param integer The integer to be tested.
     * @param numThreads The number of threads that the test should use.
     * @param numTrials The number of trials to perform in each test.
     * @return A pair consisting of the duration of the test in milliseconds, and the test result (true/false).
     */
    public static Pair<Long, Boolean> runSingleTest(BigInteger integer, int numThreads, int numTrials) {
        MillerRabinThreaded mrt = new MillerRabinThreaded(numThreads);
        boolean isProbablePrime;
        System.out.println("Starting test...");
        long startTime = System.nanoTime();
        isProbablePrime = mrt.isProbablePrime(integer, numTrials);
        long endTime = System.nanoTime();
        System.out.println("Test finished.");
        long duration = TimeUnit.NANOSECONDS.toMillis((endTime - startTime));
        return new Pair<>(duration, isProbablePrime);
    }

    /**
     * Run a number of Miller-Rabin tests using the serial implementation and measure execution times.
     * Currently runs 9 tests on the 15th up to the 23rd Mersenne prime.
     * @param numTrials The number of trials to perform in each test.
     * @return A list of triples containing the results (true/false), the execution time, and an identifier
     * for the number.
     */
    public static List<Triplet<String, Long, Boolean>> runFullTest(int numTrials) {
        ArrayList<Triplet<String, Long, Boolean>> results = new ArrayList<>();
        ArrayList<BigInteger> numbersToTest = new ArrayList<>();
        numbersToTest.add(Primes.M_15);
        numbersToTest.add(Primes.M_16);
        numbersToTest.add(Primes.M_17);
        numbersToTest.add(Primes.M_18);
        numbersToTest.add(Primes.M_19);
        numbersToTest.add(Primes.M_20);
        numbersToTest.add(Primes.M_21);
        numbersToTest.add(Primes.M_22);
        numbersToTest.add(Primes.M_23);

        int numTests = numbersToTest.size();
        System.out.println("Starting " + numTests + " tests...");
        for (int i = 0; i < numTests; i++) {
            String name = "Mersenne " + (i + 15);
            boolean isProbablePrime;
            System.out.println((i+1) + " ");
            long startTime = System.nanoTime();
            isProbablePrime = MillerRabinSerial.isProbablePrime(numbersToTest.get(i), numTrials);
            long endTime = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMillis((endTime - startTime));
            Triplet<String, Long, Boolean> result = new Triplet<>(name, duration, isProbablePrime);
            results.add(result);
        }
        System.out.println("Tests finished.");
        return results;
    }

    /**
     * Run a number of Miller-Rabin tests using the threaded implementation and measure the execution times.
     * Currently runs 9 tests on the 15th up to the 23rd Mersenne prime.
     * @param numThreads The number of threads to be used in the tests.
     * @param numTrials The number of trials to perform in each test.
     * @return A list of triples containing the results (true/false), the execution time, and an identifier
     * for the number.
     */
    public static List<Triplet<String, Long, Boolean>> runFullTest(int numThreads, int numTrials) {
        ArrayList<Triplet<String, Long, Boolean>> results = new ArrayList<>();
        ArrayList<BigInteger> numbersToTest = new ArrayList<>();
        numbersToTest.add(Primes.M_15);
        numbersToTest.add(Primes.M_16);
        numbersToTest.add(Primes.M_17);
        numbersToTest.add(Primes.M_18);
        numbersToTest.add(Primes.M_19);
        numbersToTest.add(Primes.M_20);
        numbersToTest.add(Primes.M_21);
        numbersToTest.add(Primes.M_22);
        numbersToTest.add(Primes.M_23);

        int numTests = numbersToTest.size();
        System.out.println("Starting " + numTests + " tests...");
        for (int i = 0; i < numTests; i++) {
            String name = "Mersenne " + (i + 15);
            boolean isProbablePrime;
            System.out.print((i + 1) + " ");
            // The following line initializes a new ExecutorService and a HashSet for pending results.
            // It could probably be moved to a static 'reset' method instead of creating new instances.
            MillerRabinThreaded mrt = new MillerRabinThreaded(numThreads);
            long startTime = System.nanoTime();
            isProbablePrime = mrt.isProbablePrime(numbersToTest.get(i), numTrials);
            long endTime = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMillis((endTime - startTime));
            Triplet<String, Long, Boolean> result = new Triplet<>(name, duration, isProbablePrime);
            results.add(result);
        }
        System.out.println("Tests finished.");
        return results;

    }

    /**
     * Print the result of a single test from the serial implementation to the standard output stream.
     * @param result A pair containing the test result (true/false) and the duration of the test.
     * @param numTrials The number of trials performed in the test.
     */
    public static void printSingleResult(Pair<Long, Boolean> result, int numTrials) {
        boolean isProbablePrime = result.getRight();
        long duration = result.getLeft();
        String message;
        if (isProbablePrime) {
            message = "Number passed the test and is probably prime.";
        } else {
            message = "Number is composite.";
        }
        System.out.println("Test performed " + numTrials + " trials.");
        System.out.println("Duration of test: " + duration + " milliseconds.");
        System.out.println(message);
    }

    /**
     * Print the result of a single test from the threaded implementation to the standard output stream.
     * @param result A pair containing the test result (true/false) and the duration of the test.
     * @param numThreads The number of threads used in the test.
     * @param numTrials The number of trials performed in the test.
     */
    public static void printSingleResult(Pair<Long, Boolean> result, int numThreads, int numTrials) {
        boolean isProbablePrime = result.getRight();
        long duration = result.getLeft();
        String message;
        if (isProbablePrime) {
            message = "Number passed the test and is probably prime.";
        } else {
            message = "Number is composite.";
        }
        System.out.println("Test ran with " + numThreads + " threads, performing " + numTrials + " trials.");
        System.out.println("Duration of test: " + duration + " milliseconds.");
        System.out.println(message);
    }

    /**
     * Print the results of some number of tests from the serial implementation to the standard output stream.
     * @param results A list of triples containing a string ID, a boolean test result and a long execution time.
     * @param numTrials The number of trials performed each test.
     */
    public static void printFullResult(List<Triplet<String, Long, Boolean>> results, int numTrials) {
        System.out.println("Tests performed " + numTrials + " trials.");
        for (Triplet<String, Long, Boolean> res : results) {
            String name = res.getLeft();
            long dur = res.getMiddle();
            boolean isPrime = res.getRight();
            System.out.println(name + " is probable prime? " + isPrime + ". Test duration: " + dur + "ms.");
        }
    }

    /**
     * Print the results of some number of tests from the threaded implementation to the standard output stream.
     * @param results A list of triples containing a string ID, a boolean test result and a long execution time.
     * @param numThreads The number of threads that was used in the testing.
     * @param numTrials The number of trials performed each test.
     */
    public static void printFullResult(List<Triplet<String, Long, Boolean>> results, int numThreads, int numTrials) {
        System.out.println("Test ran with " + numThreads + " threads, performing " + numTrials + " trials.");
        for (Triplet<String, Long, Boolean> res : results) {
            String name = res.getLeft();
            long dur = res.getMiddle();
            boolean isPrime = res.getRight();
            System.out.println(name + " is probable prime? " + isPrime + ". Test duration: " + dur + "ms.");
        }
    }

}
