package me.eexxlliinn.filereader.impl;

import me.eexxlliinn.filereader.FileReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileReaderImpl implements FileReader {

    @Override
    public Set<String> findAllFiles(final String path) throws IOException {
        Set<String> files = new HashSet<String>();
        try (Stream<Path> stream = Files.walk(Paths.get(path), 3)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }
}
