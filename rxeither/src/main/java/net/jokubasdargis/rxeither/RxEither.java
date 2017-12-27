package net.jokubasdargis.rxeither;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Helper to create and filter {@link Observable}s of {@link Either} type.
 */
public final class RxEither {

    /**
     * Creates only left {@link Either} type emitting observable.
     */
    public static <L, R> Observable<Either<L, R>> left(Observable<L> left) {
        return from(left, Observable.<R>never());
    }

    /**
     * Creates only right {@link Either} type emitting observable.
     */
    public static <L, R> Observable<Either<L, R>> right(Observable<R> right) {
        return from(Observable.<L>never(), right);
    }

    /**
     * Combines two observables into a single {@link Either} observable.
     */
    public static <L, R> Observable<Either<L, R>> from(Observable<L> left, Observable<R> right) {
        return Observable.create(new EitherOnSubscribe<>(left, right));
    }

    /**
     * Checks whether {@link Either} is left.
     */
    public static <L, R> Predicate<Either<L, R>> isLeft() {
        return IsLeft.instance();
    }

    /**
     * Checks whether {@link Either} is right.
     */
    public static <L, R> Predicate<Either<L, R>> isRight() {
        return IsRight.instance();
    }

    /**
     * Filters left side of {@link Either} observable.
     */
    public static <L, R> Observable<L> filterLeft(Observable<Either<L, R>> either) {
        return either.filter(RxEither.<L, R>isLeft()).map(JoinLeft.<L, R>instance());
    }

    /**
     * Filters right side of {@link Either} observable.
     */
    public static <L, R> Observable<R> filterRight(Observable<Either<L, R>> either) {
        return either.filter(RxEither.<L, R>isRight()).map(JoinRight.<L, R>instance());
    }

    /**
     * Creates an {@link Consumer} to lazily invoke the provided fold {@link Consumer}s.
     */
    public static <L, R> Consumer<Either<L, R>> continuedLazy(Consumer<L> left, Consumer<R> right) {
        return ContinuedLazy.create(left, right);
    }

    /**
     * Creates a {@link Function} to lazily get a fold result from the provided {@link Function}s.
     */
    public static <L, R, T> Function<Either<L, R>, T> joinLazy(Function<L, T> left, Function<R, T> right) {
        return JoinLazy.create(left, right);
    }

    private static class ContinuedLazy<L, R> implements Consumer<Either<L, R>> {
        private final Consumer<L> left;
        private final Consumer<R> right;

        static <L, R> Consumer<Either<L, R>> create(Consumer<L> left, Consumer<R> right) {
            return new ContinuedLazy<>(left, right);
        }

        private ContinuedLazy(Consumer<L> left, Consumer<R> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public void accept(Either<L, R> lrEither) throws Exception {
            lrEither.continued(left, right);
        }
    }

    private static class JoinLazy<L, R, T> implements Function<Either<L, R>, T> {
        private final Function<L, T> left;
        private final Function<R, T> right;

        public static <L, R, T> Function<Either<L, R>, T> create(Function<L, T> left, Function<R, T> right) {
            return new JoinLazy<>(left, right);
        }

        private JoinLazy(Function<L, T> left, Function<R, T> right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public T apply(Either<L, R> lrEither) {
            return lrEither.join(left, right);
        }
    }

    private static class JoinLeft<L, R> implements Function<Either<L, R>, L> {
        @SuppressWarnings("unchecked")
        static <L, R> JoinLeft<L, R> instance() {
            return (JoinLeft<L, R>) Holder.INSTANCE;
        }

        @Override
        public L apply(Either<L, R> lrEither) throws Exception {
            return lrEither.join(Identity.<L>instance(), Nothing.<R, L>instance());
        }

        private static class Holder {
            static final JoinLeft<?, ?> INSTANCE = new JoinLeft<>();
        }
    }

    private static class JoinRight<L, R> implements Function<Either<L, R>, R> {
        @SuppressWarnings("unchecked")
        static <L, R> JoinRight<L, R> instance() {
            return (JoinRight<L, R>) Holder.INSTANCE;
        }

        @Override
        public R apply(Either<L, R> lrEither) throws Exception {
            return lrEither.join(Nothing.<L, R>instance(), Identity.<R>instance());
        }

        private static class Holder {
            static final JoinRight<?, ?> INSTANCE = new JoinRight<>();
        }
    }

    private static class IsLeft<L, R> implements Predicate<Either<L, R>> {
        @SuppressWarnings("unchecked")
        static <L, R> IsLeft<L, R> instance() {
            return (IsLeft<L, R>) Holder.INSTANCE;
        }

        @Override
        public boolean test(Either<L, R> lrEither) {
            return lrEither.isLeft();
        }

        private static class Holder {

            static final IsLeft<?, ?> INSTANCE = new IsLeft<>();
        }
    }

    private static class IsRight<L, R> implements Predicate<Either<L, R>> {
        @SuppressWarnings("unchecked")
        static <L, R> IsRight<L, R> instance() {
            return (IsRight<L, R>) Holder.INSTANCE;
        }

        @Override
        public boolean test(Either<L, R> lrEither) {
            return lrEither.isRight();
        }

        private static class Holder {

            static final IsRight<?, ?> INSTANCE = new IsRight<>();
        }
    }

    private static class Nothing<T, R> implements Function<T, R> {
        @SuppressWarnings("unchecked")
        static <T, R> Nothing<T, R> instance() {
            return (Nothing<T, R>) Holder.INSTANCE;
        }

        @Override
        public R apply(T t) throws Exception {
            return null;
        }

        private static class Holder {
            static final Nothing<?, ?> INSTANCE = new Nothing<>();
        }
    }

    private static class Identity<T> implements Function<T, T> {
        @SuppressWarnings("unchecked")
        static <T> Identity<T> instance() {
            return (Identity<T>) Holder.INSTANCE;
        }

        @Override
        public T apply(T t) throws Exception {
            return t;
        }

        private static class Holder {
            static final Identity<?> INSTANCE = new Identity<>();
        }
    }

    private RxEither() {
        throw new AssertionError("No instances");
    }
}
