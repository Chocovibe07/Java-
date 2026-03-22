package com.gdut.repair;

import com.gdut.repair.constant.RepairStatusEnum;
import com.gdut.repair.entity.Repair;
import com.gdut.repair.entity.User;
import com.gdut.repair.service.RepairService;
import com.gdut.repair.service.UserService;
import com.gdut.repair.service.impl.RepairServiceImpl;
import com.gdut.repair.service.impl.UserServiceImpl;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.io.Reader;
import java.util.List;

public class RepairServiceImplTest {

    @Test
    public void shouldSubmitQueryAndUpdateRepair() throws Exception {
        SqlSessionFactory factory = buildFactory();
        UserService userService = new UserServiceImpl(factory);
        RepairService repairService = new RepairServiceImpl(factory);

        String account = "3125" + String.format("%06d", System.currentTimeMillis() % 1000000);
        userService.register(account, "abc12345", 1);
        User user = userService.login(account, "abc12345");

        Repair repair = repairService.createRepair(user.getAccount(), "A-101", "Desk lamp is broken");
        List<Repair> repairsByUser = repairService.getRepairsByUser(user.getAccount());
        boolean updated = repairService.updateStatus(repair.getId(), RepairStatusEnum.FINISHED);
        boolean canceledAfterFinished = repairService.cancelRepair(repair.getId(), user.getAccount());

        Assert.assertNotNull(repair);
        Assert.assertFalse(repairsByUser.isEmpty());
        Assert.assertTrue(updated);
        Assert.assertFalse(canceledAfterFinished);
    }

    private SqlSessionFactory buildFactory() throws Exception {
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        return new SqlSessionFactoryBuilder().build(reader);
    }
}

