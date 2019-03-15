package misc;

import blokus.Move;

import java.util.List;
import java.util.Vector;

public class Splitter {
    public static List<OnedSpan> splitListInto(List<Move> moves, int amountOfChunks) {
        int surfaceArea = moves.size();


        int[] lengths = new int[amountOfChunks];
        int remainder = surfaceArea % amountOfChunks;

        for (int i = 0; i < amountOfChunks; i++) {
            lengths[i] = surfaceArea / amountOfChunks;
        }

        for (int i = 0; i < remainder; i++) {
            lengths[i] += 1;
        }

        List<OnedSpan> spans = new Vector<>();

        int x = 0;

        for (int length : lengths) {
            spans.add(new OnedSpan(x, x + length));
            x += length;
        }

        return spans;
    }
}
