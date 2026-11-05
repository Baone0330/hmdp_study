package com.hmdp.utils;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// 登录拦截器
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        //1. 获取session
//        HttpSession session = request.getSession();
//        //2. 获取session中的用户
//        Object user = session.getAttribute("user");
//        //3. 判断用户是否存在
//        if (user == null) {
//            //4. 不存在，拦截，返回401状态码
//            response.setStatus(401);
//            return false;
//        }
//        //5. 存在，保存用户信息到ThreadLocal
//        //这里留意一下，我们拿到的项目传入的类型是UserDTO，需要new一个 再用BeanUtils拷贝
//        UserDTO userDTO = new UserDTO();
//        BeanUtils.copyProperties(user, userDTO);
//        UserHolder.saveUser(userDTO);

//        // 1. 获取token
//        String token = request.getHeader("authorization");
//        if (token == null) {
//            //不存在，拦截，返回 401 状态码
//            response.setStatus(401);
//            return false;
//        }
//        //2. 基于token获取Redis中的用户
//        String key = RedisConstants.LOGIN_USER_KEY + token;
//        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);
//        //3. 判断用户是否存在
//        if (userMap.isEmpty()) {
//            //4. 不存在，拦截，返回 401 状态码
//            response.setStatus(401);
//            return false;
//        }
//        //5. 将查询到的Hash数据转为UserDTO对象
//        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
//        //6. 保存用户信息到ThreadLocal
//        UserHolder.saveUser(userDTO);
//        //7. 刷新token有效期
//        stringRedisTemplate.expire(key, RedisConstants.LOGIN_USER_TTL, TimeUnit.MINUTES);
        //1. 判断是否拦截（ThreadLocal中是否有用户）
        if (UserHolder.getUser() == null) {
            //不存在，拦截，返回 401 状态码
            response.setStatus(401);
            return false;
        }
        // 存在 则放行
        return true;
    }
}
