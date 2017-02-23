package moe.tristan.HashCode2017.util;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * The parser class
 */
@Slf4j
public class Parser {
    @NotNull
    public static InputFile parseFile(String pathStr) {
        Path path = Paths.get("/Users/Tristan/IdeaProjects/HashCode2017/input/"+pathStr);
        log.info("Loading file : " + path.toAbsolutePath().toString());

        try {
            List<String> lines = Files.readAllLines(path);
            log.info("Loaded {} lines", lines.size());
            return new InputFile(lines);
        } catch (IOException e) {
            e.printStackTrace();
            return new InputFile(Collections.emptyList());
        }
    }
}
