package com.atex.common.collections;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * A Pair of objects.
 *
 * <p>You create a pair with Pair.of().</p>
 *
 * @author mnova
 * @since 10.18.3
 */
public class Pair<T, U> {
    private final T left;
    private final U right;

    protected Pair(final T left,
                   final U right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Return the left pair.
     *
     * @return the left pair (can be null).
     */
    public T left() {
        return left;
    }

    /**
     * Return the right pair.
     *
     * @return the right pair (can be null).
     */
    public U right() {
        return right;
    }

    /**
     * Create the pair object.
     *
     * @param left a nullable object.
     * @param right a nullable object.
     * @param <T> the left object type.
     * @param <U> the right object type.
     * @return a not null Pair.
     */
    public static <T, U> Pair<T, U> of(final T left,
                                       final U right) {
        return new Pair<>(left, right);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        final Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) &&
                Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Pair.class.getSimpleName() + "[", "]")
                .add("left=" + left)
                .add("right=" + right)
                .toString();
    }
}