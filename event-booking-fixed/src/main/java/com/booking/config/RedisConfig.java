package com.booking.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;

import org.springframework.data.redis.connection.RedisConnectionFactory;

import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/*
 * Redis configuration class.
 *
 * Handles:
 * - Redis connection setup
 * - RedisTemplate configuration
 * - CacheManager configuration
 * - JSON serialization/deserialization
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /*
     * Custom Redis ObjectMapper.
     *
     * Used for:
     * - JSON serialization
     * - LocalDateTime support
     */
    @Bean
    public ObjectMapper redisObjectMapper() {

        ObjectMapper objectMapper =
                new ObjectMapper();

        /*
         * Support Java 8 date/time types.
         */
        objectMapper.registerModule(
                new JavaTimeModule()
        );

        /*
         * Store dates in readable ISO format
         * instead of timestamps.
         * 
         * Without this Redis cannot 
         * serialize LocalDateTime properly
         */
        objectMapper.disable(
                SerializationFeature
                        .WRITE_DATES_AS_TIMESTAMPS
        );

        /*
         * Preserve class type information.
         *
         * Required when storing polymorphic
         * objects inside Redis cache.
         * 
         * Without this redis cannot determine the 
         * original type during derserializer
         */
        objectMapper.activateDefaultTyping(

                LaissezFaireSubTypeValidator.instance,

                ObjectMapper.DefaultTyping.NON_FINAL,

                JsonTypeInfo.As.PROPERTY
        );

        return objectMapper;
    }

    /*
     * Shared Redis serializer.
     *
     * Converts Java objects
     * to JSON and vice versa.
     * 
     * It is used by:
     *   RedisTemplate
     *   CacheManager
     */
    @Bean
    public GenericJackson2JsonRedisSerializer
    redisSerializer(
            ObjectMapper redisObjectMapper) {

        return new GenericJackson2JsonRedisSerializer(
                redisObjectMapper
        );
    }

    /*
     * RedisTemplate configuration.
     *
     * Used for:
     * - Manual Redis operations
     * - Storing custom objects
     * - Redis-based features
     */
    @Bean
    public RedisTemplate<String, Object>
    redisTemplate(

            RedisConnectionFactory connectionFactory,

            GenericJackson2JsonRedisSerializer serializer) {

        RedisTemplate<String, Object> template =
                new RedisTemplate<>();

        /*
         * Redis connection factory.
         * 
         * Connects RedisTemplate to Redis server.
         * 
         * Without this RedisTemplate cannot 
         * communicate with Redis
         */
        template.setConnectionFactory(
                connectionFactory
        );

        /*
         * String serializer for Redis keys.
         */
        template.setKeySerializer(
                new StringRedisSerializer()
        );

        /*
         * JSON serializer for values.
         */
        template.setValueSerializer(
                serializer
        );

        /*
         * Serializer for hash keys.
         */
        template.setHashKeySerializer(
                new StringRedisSerializer()
        );

        /*
         * Serializer for hash values.
         */
        template.setHashValueSerializer(
                serializer
        );

        /*
         * Finalize template initialization.
         * 
         * Without this template may 
         * not initialize properly
         */
        template.afterPropertiesSet();

        return template;
    }

    /*
     * Spring CacheManager configuration.
     *
     * Enables:
     * - @Cacheable
     * - @CachePut
     * - @CacheEvict
     *
     * Uses Redis as backend cache store.
     */
    @Bean
    public CacheManager cacheManager(

            RedisConnectionFactory connectionFactory,

            GenericJackson2JsonRedisSerializer serializer) {

        RedisCacheConfiguration configuration =

                RedisCacheConfiguration
                        .defaultCacheConfig()

                        /*
                         * Cache expiration time.
                         */
                        .entryTtl(
                                Duration.ofMinutes(10)
                        )

                        /*
                         * Avoid storing null values.
                         */
                        .disableCachingNullValues()

                        /*
                         * Serialize cache keys as strings.
                         * 
                         * Ensures cache keys are stored as readable strings.
                         */
                        .serializeKeysWith(

                                RedisSerializationContext
                                        .SerializationPair
                                        .fromSerializer(

                                                new StringRedisSerializer()
                                        )
                        )

                        /*
                         * Serialize cache values as JSON.
                         * 
                         * Ensures cache values are stored as JSON.
                         */
                        .serializeValuesWith(

                                RedisSerializationContext
                                        .SerializationPair
                                        .fromSerializer(
                                                serializer
                                        )
                        );

        /*
         * Build Redis cache manager.
         * 
         * Builds Spring CacheManager using Redis backend.
         * Spring Cache ↔ Redis
         */
        return RedisCacheManager
                .builder(connectionFactory)

                .cacheDefaults(configuration)

                .build();
    }
}
/*
| Method                | Purpose                       |
| --------------------- | ----------------------------- |
| `redisObjectMapper()` | JSON conversion configuration |
| `redisSerializer()`   | Shared Redis serializer       |
| `redisTemplate()`     | Manual Redis operations       |
| `cacheManager()`      | Spring cache integration      |
*/
