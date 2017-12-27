package net.jokubasdargis.rxeither.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.jokubasdargis.rxeither.Either;
import io.reactivex.functions.Consumer;

public class SwitchActionExample {

    public static void main(String[] args) {
        System.out.println("Type Either a string or an Int: ");

        String in = "";
        Either<String, Integer> either;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            in = reader.readLine();
            either = Either.right(Integer.parseInt(in));
        } catch (NumberFormatException | IOException e) {
            either = Either.left(in);
        }

        either.continued(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println("You passed me the String: " + s);
            }
        }, new Consumer<Integer>() {
            @Override
            public void accept(Integer x) {
                System.out.println(
                        "You passed me the Int: " + x
                                + ", which I will increment. " + x + " + 1 = "
                                + (x + 1));
            }
        });
    }

    private SwitchActionExample() {
        throw new AssertionError("No instances");
    }
}
