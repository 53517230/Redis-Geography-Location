package com.geo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * @author dangyicheng
 * @date 2022-04-21 14:06:28 星期四
 */
@EnableCaching
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
  @Bean
  public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
    RedisCacheConfiguration redisCacheConfiguration =
        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(2L));
    return RedisCacheManager.builder(
            RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
        .cacheDefaults(redisCacheConfiguration)
        .build();
  }

  @Bean
  public RedisTemplate<Object, Object> redisTemplate(
      RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
    /** 配置连接工厂 */
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    /** 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值（默认使用JDK的序列化方式） */
    Jackson2JsonRedisSerializer jackson2JsonRedisSerializer =
        new Jackson2JsonRedisSerializer(Object.class);
    ObjectMapper objectMapper = new ObjectMapper();
    /** 指定要序列化的域，field,get和set,以及修饰符范围，ANY是都有包括private和public */
    objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    /** 指定序列化输入的类型，类必须是非final修饰的，final修饰的类，比如String,Integer等会抛出异常 */
    objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
    // value序列化方式采用jackson*/
    redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
    // key采用String的序列化方式*/
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    // 对hash的key采用String的序列化方式*/
    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    /** 对hash的value采用jackson的序列化方式 */
    redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }
}
