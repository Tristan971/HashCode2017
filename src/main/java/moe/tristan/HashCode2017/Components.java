package moe.tristan.HashCode2017;

import moe.tristan.HashCode2017.util.Parser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Components
 */
@Configuration
public class Components {
    @Bean
    public Parser getParser() {
        return new Parser();
    }
}
