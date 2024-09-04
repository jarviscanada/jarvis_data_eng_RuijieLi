package ca.jrvs.apps.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LambdaStreamImp implements LambdaStreamExc {

    @Override
    public Stream<String> createStrStream(String... strings) {
        return Arrays.stream(strings);
    }

    @Override
    public Stream<String> toUpperCase(String... strings) {
        return Arrays.stream(strings).map(String::toUpperCase);
    }

    @Override
    public Stream<String> filter(Stream<String> stringStream, String pattern) {
        Pattern compiledPattern = Pattern.compile(pattern);
        return stringStream.filter((String s) -> compiledPattern.matcher(s).find());
    }

    @Override
    public IntStream createIntStream(int[] arr) {
        return Arrays.stream(arr);
    }

    @Override
    public <E> List<E> toList(Stream<E> stream) {
        return stream.collect(Collectors.toList());
    }

    @Override
    public List<Integer> toList(IntStream stream) {
        return stream.boxed().collect(Collectors.toList());
    }

    @Override
    public IntStream createIntStream(int start, int end) {
        return IntStream.range(start, end + 1);
    }

    @Override
    public DoubleStream squareRootIntStream(IntStream intStream) {
        return intStream.mapToDouble(Double::valueOf).map(Math::sqrt);
    }

    @Override
    public IntStream getOdd(IntStream stream) {
        return stream.filter(
            (int i) -> {return i % 2 != 0;}
        );
    }

    @Override
    public Consumer<String> getLambdaPrinter(String prefix, String suffix) {
        return new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(prefix + s + suffix);
            }
        };
    }

    @Override
    public void printMessages(String[] messages, Consumer<String> consumer) {
        this.createStrStream(messages).forEach(consumer);
    }

    @Override
    public void printOdd(IntStream intStream, Consumer<String> printer) {
        this.getOdd(intStream).mapToObj((int i) -> "" + i).forEach(printer);
    }

    @Override
    public Stream<Integer> flatNestedInt(Stream<List<Integer>> ints) {
        ArrayList<Integer> arr = new ArrayList<Integer>();
        ints.forEach(arr::addAll);
        return arr.stream();
    }

    public Stream<Integer> flatNestedInt2(Stream<List<Integer>> ints) {
        List<?> test;
        test = Arrays.asList(new String[]{"12", "12", "12"});
//        test = new ArrayList<Integer>();
//        test.add(Integer.valueOf(12));
        test.forEach(System.out::println);
        return ints.flatMap(List::stream);
    }
}
