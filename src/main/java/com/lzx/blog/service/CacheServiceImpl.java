package com.lzx.blog.service;

import com.lzx.blog.popj.VerCode;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 缓存Service实现类
 *
 * @author Administrator
 */
@Service
@Transactional
public class CacheServiceImpl implements CacheService {

    public static final String CACHE_NAME = "local";

    /**
     * 将商品信息保存到本地缓存中
     *
     * @param verCode
     * @return
     */
    @CachePut(value = CACHE_NAME, key = "#verCode.getEmail()")
    public VerCode SaveLocalEmail(VerCode verCode) {
        return verCode;
    }

    /**
     * 从本地缓存中获取商品信息
     *
     * @param email
     * @return
     */
    @Cacheable(value = CACHE_NAME, key = "#email")
    public VerCode GetLocalEmail(String email) {
        return null;
    }
}