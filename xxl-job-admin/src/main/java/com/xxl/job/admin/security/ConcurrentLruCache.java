package com.xxl.job.admin.security;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

/**
 * @author Ice2Faith
 * @date 2024/5/25 14:10
 * @desc
 */

public class ConcurrentLruCache<K, V> {

    private final int sizeLimit;
    private final Function<K, V> generator;
    // for null key
    private final AtomicInteger nullCount = new AtomicInteger(0);
    private final AtomicReference<AtomicReference<V>> nullCache = new AtomicReference<>();
    // value for null value
    private final ConcurrentHashMap<K, AtomicReference<V>> cache = new ConcurrentHashMap<>();
    private final ConcurrentLinkedDeque<K> queue = new ConcurrentLinkedDeque<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private volatile int size;

    public ConcurrentLruCache(Function<K, V> generator) {
        this(-1, generator);
    }

    public ConcurrentLruCache(int sizeLimit, Function<K, V> generator) {
        if (sizeLimit < 0) {
            sizeLimit = 2048;
        }
        if (generator == null) {
            throw new IllegalArgumentException("generator function cannot be null");
        }
        this.sizeLimit = sizeLimit;
        this.generator = generator;
    }

    public V get(K key) {

        if (this.sizeLimit == 0) {
            // not cache mode, direct return
            return this.generator.apply(key);
        }

        if (key == null) {
            // process null key
            this.lock.writeLock().lock();
            try {
                nullCount.set(0);
                AtomicReference<V> cached = nullCache.get();
                if (cached != null) {
                    return cached.get();
                } else {
                    V ret = this.generator.apply(key);
                    nullCache.set(new AtomicReference<>(ret));
                    return ret;
                }
            } finally {
                this.lock.writeLock().lock();
            }

        } else {
            // remove null cache while exceeded size limit not access null
            nullCount.updateAndGet(v -> {
                int ret = v + 1;
                if (ret > this.sizeLimit) {
                    nullCache.set(null);
                }
                return ret;
            });
        }

        AtomicReference<V> cached = this.cache.get(key);
        if (cached != null) {
            // cache hit
            if (this.size < this.sizeLimit) {
                // not enough limit, direct return
                return cached.get();
            }

            this.lock.readLock().lock();

            try {
                if (this.queue.removeLastOccurrence(key)) {
                    this.queue.offer(key);
                }

                return cached.get();
            } finally {
                this.lock.readLock().unlock();
            }
        }

        // missing cache
        this.lock.writeLock().lock();

        try {
            cached = this.cache.get(key);
            if (cached == null) {
                V value = this.generator.apply(key);
                if (this.size == this.sizeLimit) {
                    K leastUsed = this.queue.poll();
                    if (leastUsed != null) {
                        this.cache.remove(leastUsed);
                    }
                }

                this.queue.offer(key);
                this.cache.put(key, new AtomicReference<>(value));
                this.size = this.cache.size();
                return value;
            }

            if (this.queue.removeLastOccurrence(key)) {
                this.queue.offer(key);
            }

            return cached.get();
        } finally {
            this.lock.writeLock().unlock();
        }


    }

    public boolean contains(K key) {
        if (key == null) {
            return nullCache.get() == null;
        }
        return this.cache.containsKey(key);
    }

    public boolean remove(K key) {
        if (key == null) {
            nullCache.set(null);
            return true;
        }
        this.lock.writeLock().lock();

        try {
            boolean wasPresent = this.cache.remove(key) != null;
            this.queue.remove(key);
            this.size = this.cache.size();
            return wasPresent;
        } finally {
            this.lock.writeLock().unlock();
        }

    }

    public void clear() {
        this.lock.writeLock().lock();

        try {
            this.cache.clear();
            this.queue.clear();
            this.size = 0;
            nullCache.set(null);
            nullCount.set(0);
        } finally {
            this.lock.writeLock().unlock();
        }

    }

    public int size() {
        return this.size;
    }

    public int sizeLimit() {
        return this.sizeLimit;
    }
}
