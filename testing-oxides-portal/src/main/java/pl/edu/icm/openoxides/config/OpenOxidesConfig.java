package pl.edu.icm.openoxides.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class OpenOxidesConfig {

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.indentOutput(true);
        builder.featuresToDisable(
                SerializationFeature.WRITE_NULL_MAP_VALUES,
                SerializationFeature.FAIL_ON_EMPTY_BEANS
        );
        builder.failOnUnknownProperties(false);
        return builder;
    }
}
