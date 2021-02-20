package com.atguigu.gmall.index.service;

/**
 * Author: Administrator
 * Create: 2021/2/14
 **/
public interface LockTestService {

    void testNoLock();

    void testLocalLock();

    void testRedisBaseLock() throws InterruptedException;

    void testRedisTimeoutLock() throws InterruptedException;

    void testRedisLuaLock() throws InterruptedException;

    void testRedisLuaReentrantLock() throws InterruptedException;

    void testRedissonLock();

}
