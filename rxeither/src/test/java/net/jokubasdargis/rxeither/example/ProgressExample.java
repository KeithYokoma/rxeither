package net.jokubasdargis.rxeither.example;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import net.jokubasdargis.rxeither.Either;
import net.jokubasdargis.rxeither.RxEither;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class ProgressExample {

    public static void main(String[] args) {
        final PublishSubject<Integer> progress = PublishSubject.create();

        Observable<String> results = Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                progress.onNext(0);
                TimeUnit.SECONDS.sleep(1);
                progress.onNext(100);
                return "Hello, world!";
            }
        }).subscribeOn(Schedulers.io());

        Observable<Either<Integer, String>> either = RxEither.from(progress, results).share();

        RxEither.filterLeft(either).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("Progress: " + integer);
            }
        });
        String result = RxEither.filterRight(either).blockingSingle();
        System.out.println("Results: " + result);
    }

    private ProgressExample() {
        throw new AssertionError("No instances");
    }
}

