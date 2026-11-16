package com.hmdp;

import com.hmdp.utils.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class HmDianPingApplicationTests {

    @Resource
    private RedisIdWorker redisIdWorker;

    // 创建一个固定大小为 500 的线程池
    private ExecutorService es = Executors.newFixedThreadPool(500);

    @Test
    void testIdWorker() throws InterruptedException {

        // 创建 CountDownLatch，初始计数为 300
        CountDownLatch latch = new CountDownLatch(300);

        // 定义一个任务
        Runnable task = () -> {

            // 每个任务循环生成 100 个 ID
            for (int i = 0; i < 100; i++) {

                // 调用 RedisIdWorker 生成全局唯一 ID
                long id = redisIdWorker.nextId("order");

                System.out.println("id = " + id);
            }

            // 当前任务执行完毕，计数减 1
            latch.countDown();
        };

        // 记录开始时间
        long begin = System.currentTimeMillis();

        // 提交 300 个任务到线程池
        for (int i = 0; i < 300; i++) {
            es.submit(task);
        }

        // 等待所有任务执行完毕
        latch.await();

        // 记录结束时间
        long end = System.currentTimeMillis();

        // 输出总耗时
        System.out.println("time = " + (end - begin));
    }

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testRedis() {
        stringRedisTemplate.opsForValue().set("test", "Redis7.4");
        String value = stringRedisTemplate.opsForValue().get("test");
        System.out.println(value);
    }

}
