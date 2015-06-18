package pl.edu.icm.oxides.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestContextListener;

@Configuration
public class OxidesConfig {

    @Bean
    @ConditionalOnMissingBean(RequestContextListener.class)
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

//    @Bean
//    public Jackson2ObjectMapperBuilder jacksonBuilder() {
//        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
//        builder.indentOutput(true);
//        builder.featuresToDisable(
//                SerializationFeature.WRITE_NULL_MAP_VALUES,
//                SerializationFeature.FAIL_ON_EMPTY_BEANS
//        );
//        builder.failOnUnknownProperties(false);
//        return builder;
//    }

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager manager = new ConcurrentMapCacheManager("sites", "storages", "jobs");
        manager.setAllowNullValues(false);
        return manager;
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
