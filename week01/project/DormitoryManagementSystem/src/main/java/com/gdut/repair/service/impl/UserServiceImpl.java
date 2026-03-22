package com.gdut.repair.service.impl;

import com.gdut.repair.entity.User;
import com.gdut.repair.mapper.UserMapper;
import com.gdut.repair.service.UserService;
import com.gdut.repair.util.PasswordUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.time.LocalDateTime;

public class UserServiceImpl implements UserService {

    // MyBatis 会话工厂：用于创建 SqlSession 与数据库交互。
    private SqlSessionFactory factory;

    public UserServiceImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean register(String account, String password, Integer role) {

        // openSession(true) 表示开启自动提交，适合当前简单命令式场景。
        try (SqlSession session = factory.openSession(true)) {

            // 动态获取 Mapper 代理对象，通过接口直接调用 SQL。
            UserMapper mapper = session.getMapper(UserMapper.class);

            // 判断账号是否存在
            if (mapper.selectByAccount(account) != null) {
                return false;
            }

            // 组装待入库的用户对象。
            User user = new User();
            user.setAccount(account);
            // 密码不存明文，先做 MD5 加密。
            user.setPassword(PasswordUtil.encrypt(password));
            user.setRole(role);
            // 记录创建时间和更新时间。
            user.setCreateTime(LocalDateTime.now());
            user.setUpdateTime(LocalDateTime.now());

            // 执行插入。
            mapper.insert(user);

            return true;
        }
    }

    @Override
    public User login(String account, String password) {

        try (SqlSession session = factory.openSession(true)) {

            UserMapper mapper = session.getMapper(UserMapper.class);

            // 先按账号查用户。
            User user = mapper.selectByAccount(account);

            if (user == null) {
                return null;
            }

            // 校验密码（加密后比对）
            String encrypted = PasswordUtil.encrypt(password);

            if (!user.getPassword().equals(encrypted)) {
                return null;
            }

            return user;
        }
    }

    @Override
    public boolean bindDorm(Long userId, String dormBuilding, String dormRoom) {

        // 基础参数保护，避免无效 ID 进入数据库层。
        if (userId == null || userId <= 0) {
            return false;
        }

        try (SqlSession session = factory.openSession(true)) {

            UserMapper mapper = session.getMapper(UserMapper.class);

            // 返回受影响行数 > 0 代表更新成功。
            return mapper.bindDormById(userId, dormBuilding, dormRoom) > 0;
        }
    }
}

