package ca.jrvs.apps.grep;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;

public class JavaGrepImp implements JavaGrep {
    final Logger logger = LoggerFactory.getLogger(JavaGrepImp.class);
    private String regex;
    private String rootPath;
    private String outFile;
    private Pattern compiledPattern;
    public static void main(String[] args) {
        if(args.length != 3) {
            throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
        }
        BasicConfigurator.configure();
        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutFile(args[2]);

        try {
            javaGrepImp.process();
        } catch (Exception ex) {
            javaGrepImp.logger.error("Error: unable to process", ex);
        }
    }

    @Override
    public void process() throws IOException {
//        this.listFiles(this.rootPath);
    }

    @Override
    public List<File> listFiles(String rootDir) {
        List<File> files = new ArrayList<>();
        Path rootDirPath = Paths.get(rootDir);
        this.logger.debug(rootDir);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootDirPath)) {
            for (Path path:stream) {
                if(Files.isRegularFile(path)) {
                    files.add(new File(path.toString()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.logger.debug(files.toString());
        return files;
    }

    @Override
    public List<String> readLines(File inputFile) {
        return Collections.emptyList();
    }

    @Override
    public boolean containsPattern(String line) {
        return this.compiledPattern.matcher(line).find();
    }

    @Override
    public void writeToFile(List<String> lines) throws IOException {

    }

    @Override
    public String getRootPath() {
        return rootPath;
    }

    @Override
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public String getRegex() {
        return regex;
    }

    @Override
    public void setRegex(String regex) {
        this.regex = regex;
        this.compiledPattern = Pattern.compile(this.regex);
    }

    @Override
    public String getOutFIle() {
        return outFile;
    }

    @Override
    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }
}
