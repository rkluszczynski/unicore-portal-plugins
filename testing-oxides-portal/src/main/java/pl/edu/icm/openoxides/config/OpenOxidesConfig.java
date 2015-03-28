package pl.edu.icm.openoxides.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestContextListener;
import pl.edu.icm.openoxides.controller.SessionData;
import pl.edu.icm.openoxides.controller.SessionDataImpl;

@Configuration
public class OpenOxidesConfig {

    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Bean
    @Scope(value = "globalSession", proxyMode = ScopedProxyMode.INTERFACES)
    public SessionData currentUser() {
        return new SessionDataImpl();
    }


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

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(15);
//        threadPoolTaskExecutor.setQueueCapacity();
        return threadPoolTaskExecutor;
    }
}
