package ca.jrvs.apps.grep;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaGrepWithStream {

    final Logger logger = LoggerFactory.getLogger(JavaGrepWithStream.class);
    private String regex;
    private String rootPath;
    private String outFile;
    private Pattern compiledPattern;
    private Integer CHUNK_SIZE = 20 * 1024 * 1024;

    public void setChunkSize(int chunkSize) {
        this.CHUNK_SIZE = chunkSize * 1024 * 1024;
    }
    public Integer getChunkSize() {
        return CHUNK_SIZE / 1024 / 1024;
    }

    public static void main(String[] args) {

        if(args.length < 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }
        BasicConfigurator.configure();
        JavaGrepWithStream javaGrepWithStream = new JavaGrepWithStream();
        javaGrepWithStream.setRegex(args[0]);
        javaGrepWithStream.setRootPath(args[1]);
        javaGrepWithStream.setOutFile(args[2]);
        javaGrepWithStream.logger.debug("name: {}", ManagementFactory.getRuntimeMXBean().getName());
        if(args.length >= 4) {
            javaGrepWithStream.setChunkSize(Integer.parseInt(args[3]));
        }
        try {
            javaGrepWithStream.process();
        } catch (IOException ex) {
            javaGrepWithStream.logger.error("IO Exception while processing files", ex);
        }
    }

    public void process() throws IOException {
        Stream<Path> paths = listFiles(this.rootPath);
        logger.debug(this.outFile);
        paths.map(
            Path::toFile
        ).forEach(
            this::filterFile
        );
    }

    void filterFile(File file) {
        try {
            if(Files.size(file.toPath()) <= CHUNK_SIZE) {
                this.filterEntireFile(file);
            } else {
                this.filterFileByLine(file);
            }
        } catch (IOException e) {
            throw new RuntimeException("IO Error while checking file size of " + file.toPath(), e);
        }
    }

    void filterEntireFile(File file) {
        try (Stream<String> lines = Files.readAllLines(file.toPath()).stream()) {
            String chunkContent = lines.filter(
                this::containsPattern
            ).collect(
                new StringBuilderCollector()
            );
            logger.debug("Reading small file {}", file.toPath());
            this.writeToFile(chunkContent);
        } catch (IOException e) {
            throw new RuntimeException("Error while processing file " + file.toPath(), e);
        }
    }

    void filterFileByLine(File file)  {
        Path path = file.toPath();
        
        try(Stream<String> lines = Files.lines(path, StandardCharsets.ISO_8859_1) ) {

            Iterable<String> iterable = lines.filter(this::containsPattern)::iterator;
            Files.write(Paths.get(this.outFile), iterable, StandardCharsets.ISO_8859_1, StandardOpenOption.APPEND, StandardOpenOption.CREATE);

        } catch (IOException e) {
            // Java has a class representing runtime IOException:
            throw new UncheckedIOException("Error while reading file " + path, e);
        }
    }

    public Stream<Path> listFiles(String rootDir) {
        try {
            return Files.walk(Paths.get(rootDir)).filter(
                path -> !Files.isDirectory(path)
            );
        } catch (IOException e) {
            throw new RuntimeException("Error: IO Exception while reading root directory", e);
        }
    }

    public Stream<String> readLines(File inputFile) {
        try {
            return Files.lines(inputFile.toPath(), Charset.forName("ISO-8859-1"));
        } catch (IOException e) {
            throw new RuntimeException("Error: IO Exception while reading " + inputFile.getAbsolutePath(), e);
        }
    }

    public boolean containsPattern(String line) {
        // logger.debug(line);
        return this.compiledPattern.matcher(line).find();
    }

    public void writeToFile(String lines) {
        Path outFilePath = Paths.get(this.outFile);
        try(
            FileChannel fileChannel = FileChannel.open(outFilePath, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
            FileLock lock = fileChannel.lock()
        ) {
            Files.write(outFilePath, lines.getBytes(StandardCharsets.ISO_8859_1), StandardOpenOption.APPEND);
        } catch (IOException e) {
            logger.error("error while opening atomic file channel for {}", outFilePath, e);
        }
    }

    public String getRootPath() {
        return this.rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getRegex() {
        return this.regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
        this.compiledPattern = Pattern.compile(regex);
    }

    public String getOutFIle() {
        return this.outFile;
    }

    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }
}
