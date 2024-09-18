# Introduction
This application implements the basic functionality of Linux `grep` in Java. It allows the user to enter a regular expression, a directory to search recursively, an output file path, and optionally, a number that represents the maximum file size that can be read without using a stream. Package and plugin management is done with Maven, and there is an option to put this app inside a Docker container with Dockerfile.

# Quick Start
To compile the app:
1. `cd path/to/core_java/grep`
2. `mvn clean package`
This will generate the jar file in the directory `target`. To use the generated jar file (`grep-1.0-SNAPSHOT.jar`), use the following command:
`java -jar grep-1.0-SNAPSHOT.jar REGEX path/to/directory/to/search path/to/output/file [MAX_SIZE (optional) default=20]`
The output should now be in the specified output file.

# Implemenation
## Pseudocode
```pseudocode
paths := recursive list of files in directory to search;
files := turn_paths_to_file(path)

for each file in files:
    if file is smaller than or equal to MAX_SIZE:
        lines := read all lines in file
        filtered_lines := filter_by_regex(lines)
        write lines to file
    else:
        line_stream := line stream with Files.lines
        filtered_line_stream := filter line stream with regex
        iterator := get iterator from filtered line stream
        write to file with iterator
```

## Performance Issue
1. For a big file (e.g. bigger than RAM, bigger than 20MB by default), the application will read it one chunk at a time (could be 8kB depending on the implementation). Once a chunk is processed, the result is appended to a file before moving on to the next chunk.
2. Anything equal to or smaller than 20MB or whatever the user specifies is read in one single chunk
3. To avoid memory issues with reading the entire directory at once, the algorithm processes one file at a time.

# Test
Samples were tested with:
1. A Kaggle dataset (911 call logs) of about 100MB to test reading chunks.
2. A bash script with one line matching the regex was used for testing smaller files.

# Deployment
Without Docker: if Java 8 is installed, the jar file can be used directly
With Docker: The Docker image on Docker Hub was published with Docker push, and can be used with 
```
docker run --rm \ 
    -v path/to/directory/named/data:/data \
    -v path/to/directory/named/log:/log \
    ${docker_user}/grep .*Romeo.*Juliet.* /data /log/grep.out
```

# Improvement
1. Right now, only the lines appear. It would be better to indicate the file name with the full path
2. We can turn the output into an HTML or markdown file so that the matched part can be in a different color
3. We can add aditional functionalities such as filter which files to check
4. The Docker image cannot search the host effectively as it can only access the files in /data, and usually it's not a good idea to mount the entire file system to Docker