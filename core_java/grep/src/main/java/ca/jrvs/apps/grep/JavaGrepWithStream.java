package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static void main(String[] args) {
        if(args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }
        BasicConfigurator.configure();
        JavaGrepWithStream javaGrepWithStream = new JavaGrepWithStream();
        javaGrepWithStream.setRegex(args[0]);
        javaGrepWithStream.setRootPath(args[1]);
        javaGrepWithStream.setOutFile(args[2]);
        try {
            javaGrepWithStream.process();
        } catch (Exception ex) {
            javaGrepWithStream.logger.error("Error: unable to process", ex);
        }
    }
    public void process() throws IOException {
        Stream<Path> paths = listFiles(this.rootPath);
        logger.debug(this.outFile);
//        boolean success = true;
        boolean filtered = paths.map(
            Path::toFile
        ).flatMap(
            this::readLines
        ).filter(
            this::containsPattern
        ).collect(
            new WriteToFileCollector(new File(this.outFile))
        );

        if(!filtered) {
            logger.error("write failed");
        }
    }

    public Stream<Path> listFiles(String rootDir) {
        try {
            return Files.walk(Paths.get(rootDir)).filter(
                path -> !Files.isDirectory(path)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Stream<String> readLines(File inputFile) {
        try {
            return Files.lines(inputFile.toPath(), Charset.forName("ISO-8859-1"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean containsPattern(String line) {
        return this.compiledPattern.matcher(line).find();
    }

    public boolean writeToFile(Stream<String> lines) throws IOException {
        File outFile = new File(this.outFile);
        return lines.collect(new WriteToFileCollector(outFile));
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
