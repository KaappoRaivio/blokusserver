package misc;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class OnedSpan implements Iterable<Integer>{
    private int start;
    private int end;

    public OnedSpan(int start, int end) {
        this.start = start;
        this.end = end;
    }

    private boolean isIn (int position) {
        return position >= start && position < end;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            int current = start - 1;

            @Override
            public boolean hasNext() {
                return isIn(current + 1);
            }

            @Override
            public Integer next() {
                current += 1;
                return current;
            }
        };
    }

    @Override
    public void forEach(Consumer<? super Integer> action) {
        for (int a : this) {
            action.accept(a);
        }
    }

    @Override
    public Spliterator<Integer> spliterator() {
        throw new RuntimeException(new blokus.NotImplementedError());
    }

    public static void main (String[] args) {
        new OnedSpan(0, 10).forEach(System.out::println);
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
