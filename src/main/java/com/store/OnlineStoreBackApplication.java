package com.store;

import com.store.webconfig.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
@EnableScheduling
public class OnlineStoreBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineStoreBackApplication.class, args);
    }
}