package ca.jrvs.apps.grep;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class StringBuilderCollector implements Collector<String,StringBuilder,String> {
    private StringBuilder sb;
    StringBuilderCollector() {
        this.sb = new StringBuilder();
    }

    @Override
    public Supplier<StringBuilder> supplier() {
        return () -> {return this.sb;};
    }

    @Override
    public BiConsumer<StringBuilder, String> accumulator() {
        return (sb, string) -> {
            sb.append(string);
            sb.append('\n');
        };
    }

    @Override
    public BinaryOperator<StringBuilder> combiner() {
        return (sb1, sb2) -> {
            // sb1.append(sb2);
            return sb1;
        };
    }

    @Override
    public Function<StringBuilder, String> finisher() {
        return StringBuilder::toString;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
