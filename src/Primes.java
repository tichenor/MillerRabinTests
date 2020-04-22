import java.math.BigInteger;
import java.util.ArrayList;

/**
 * This class holds some large Mersenne primes in BigInteger format. Due to the nature of
 * Mersenne primes (primes on the form 2^x - 1 for some integer x), the number of bits needed
 * to represent a Mersenne prime is exactly equal to the integer x. This gives a fast way
 * to read off how large the size of the input is when running primality tests on these primes.
 * For detailed information, see https://www.bigprimes.net/archive/mersenne.
 */
public class Primes {

    // Generate a Mersenne prime from a power (2^x - 1)
    public static BigInteger mersenneify(int power) {
        return BigInteger.TWO.pow(power).subtract(BigInteger.ONE);
    }

    // Some Mersenne primes.
    public static final BigInteger M_11 = mersenneify(107);

    public static final BigInteger M_15 = mersenneify(1279);
    public static final BigInteger M_16 = mersenneify(2203);
    public static final BigInteger M_17 = mersenneify(2281);
    public static final BigInteger M_18 = mersenneify(3217);
    public static final BigInteger M_19 = mersenneify(4253);
    public static final BigInteger M_20 = mersenneify(4423);
    public static final BigInteger M_21 = mersenneify(9689);
    public static final BigInteger M_22 = mersenneify(9941);
    public static final BigInteger M_23 = mersenneify(11213);

    public static final BigInteger M_25 = mersenneify(21701);
    public static final BigInteger M_28 = mersenneify(86243);

}