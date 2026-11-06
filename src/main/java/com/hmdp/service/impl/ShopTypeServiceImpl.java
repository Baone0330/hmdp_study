package com.hmdp.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.hmdp.utils.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 查询店铺类型
     *
     * @return
     */
    @Override
    public List<ShopType> queryTypeList() {
        // 1. 从redis中获取店铺类型
        String shopTypeJson = stringRedisTemplate.opsForValue().get(RedisConstants.CACHE_SHOP_TYPE_KEY);

        //2. 判断缓存是否存在
        if (StringUtils.isNotBlank(shopTypeJson)) {
            // 缓存存在 直接返回
            List<ShopType> shopType = JSONUtil.toList(shopTypeJson, ShopType.class);
            return shopType;
        }

        //3. 缓存不存在 查询数据库
        List<ShopType> shopTypeList = query().orderByAsc("sort").list();

        //4. 写入redis缓存
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_TYPE_KEY, JSONUtil.toJsonStr(shopTypeList), RedisConstants.CACHE_SHOP_TYPE_TTL, TimeUnit.MINUTES);

        //返回查询结果
        return shopTypeList;
    }
}
