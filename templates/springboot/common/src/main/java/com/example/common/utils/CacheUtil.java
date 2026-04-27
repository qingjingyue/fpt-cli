package com.example.common.utils;

import cn.hutool.cache.impl.TimedCache;

import java.util.concurrent.TimeUnit;


///  缓存工具类
public class CacheUtil extends cn.hutool.cache.CacheUtil {

    /// 创建缓存，默认5秒过期
    private static final TimedCache<String, String> timedCache;

    static {
        timedCache = CacheUtil.newTimedCache(5000);
        // 启动定时任务，每1秒清理一次过期条目，注释此行首次启动仍会清理过期条目
        timedCache.schedulePrune(1000);
    }

    public static void put(String key, String value, long timeout, TimeUnit timeUnit) {
        // 例如 TimeUnit.SECONDS.toMillis(5) → 5000（5秒=5000毫秒）
        timedCache.put(key, value, timeout * timeUnit.toMillis(1));
    }

    public static String get(String key) {
        return timedCache.get(key);
    }

    public static void remove(String key) {
        timedCache.remove(key);
    }
}
