package net.jokubasdargis.rxeither;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

@SuppressWarnings("unchecked")
public final class EitherTest {

    private final EventA eventA = new EventA();
    private final EventB eventB = new EventB();
    private final Function<EventA, EventA> funcAA = mock(Function.class);
    private final Function<EventB, EventA> funcBA = mock(Function.class);
    private final Function<EventA, EventB> funcAB = mock(Function.class);
    private final Function<EventB, EventB> funcBB = mock(Function.class);
    private final Consumer<EventA> actionA = mock(Consumer.class);
    private final Consumer<EventB> actionB = mock(Consumer.class);

    @Test
    public void joinLeft() throws Exception {
        Either<EventA, EventB> left = Either.left(eventA);

        left.join(funcAA, funcBA);

        verify(funcAA).apply(eventA);
        verifyNoMoreInteractions(funcBA);
    }

    @Test
    public void continuedLeft() throws Exception {
        Either<EventA, EventB> left = Either.left(eventA);

        left.continued(actionA, actionB);

        verify(actionA).accept(eventA);
        verifyNoMoreInteractions(actionB);
    }

    @Test
    public void joinRight() throws Exception {
        Either<EventA, EventB> right = Either.right(eventB);

        right.join(funcAB, funcBB);

        verify(funcBB).apply(eventB);
        verifyNoMoreInteractions(funcAB);
    }

    @Test
    public void continuedRight() throws Exception {
        Either<EventA, EventB> right = Either.right(eventB);

        right.continued(actionA, actionB);

        verify(actionB).accept(eventB);
        verifyNoMoreInteractions(actionA);
    }

    @Test
    public void leftEquals() {
        Either<EventA, EventB> left1 = Either.left(eventA);
        Either<EventA, EventB> left2 = Either.left(eventA);

        assertThat(left1).isEqualTo(left2);
    }

    @Test
    public void rightEquals() {
        Either<EventA, EventB> right1 = Either.right(eventB);
        Either<EventA, EventB> right2 = Either.right(eventB);

        assertThat(right1).isEqualTo(right2);
    }

    @Test
    public void leftIsLeft() {
        Either<EventA, EventB> left = Either.left(eventA);

        assertThat(left.isLeft()).isTrue();
    }

    @Test
    public void leftIsNotRight() {
        Either<EventA, EventB> left = Either.left(eventA);

        assertThat(left.isRight()).isFalse();
    }

    @Test
    public void rightIsRight() {
        Either<EventA, EventB> right = Either.right(eventB);

        assertThat(right.isRight()).isTrue();
    }

    @Test
    public void rightIsNotLeft() {
        Either<EventA, EventB> right = Either.right(eventB);

        assertThat(right.isLeft()).isFalse();
    }
}
