package ru.aliascage.movie_service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.aliascage.movie_service.model.VoteAverageResponse;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public IMap<String, VoteAverageResponse> averageMap(@Autowired HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getMap("dataCache");
    }

    @Bean
    @ConfigurationProperties(prefix = "executor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        return executor;
    }
}
