package com.atguigu.gmall.index.service.impl;

import com.atguigu.gmall.index.service.LockTestService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Author: Administrator
 * Create: 2021/2/14
 **/
@Service
public class LockTestServiceImpl implements LockTestService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    private Timer timer;

    // 测试无锁
    public void testNoLock() {
        String number = this.stringRedisTemplate.opsForValue().get("number");
        if (number == null) {
            return;
        }
        int num = Integer.parseInt(number);
        this.stringRedisTemplate.opsForValue().set("number", String.valueOf(++num));
    }

    // 测试本地锁
    @Override
    public synchronized void testLocalLock() {
        // 查询redis中的num值
        String value = this.stringRedisTemplate.opsForValue().get("num");
        // 没有该值return
        if (StringUtils.isBlank(value)) {
            return;
        }
        // 有值就转成成int
        int num = Integer.parseInt(value);
        // 把redis中的num值+1
        this.stringRedisTemplate.opsForValue().set("num", String.valueOf(++num));
    }

    // 测试Redis基本锁
    public void testRedisBaseLock() throws InterruptedException {
        String uuid = UUID.randomUUID().toString().replace("-", "");

        Boolean doLock = false;
        do {
            doLock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid);
        } while (!doLock);

        // 自增操作
        String number = this.stringRedisTemplate.opsForValue().get("number");
        if (number == null) {
            stringRedisTemplate.opsForValue().set("number", "1");
            return;
        }
        int num = Integer.parseInt(number);
        this.stringRedisTemplate.opsForValue().set("number", String.valueOf(++num));

        // 删除锁操作，判断是否是自己的锁，如果是，则删掉
        String lock = stringRedisTemplate.opsForValue().get("lock");
        if (uuid.equals(lock)) {
            stringRedisTemplate.delete("lock");
        }
    }

    // 测试Redis 超时锁
    public void testRedisTimeoutLock() throws InterruptedException {
        String uuid = UUID.randomUUID().toString().replace("-", "");

        Boolean doLock = false;
        do {
            doLock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 30l, TimeUnit.SECONDS);
        } while (!doLock);

        // 自增操作
        String number = this.stringRedisTemplate.opsForValue().get("number");
        if (number == null) {
            stringRedisTemplate.opsForValue().set("number", "1");
            return;
        }
        int num = Integer.parseInt(number);
        this.stringRedisTemplate.opsForValue().set("number", String.valueOf(++num));

        // 删除锁操作，判断是否是自己的锁，如果是，则删掉
        String lock = stringRedisTemplate.opsForValue().get("lock");
        if (uuid.equals(lock)) {
            stringRedisTemplate.delete("lock");
        }
    }

    // 测试 Redis 使用Lua脚本删除锁操作
    public void testRedisLuaLock() throws InterruptedException {
        String uuid = UUID.randomUUID().toString().replace("-", "");

        Boolean doLock = false;
        do {
            doLock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 30l, TimeUnit.SECONDS);
        } while (!doLock);

        // 自增操作
        String number = this.stringRedisTemplate.opsForValue().get("number");
        if (number == null) {
            stringRedisTemplate.opsForValue().set("number", "1");
            return;
        }
        int num = Integer.parseInt(number);
        this.stringRedisTemplate.opsForValue().set("number", String.valueOf(++num));

        // lua 脚本删除锁操作
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript redisScript = new DefaultRedisScript<>(luaScript, Long.class);
        // redisScript.setResultType(Long.class);
        stringRedisTemplate.execute(redisScript, Arrays.asList("lock"), uuid);
    }

    // 测试 Redis 可重入锁
    public void testRedisLuaReentrantLock() throws InterruptedException {
        // 生成UUID 作为自己锁的唯一标识
        String uuid = UUID.randomUUID().toString().replace("-", "");

        // 加锁
        tryLock("lock", uuid, "10");

        // 自增操作
        // Thread.sleep(30*1000);
        String number = this.stringRedisTemplate.opsForValue().get("number");
        if (number == null) {
            stringRedisTemplate.opsForValue().set("number", "1");
            return;
        }
        int num = Integer.parseInt(number);
        this.stringRedisTemplate.opsForValue().set("number", String.valueOf(++num));

        // 解锁
        releaseLock("lock", uuid);
    }

    @Override
    public void testRedissonLock() {
        // 通过Redisson加锁
        RLock lock = redissonClient.getLock("lock");

        // 加锁
        lock.lock();

        // 自增操作
        String number = this.stringRedisTemplate.opsForValue().get("number");
        if (number == null) {
            stringRedisTemplate.opsForValue().set("number", "1");
            return;
        }
        int num = Integer.parseInt(number);
        this.stringRedisTemplate.opsForValue().set("number", String.valueOf(++num));

        // 解锁
        lock.unlock();
    }

    private Boolean tryLock(String lockName, String uuid, String expire) {
        // lua脚本 自旋加锁
        Boolean doLock = false;
        do {
            String incrbyScript = "if(redis.call('exists', KEYS[1]) == 0 or redis.call('hexists', KEYS[1], ARGV[1]) == 1) " +
                                          "then " +
                                          "  redis.call('hincrby', KEYS[1], ARGV[1], 1) " +
                                          "  redis.call('expire', KEYS[1], ARGV[2]) " +
                                          "  return 1 " +
                                          "else " +
                                          "  return 0 " +
                                          "end";
            doLock = stringRedisTemplate.execute(new DefaultRedisScript<>(incrbyScript, Boolean.class), Arrays.asList(lockName), uuid, expire)/* == 1 ? true : false*/;
        } while (!doLock);
        // 开启定时任务
        autoRenewExpire(lockName, uuid, expire);
        return true;
    }

    private void autoRenewExpire(String lockName, String uuid, String expire) {

        timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String renewScript = "if(redis.call('hexists', KEYS[1], ARGV[1]) == 1) " +
                                             "then " +
                                             "  return redis.call('expire', KEYS[1], ARGV[2]) " +
                                             "else " +
                                             "  return 0 " +
                                             "end";
                Boolean success = stringRedisTemplate.execute(new DefaultRedisScript<>(renewScript, Long.class), Arrays.asList(lockName), uuid, expire) == 1 ? true : false;
                if (success) {
                    System.out.println("【" + lockName + ":" + uuid + "】自动续期，续期时长为" + expire + "秒");
                } else {
                    System.out.println("锁已被释放，停止定时任务！");
                    timer.cancel();
                }
            }
        }, Long.parseLong(expire) * 2 / 3 * 1000, Long.parseLong(expire) * 2 / 3 * 1000);
    }

    private Boolean releaseLock(String lockName, String uuid) {
        // lua 脚本删除锁操作
        String decrbyScript = "if(redis.call('hexists', KEYS[1], ARGV[1]) == 0) " +
                                      "then " +
                                      "     return nil " +
                                      "elseif(redis.call('hincrby', KEYS[1], ARGV[1], -1) == 0) " +
                                      "     then return redis.call('del', KEYS[1]) " +
                                      "else " +
                                      "     return 0 " +
                                      "end";
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<>(decrbyScript, Long.class), Arrays.asList("lock"), uuid);
        if (result == null) {
            throw new IllegalMonitorStateException("attempt to unlock lock, not locked by lockName: " + lockName + " with request: " + uuid);
        }
        return true;
    }

}
