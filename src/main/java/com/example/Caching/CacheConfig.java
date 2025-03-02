package com.example.Caching;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("menuDirectorsCache");

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(40, TimeUnit.MINUTES)  // Время жизни кэша
        );

        return cacheManager;
    }
}
