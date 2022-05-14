package io.ddd.framework.acl.impl.cached;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.ddd.framework.acl.cache.CacheService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 缺省缓存实现,可自行考虑redis,memcached等中间件
 */
@Service
public class AuthCachedImpl implements CacheService<String,Object>, InitializingBean {

    @Value("${jwt.token-expire-time:60}")
    private Integer jwtTokenExpireTime;

    private Cache<String, Object> cached;

    @Override
    public Object get(String key) {
        return cached.getIfPresent(key);
    }

    @Override
    public void set(String key, Object value) {
        cached.put(key,value);
    }

    @Override
    public void set(String s, Object o, Long expire, TimeUnit timeUnit) {

    }

    @Override
    public void afterPropertiesSet() {
        cached = CacheBuilder.newBuilder().expireAfterAccess(jwtTokenExpireTime, TimeUnit.MINUTES).build();
    }
}