package net.jokubasdargis.rxeither;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

final class Right<L, R> extends Either<L, R> {

    private final R value;

    Right(R value) {
        this.value = value;
    }

    @Override
    public boolean isLeft() {
        return false;
    }

    @Override
    public boolean isRight() {
        return true;
    }

    @Override
    public void continued(Consumer<L> left, Consumer<R> right) {
        try {
            right.accept(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <R1> R1 join(Function<L, R1> left, Function<R, R1> right) {
        try {
            return right.apply(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return "Right{" + "value=" + value + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Right) {
            Right<?, ?> that = (Right<?, ?>) o;
            return (this.value.equals(that.value));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.value.hashCode();
        return h;
    }
}
