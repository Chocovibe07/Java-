package com.gdut.repair.service;

import com.gdut.repair.entity.User;

public interface UserService {

    // 注册新用户，成功返回 true，账号重复返回 false。
    boolean register(String account, String password, Integer role);

    // 登录校验，成功返回用户对象，失败返回 null。
    User login(String account, String password);

    // 绑定宿舍到指定用户，成功返回 true。
    boolean bindDorm(Long userId, String dormBuilding, String dormRoom);

}

