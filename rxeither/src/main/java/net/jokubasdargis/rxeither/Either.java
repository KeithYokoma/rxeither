package net.jokubasdargis.rxeither;

import com.pacoworks.rxsealedunions2.Union2;

/**
 * Represents a value of one of two possible types (a disjoint union.) Instances are either
 * instance of {@link Left} or {@link Right}.
 *
 * @param <L> The type of left value.
 * @param <R> The type of right value.
 */
public abstract class Either<L, R> implements Union2<L, R> {

    /**
     * Creates {@link Either} instance for the given value as left.
     */
    public static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }

    /**
     * Creates {@link Either} instance for the given value as right.
     */
    public static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }

    Either() { }

    /**
     * @return true if this is a Left, false otherwise.
     */
    public abstract boolean isLeft();

    /**
     * @return false if this is a Right, false otherwise.
     */
    public abstract boolean isRight();
}
