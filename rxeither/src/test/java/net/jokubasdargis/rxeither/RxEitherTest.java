package net.jokubasdargis.rxeither;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public final class RxEitherTest {

    private final EventA eventA = new EventA();
    private final EventB eventB = new EventB();
    private final TestObserver<Either<EventA, EventB>> subscriber = TestObserver.create();
    private final TestScheduler testScheduler = new TestScheduler();
    private final Subject<EventA> eventASubject = PublishSubject.create();
    private final Subject<EventB> eventBSubject = PublishSubject.create();

    @Test
    public void singleLeft() {
        Observable<Either<EventA, EventB>> either = RxEither.left(eventASubject);
        either.subscribe(subscriber);

        eventASubject.onNext(eventA);

        testScheduler.triggerActions();

        subscriber.assertNoErrors();
        subscriber.assertNotComplete();
        subscriber.assertValue(Either.<EventA, EventB>left(eventA));
    }

    @Test
    public void singleRight() {
        Observable<Either<EventA, EventB>> either = RxEither.right(eventBSubject);
        either.subscribe(subscriber);

        eventBSubject.onNext(eventB);

        subscriber.assertNoErrors();
        subscriber.assertNotComplete();
        subscriber.assertValue(Either.<EventA, EventB>right(eventB));
    }

    @Test
    public void errorLeftBlockRight() {
        Throwable error = new Throwable();
        Observable<Either<EventA, EventB>> either = RxEither.from(eventASubject, eventBSubject);
        either.subscribe(subscriber);

        eventASubject.onError(error);
        eventBSubject.onNext(eventB);

        subscriber.assertError(error);
        subscriber.assertNotComplete();
        subscriber.assertTerminated();
        subscriber.assertNoValues();
    }

    @Test
    public void filterLeft() {
        TestObserver<EventA> subscriber = TestObserver.create();
        Observable<EventA> left = RxEither.filterLeft(RxEither.from(eventASubject, eventBSubject));
        left.subscribe(subscriber);

        eventASubject.onNext(eventA);
        eventBSubject.onNext(eventB);

        subscriber.assertNoErrors();
        subscriber.assertNotComplete();
        subscriber.assertValue(eventA);
    }

    @Test
    public void filterRight() {
        TestObserver<EventB> subscriber = TestObserver.create();
        Observable<EventB> right =
                RxEither.filterRight(RxEither.from(eventASubject, eventBSubject));
        right.subscribe(subscriber);

        eventASubject.onNext(eventA);
        eventBSubject.onNext(eventB);

        subscriber.assertNoErrors();
        subscriber.assertNotComplete();
        subscriber.assertValue(eventB);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void continuedLazy() throws Exception {
        Consumer<EventA> eventAAction = mock(Consumer.class);
        Consumer<EventB> eventBAction = mock(Consumer.class);

        Observable<Either<EventA, EventB>> either = RxEither.from(eventASubject, eventBSubject);

        either.subscribe(RxEither.continuedLazy(eventAAction, eventBAction));

        eventASubject.onNext(eventA);
        eventBSubject.onNext(eventB);
        testScheduler.triggerActions();

        verify(eventAAction).accept(eventA);
        verify(eventBAction).accept(eventB);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void joinLazy() throws Exception {
        Consumer<EventA> eventAAction = mock(Consumer.class);
        Function<EventA, EventA> eventAEventAFunc = mock(Function.class);
        Function<EventB, EventA> eventBEventAFunc = mock(Function.class);

        when(eventAEventAFunc.apply(eventA)).thenReturn(eventA);
        when(eventBEventAFunc.apply(eventB)).thenReturn(eventA);

        Observable<Either<EventA, EventB>> either = RxEither.from(eventASubject, eventBSubject);

        either.map(RxEither.joinLazy(eventAEventAFunc, eventBEventAFunc)).subscribe(eventAAction);

        eventASubject.onNext(eventA);
        eventBSubject.onNext(eventB);
        testScheduler.triggerActions();

        verify(eventAAction, times(2)).accept(eventA);
    }
}
