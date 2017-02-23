package moe.tristan.HashCode2017;

import lombok.extern.slf4j.Slf4j;
import moe.tristan.HashCode2017.util.InputFile;
import moe.tristan.HashCode2017.util.Parser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * HashCode !! :-)
 */
@Slf4j
@SpringBootApplication
public class HashCode2017 {

    private static ApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(HashCode2017.class);
        // End of initialization //

        Parser parser = context.getBean(Parser.class);
        InputFile kittens = Parser.parseFile("kittens.in");

        log.info("Loaded input file Kittens ! -> {}", kittens.toString());
    }
}
