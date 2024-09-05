package ca.jrvs.apps.grep;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class WriteToFileCollector implements Collector<String, FileWriter, Boolean> {

    private final FileWriter fileWriter;
    private final String outFilePath;
    private boolean newline = true;
    WriteToFileCollector(File file) {
        this.outFilePath = file.getAbsolutePath();
        try {
            this.fileWriter = new FileWriter(file);
        } catch (IOException e) {
            throw new RuntimeException("Error while creating FileWriter for file " + outFilePath, e);
        }
    }

    public void setNewline(boolean newline) {
        this.newline = newline;
    }

    @Override
    public Supplier<FileWriter> supplier() {
        return () -> this.fileWriter;
    }

    @Override
    public BiConsumer<FileWriter, String> accumulator() {
        return (writer, value) -> {
            try {
                writer.write(value + (newline ? "\n" : ""));
            } catch (IOException e) {
                throw new RuntimeException("IOException while writing " + newline + " to " + outFilePath, e);
            }
        };
    }

    @Override
    public BinaryOperator<FileWriter> combiner() {
        return (writer1, writer2) -> writer1;
    }

    @Override
    public Function<FileWriter, Boolean> finisher() {
        return (writer) -> {
            try {
                writer.close();
                return true;
            } catch (IOException e) {
                throw new RuntimeException("Error while closing FileWriter to path " + outFilePath, e);
            }
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}
