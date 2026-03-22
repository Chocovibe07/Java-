package com.gdut.repair;

import com.gdut.repair.entity.User;
import com.gdut.repair.service.UserService;
import com.gdut.repair.service.impl.UserServiceImpl;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.Reader;

public class UserServiceImplTest {

    @Test
    public void shouldRegisterAndLoginUser() throws Exception {
        UserService userService = new UserServiceImpl(buildFactory());

        String account = "3125" + String.format("%06d", System.currentTimeMillis() % 1000000);
        boolean registered = userService.register(account, "abc12345", 1);
        User loggedIn = userService.login(account, "abc12345");

        Assert.assertTrue(registered);
        Assert.assertNotNull(loggedIn);
        Assert.assertEquals(account, loggedIn.getAccount());
        Assert.assertEquals(Integer.valueOf(1), loggedIn.getRole());
    }

    private SqlSessionFactory buildFactory() throws Exception {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        return new SqlSessionFactoryBuilder().build(reader);
    }
}

