package com.example.service;

import com.example.beans.CacheKeyConstants;
import com.example.util.JsonMapper;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;

@Service
@Slf4j
public class SysCacheService {

    @Resource(name = "redisPool")
    private RedisPool redisPool;

    public void saveCache(String toSavedValue, int timeoutSecond, CacheKeyConstants prefix) {
        saveCache(toSavedValue, timeoutSecond, prefix, null);
    }

    public void saveCache(String toSavedValue, int timeoutSecond, CacheKeyConstants prefix, String... keys) {
        if (toSavedValue == null) {
            return;
        }
        ShardedJedis shardedJedis = null;
        try {
            String key = generateCacheKey(prefix, keys);
            shardedJedis = redisPool.instance();
            shardedJedis.setex(key, timeoutSecond, toSavedValue);
        } catch (Exception e) {
            log.error("save cache exception,prefix={},keys={}", prefix, JsonMapper.objectToString(keys));
        } finally {
            redisPool.safedClose(shardedJedis);
        }
    }

    public String getFromCache(CacheKeyConstants prefix, String... keys) {
        ShardedJedis shardedJedis = null;
        try {
            String key = generateCacheKey(prefix, keys);
            shardedJedis = redisPool.instance();
            return shardedJedis.get(key);
        } catch (Exception e) {
            log.error("get value from cache exception,prefix={},keys={}", prefix, JsonMapper.objectToString(keys));
            return null;
        } finally {
            redisPool.safedClose(shardedJedis);
        }
    }

    private String generateCacheKey(CacheKeyConstants prefix, String... keys) {
        String key = prefix.name();
        if (keys != null && keys.length > 0) {
            key += "_" + Joiner.on("_").join(keys);
        }
        return key;
    }
}
