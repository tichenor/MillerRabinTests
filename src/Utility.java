import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Utility {

    /**
     * Write the contents of a list of test results from the serial implementation to a simple text file.
     * If a file with the same name already exists, it will be overwritten.
     * @param results List of test results.
     * @param fName Name of the file to create.
     * @param nTrials Number of trials performed in each test.
     */
    public static void writeToFile(List<Triplet<String, Long, Boolean>> results, String fName, int nTrials) {
        List<String> lines = new ArrayList<>();
        String header = "Tests done by the serial implementation.\nNumber of trials per test: " + nTrials;
        lines.add(header);
        for (Triplet<String, Long, Boolean> res : results){
            lines.add(res.toString()); // "(string, long, boolean)"
        }
        Path file = Paths.get(fName); // Create a file with specified name, or overwrite it if it exists.
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write the contents of a list of test results from the threaded implementation to a simple text file.
     * If a file with the same name already exists, it will be overwritten.
     * @param results List of test results.
     * @param fName Name of the file to create.
     * @param nThreads Number of threads used in the tests.
     * @param nTrials Number of trials performed in each test.
     */
    public static void writeToFile(List<Triplet<String, Long, Boolean>> results, String fName, int nThreads,
                                   int nTrials) {
        List<String> lines = new ArrayList<>();
        String header = "Tests done by the threaded implementation.\nNumber of threads used: " + nThreads +
                "\nNumber of trials per test: " + nTrials;
        lines.add(header);
        for (Triplet<String, Long, Boolean> res : results){
            lines.add(res.toString()); // "(string, long, boolean)"
        }
        Path file = Paths.get(fName); // Create a file with specified name, or overwrite it if it exists.
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pick an integer uniformly at random in the specified range.
     * @param low Lower bound.
     * @param high Upper bound.
     * @return An integer within the specified bounds.
     */
    public static BigInteger uniformRandom(BigInteger low, BigInteger high) {
        // Helper method to find a random integer between 'low' and 'high' using BigInteger.
        Random random = new Random();
        BigInteger result;
        do {
            result = new BigInteger(high.bitLength(), random);
        } while (result.compareTo(low) < 0 || result.compareTo(high) > 0);
        return result;
    }

}
