package com.hmdp;

import com.hmdp.entity.Shop;
import com.hmdp.service.IShopService;
import com.hmdp.utils.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@SpringBootTest
class HmDianPingApplicationTests {

    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private IShopService shopService;

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

    @Test
    void loadShopData() {
        // 1. 查询店铺信息
        List<Shop> list = shopService.list();

        // 2. 把店铺分组，按照typeId分组，typeId一致的放到一个集合
        Map<Long, List<Shop>> map = list.stream()
                .collect(Collectors.groupingBy(Shop::getTypeId));

        // 3. 分批完成写入Redis
        for (Map.Entry<Long, List<Shop>> entry : map.entrySet()) {

            // 3.1 获取类型id
            Long typeId = entry.getKey();
            String key = "shop:geo:" + typeId;

            // 3.2 获取同类型的店铺的集合
            List<Shop> value = entry.getValue();
            List<RedisGeoCommands.GeoLocation<String>> locations =
                    new ArrayList<>(value.size());

            // 3.3 写入Redis GEOADD key 经度 纬度 member
            for (Shop shop : value) {
                // stringRedisTemplate.opsForGeo().add(
                //     key,
                //     new Point(shop.getX(), shop.getY()),
                //     shop.getId().toString()
                // );

                locations.add(new RedisGeoCommands.GeoLocation<>(
                        shop.getId().toString(),
                        new Point(shop.getX(), shop.getY())
                ));
            }

            stringRedisTemplate.opsForGeo().add(key, locations);
        }
    }

}
