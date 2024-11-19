package me.eexxlliinn.filereader;

import java.io.IOException;
import java.util.Set;

public interface FileReader {

    Set<String> findAllFiles(String path) throws IOException;
}
