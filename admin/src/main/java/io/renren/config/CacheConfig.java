package io.renren.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.renren.modules.ltt.entity.CdStaticProxyEntity;
import io.renren.modules.sys.entity.ProjectWorkEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean(value = "caffeineCacheCode")
    public Cache<String, String> caffeineCacheCode() {
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后两个小时后过期
                .expireAfterWrite(7200, TimeUnit.DAYS)
                // 初始的缓存空间大小
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(1000)
                .build();
    }

    @Bean(value = "caffeineCacheListCdStaticProxyEntity")
    public Cache<String, Queue<CdStaticProxyEntity>> caffeineCacheListCdStaticProxyEntity() {
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后两个小时后过期
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // 初始的缓存空间大小
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(1000)
                .build();
    }

    @Bean(value = "caffeineCacheListString")
    public Cache<String, Queue<String>> caffeineCacheListString() {
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后两个小时后过期
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // 初始的缓存空间大小
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(1000)
                .build();
    }

    @Bean(value = "caffeineCacheProjectWorkEntity")
    public Cache<String, ProjectWorkEntity> caffeineCacheProjectWorkEntity() {
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后两个小时后过期
                .expireAfterWrite(7200, TimeUnit.DAYS)
                // 初始的缓存空间大小
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(1000)
                .build();
    }

}
