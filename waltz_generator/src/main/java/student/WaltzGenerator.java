package student;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import javax.sound.sampled.LineUnavailableException;
import java.io.*;
import java.util.List;
import java.util.Random;

/**
 * An implementation of Mozart's musical dice game to generate waltzes.
 */
public class WaltzGenerator {
    // Constants
    private static final int NUM_SIDES = 6;
    private static final int NUM_MEASURES_IN_MINUET = 16;
    private static final int NUM_DICE_FOR_MINUET = 2;
    private static final int NUM_MEASURES_IN_TRIO = 16;
    private static final int NUM_DICE_FOR_TRIO = 1;
    public static final String MINUET_PATH = "./res/minuet.csv";
    public static final String TRIO_PATH = "./res/trio.csv";
    public static final String WAV_TEMPLATE = "waltz-%s.wav";

    // Instance variables
    private final long seed; // initialized in constructor
    private final Random random; // initialized in constructor
    private double[] waltz; // initialized lazily in getWaltz()

    /**
     * Constructs a new waltz generator.
     */
    public WaltzGenerator() {
        long timeSeed = System.currentTimeMillis();
        this.seed = timeSeed;
        this.random = new Random(timeSeed);
    }

    /**
     * Constructs a new waltz generator using the specified seed for dice rolls.
     *
     * @param seed the seed
     */
    public WaltzGenerator(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
    }

    private double[] getWaltz() throws IOException {
        if (waltz == null) {
            String[] minuetMeasures = makeMinuet(buildTable(MINUET_PATH));
            String[] trioMeasures = makeTrio(buildTable(TRIO_PATH));
            waltz = SupportCode.buildWaltz(minuetMeasures, trioMeasures);
        }
        return waltz;
    }

    /**
     * Plays the waltz created by this generator. If called repeatedly on
     * the same instance (or another instance with the same {@link #seed}),
     * this always plays the same waltz.
     *
     * @throws LineUnavailableException if {@link javax.sound.sampled.AudioSystem}
     *                                  is unavailable to satisfy requests
     * @throws IOException              if the files containing dice data or music cannot be
     *                                  read
     */
    public void playWaltz() throws LineUnavailableException, IOException {
        StdAudio.play(getWaltz());
    }

    /**
     * Saves the waltz created by this generator into a file whose name
     * includes the {@link #seed}.
     *
     * @return the name of the file
     * @throws IOException if the files containing dice data or music cannot be
     *                     read
     */
    public String saveWaltz() throws IOException {
        String filename = String.format(WAV_TEMPLATE, seed);
        StdAudio.save(filename, getWaltz());
        return filename;
    }

    @VisibleForTesting
    int rollDice(int numDice) throws IllegalArgumentException {
        if (numDice < 1) {
            throw new IllegalArgumentException("NumDice must be positive.");
        }
        int[] diceArray = new int[numDice];
        for (int i = 0; i < numDice; i++) {
            int r = this.random.nextInt(NUM_SIDES) + 1;
            diceArray[i] = r;
        }
        int sum = 0;
        for (int j = 0; j < numDice; j++) {
            sum += diceArray[j];
        }
        return sum;
    }

    @VisibleForTesting
    String[] convertCsvToStringArray(String data) {
        return data.split(", ");
    }

    private String[][] formatTable(String[][] table, int n) {
        for (int i = 0; i < n; i++) {
            table[i] = new String[0];
        }
        return table;
    }

    @VisibleForTesting
    String[][] buildTable(List<String> list) {
        int numDice = Integer.parseInt(list.get(0));
        int numRows = numDice * NUM_SIDES + 1;
        String[][] table = new String[numRows][];
        formatTable(table, numDice);
        for (int i = numDice; i < numRows; i++) {
            int listIndex = i - (numDice - 1);
            String[] iArray = convertCsvToStringArray(list.get(listIndex));
            table[i] = iArray;
        }
        return table;
    }

    private String[][] buildTable(String path) throws IOException {
        File file = new File(path);
        List<String> lines = Files.readLines(file, Charsets.UTF_8);
        return buildTable(lines);
    }

    private String[] makeResults(String[][] measure, int numMeasure, int numRolls, char prefix) {
        String[] resultArray = new String[numMeasure];
        for (int i = 0; i < numMeasure; i++) {
            int roll = this.rollDice(numRolls);
            resultArray[i] = prefix + measure[roll][i];
        }
        return resultArray;
    }

    private String[] makeMinuet(String[][] minuetMeasures) {
        return makeResults(minuetMeasures, NUM_MEASURES_IN_MINUET, NUM_DICE_FOR_MINUET, 'M');
    }

    private String[] makeTrio(String[][] trioMeasures) {
        return makeResults(trioMeasures, NUM_MEASURES_IN_TRIO, NUM_DICE_FOR_TRIO, 'T');
    }

    public static void main(String[] args) throws LineUnavailableException, IOException {
        WaltzGenerator generator = new WaltzGenerator();
        generator.playWaltz();
//         Uncomment the following two lines to save your waltzes.
        String filename = generator.saveWaltz();
//         System.out.printf("Saved waltz to %s.%n", filename);
//        generator.buildTable(TRIO_PATH);
    }
}
