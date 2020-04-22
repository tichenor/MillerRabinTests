import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * MillerRabinSerial is a standard (serial) implementation of the Miller-Rabin
 * primality test (https://en.wikipedia.org/wiki/Miller%E2%80%93Rabin_primality_test).
 *
 * The Miller-Rabin primality test relies essentially on a mathematical fact that asserts
 * that if 'n' is a prime, then (a statement involving 'n' and 'x') holds for all integers 'x'.
 * Hence to test if an integer 'n' is a prime, the test checks to see if it can find an
 * integer 'x' such that the statement does not hold. In that case, the integer x is called
 * a "Miller-Rabin witness for the compositeness of n", as 'n' has been proven to not be prime.
 *
 * The test is probabilistic, meaning it does not determine without doubt that an integer is prime,
 * it can only tell us that it is very likely a prime (although there are ways to make the test
 * deterministic). This means that if a test is done on a composite number, the test may finish
 * very quickly since a single witness is enough to prove without a doubt that the number was
 * composite.
 *
 * The test is effective due to the fact that one can show that if 'n' is composite, then at least
 * 75% of all integers between 1 and 'n-1' are Miller-Rabin witnesses for 'n'. Moreover, for most
 * composite numbers the number of witnesses are much higher, resulting in a higher average
 * probability that the test is correct.
 *
 * In this implementation as well as in the threaded implementation, one trial refers to taking
 * a random integer 'x' and checking whether it is a witness for the integer being tested. If a
 * number of trials is done, say, 10, then by the above fact the probability that the test asserts
 * the correct result is bounded by 1 - (1/4)^10 (not strictly correct, but it's a good estimate).
 * Hence it's possible to achieve a very high accuracy of the test by doing just a few trials.
 *
 * Here is a good thread on StackOverflow on how many iterations to use when finding cryptographic safe primes:
 * https://stackoverflow.com/questions/6325576/how-many-iterations-of-rabin-miller-should-i-use-for-cryptographic-safe-primes
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

public class MillerRabinSerial {

    public static final BigInteger ZERO = BigInteger.ZERO;
    public static final BigInteger ONE = BigInteger.ONE;
    public static final BigInteger TWO = BigInteger.TWO;
    public static final BigInteger THREE = new BigInteger("3");

    /**
     * Perform a Miller-Rabin test on an integer with the specified number of trials (iterations).
     * Each trial consists of generating a random integer and checking whether it is a Miller-Rabin
     * witness. The integer to be tested must be odd and at least 5.
     * @param n The integer to be tested.
     * @param numIter The number of iterations (trials) to perform.
     * @return true if n is probably prime, and false if n is composite.
     */
    public static boolean isProbablePrime(BigInteger n, int numIter) {
        // Handle the base cases. The test is only designed for odd integers equal to 5 or greater.
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

        for (int i = 0; i < numIter; i++) {
            // Run the specified number of trials. Each iteration inside this loop
            // is referred to as one trial, and all trials together constitute a test.
            // System.out.println("Beginning trial number " + (i + 1));

            // Pick a random integer a between 2 and n - 2.
            BigInteger a = Utility.uniformRandom(TWO, n.subtract(ONE));
            BigInteger x = a.modPow(d, n); // x = a^d modulo n
            if (x.equals(ONE) || x.equals(n.subtract(ONE))) {
                /* Both these conditions must be false if a is going to be a witness
                * for the compositeness of n. Hence we exit to the next trial to pick a new
                * random number a. */
                // System.out.println("Trial failed. Jumping to next trial...");
                continue;
            }

            int r = 1;
            for (; r < s; r++) { // r runs through 1,2,...,s-1
                x = x.modPow(TWO, n); // square x and reduce mod n
                if (x.equals(ONE)) {
                    // System.out.println("Test finished. Number n is composite.");
                    return false; // n is proven composite, return false
                }
                if (x.equals(n.subtract(ONE))) {
                    // System.out.println("Trial failed. Jumping to next trial...");
                    break; // exit to next trial
                }
            }
            if (r == s) { // if none of the steps satisfied x = n - 1
                // System.out.println("Test finished. Number n is composite.");
                return false;
            }
        }
        // Trials concluded.
        // If we reach this point, it means that the trials were unable to prove
        // that n was composite, so it is a probable prime.
        // System.out.println("Test finished. Number n is probably prime.");
        return true;
    }

    public static void main(String[] args) {
        // Example on how to run tests.

        // Integer to be tested.
        BigInteger testInteger = Primes.M_23; // See the Primes class for more primes.
        // Number of trials to run. Increase this to improve accuracy and test computation time.
        int numTrials = 10;

        // Run a single test and print its output to the console by uncommenting the following.
        // Pair<Long, Boolean> testResult = Tester.runSingleTest(testInteger, numTrials);
        // Tester.printSingleResult(testResult, numTrials);

        // To test the 15th up to and including the 23th Mersenne prime, uncomment the following.
        // List<Triplet<String, Long, Boolean>> results = Tester.runFullTest(numTrials);
        // Tester.printFullResult(results, numTrials);

        // To save the results from a full test in a simple text file, use the following:
        // Utility.writeToFile(results, "serial-m15-23", numTrials);
    }



}
