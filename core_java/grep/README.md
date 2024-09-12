# Introduction
This application implements the basic functionality of Linux `grep` in Java. It allows the user to enter a regular expression, a directory to search recursively, an output file path, and optionally, a number that reperesents the number of megabytes to read at a time when searching big files. Package and plugin management is done with Maven, and there is an option to put this app inside a Docker container with Dockerfile.

# Quick Start
To compile the app:
1. `cd path/to/core_java/grep`
2. `mvn clean package`
This will generate the jar file in the directory `target`. To use the generated jar file (`grep-1.0-SNAPSHOT.jar`), use the following command:
`java -jar grep-1.0-SNAPSHOT.jar REGEX path/to/directory/to/search path/to/output/file [CHUNK_SIZE (optional)]`
The output should now be in the specified output file

# Implemenation
## Pseudocode
```pseudocode
paths := recursive list of files in directory to search;
files := turn_paths_to_file(path)

for each file in files:
    while (not finished reading file):
        content := read CHUNK_SIZE bytes from file (default 20MB)
        lines := split_by_lines(content)
        # filter by regex
        lines_that_matched_regex := filter_by_regex(lines)
        outfile := open(outfile)
        append_lines_to_outfile(outfile, lines_that_matched_regex)
```

## Performance Issue
(30-60 words)
Discuss the memory issue and how would you fix it

# Test
How did you test your application manually? (e.g. prepare sample data, run some test cases manually, compare result)

# Deployment
How you dockerize your app for easier distribution?

# Improvement
List three things you can improve in this project.