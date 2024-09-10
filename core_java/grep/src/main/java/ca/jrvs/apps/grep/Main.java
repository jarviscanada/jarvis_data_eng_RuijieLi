package ca.jrvs.apps.grep;
import ca.jrvs.apps.grep.JavaGrep;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.security.auth.callback.Callback;
import javax.xml.bind.DataBindingException;

import org.slf4j.Logger;
public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.test2();
        // System.out.println("Inside main");
    }
    public final int testInt = 2;
    public boolean t = false;
    void setT(boolean t) {
        this.t = t;
    }
    void test() {
        try {
            // System.out.println("inside test() try block");
            if(t) {
                throw new IOException();
            }
            throw new RuntimeException("inside try");
        } catch(IOException e) {
            throw new IndexOutOfBoundsException();
        } catch(IndexOutOfBoundsException e) {

        } catch(DataBindingException e) {

        } finally {
            System.out.println("inside finally");
        }
        this.someMethod(1);
        this.someMethod("test");
    }
    private <T> T someMethod(final T t) {
        // t.a = 2;
        // t = 2;
        // t = new T();
        Integer i = 1;
        
        return t;
    }
    private static class Animal {
        int t;
    }
    private static class Dog extends Animal {
        int n;
    }
    void test2() {
        // Main.Dog d = (Main.Dog) new Animal();
        String.format(null, null);
        // Function a = (int a ) {return a + a;}

        Function<Integer, Integer> f = (Integer a) -> {
            return a+a;
        };
        // Function<Integer, Integer> f2 = (Integer a) -> {
        //     return a + a;
        // };
        Integer[] is = {1, 2, 4};
        Stream<Integer> s = Arrays.stream(is);
        s.collect(Collectors.toList());
        s.collect(Collectors.toList());
        Function<Integer, String> f2 = (i) -> "2";
        
    }
}
