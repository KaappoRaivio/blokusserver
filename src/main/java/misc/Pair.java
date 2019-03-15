package misc;

import java.util.Objects;

public class Pair<K, V> {
    private final K first;

    private final V second;

    public Pair (K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getK () {
        return first;
    }

    public V getV () {
        return second;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return first.equals(pair.first) &&
                second.equals(pair.second);
    }

    @Override
    public int hashCode () {
        return Objects.hash(first, second);
    }
}
