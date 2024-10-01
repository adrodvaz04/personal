package student;

import com.google.common.collect.ObjectArrays;

import java.util.HashMap;
import java.util.Map;

public class SupportCode {
    private static final String FILE_TEMPLATE = "./res/waves/%s.wav";

    /**
     * Builds a waltz from the given minuet and trio measures. Each array
     * entry should be a base filename, such as "M11" or "T31", corresponding
     * to a file in the "./res/waves" directory with extension ".wav" (e.g.,
     * "./res/waves/M11.wav").
     *
     * @param minuetMeasures base filenames of minuet measures
     * @param trioMeasures base filenames of trio measures
     * @return an array of amplitudes
     */
    public static double[] buildWaltz(String[] minuetMeasures, String[] trioMeasures) {
        Map<String, double[]> measures = new HashMap<>();
        String[] filebases = ObjectArrays.concat(minuetMeasures, trioMeasures, String.class);

        // Determine size of result.
        int len = 0;
        for (String filebase : filebases) {
            if (!measures.containsKey(filebase)) {
                String filename = String.format(FILE_TEMPLATE, filebase);
                measures.put(filebase, StdAudio.read(filename));
            }
            len += measures.get(filebase).length;
        }

        // Concatenate the measures.
        double[] result = new double[len];
        int offset = 0;
        for (String filebase : filebases) {
            double[] measure = measures.get(filebase);
            System.arraycopy(measure, 0, result, offset, measure.length);
            offset += measure.length;
        }
        return result;
    }
}
