package com.hmdp.utils;

import com.hmdp.dto.UserDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// 登录拦截器
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1. 获取session
        HttpSession session = request.getSession();
        //2. 获取session中的用户
        Object user = session.getAttribute("user");
        //3. 判断用户是否存在
        if (user == null) {
            //4. 不存在，拦截，返回401状态码
            response.setStatus(401);
            return false;
        }
        //5. 存在，保存用户信息到ThreadLocal
        //这里留意一下，我们拿到的项目传入的类型是UserDTO，需要new一个 再用BeanUtils拷贝
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        UserHolder.saveUser(userDTO);
        //6. 放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        UserHolder.removeUser();
    }
}
