package net.jokubasdargis.rxeither;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Action;

final class EitherOnSubscribe<L, R> implements ObservableOnSubscribe<Either<L, R>> {

    private final Observable<L> left;
    private final Observable<R> right;

    EitherOnSubscribe(Observable<L> left, Observable<R> right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void subscribe(final ObservableEmitter<Either<L, R>> emitter) throws Exception {
        final AtomicBoolean done = new AtomicBoolean();
        final CompositeDisposable disposable = new CompositeDisposable();

        left.subscribe(new Observer<L>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onNext(L l) {
                if (!done.get()) {
                    emitter.onNext(Either.<L, R>left(l));
                }
            }

            @Override
            public void onError(Throwable e) {
                if (done.compareAndSet(false, true)) {
                    emitter.tryOnError(e);
                }
            }

            @Override
            public void onComplete() {
                if (done.compareAndSet(false, true)) {
                    emitter.onComplete();
                }
            }
        });

        right.subscribe(new Observer<R>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable.add(d);
            }

            @Override
            public void onComplete() {
                if (done.compareAndSet(false, true)) {
                    emitter.onComplete();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (done.compareAndSet(false, true)) {
                    emitter.tryOnError(e);
                }
            }

            @Override
            public void onNext(R r) {
                if (!done.get()) {
                    emitter.onNext(Either.<L, R>right(r));
                }
            }
        });
        emitter.setDisposable(Disposables.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                disposable.dispose();
            }
        }));
    }
}
