package com.gdut.repair.service;

import com.gdut.repair.constant.RepairStatusEnum;
import com.gdut.repair.entity.Repair;

import java.util.List;

public interface RepairService {
    // 学生创建报修单。
    Repair createRepair(String userId, String dormRoom, String description);

    // 学生查看自己的报修单。
    List<Repair> getRepairsByUser(String userId);

    // 管理员查看全部报修单。
    List<Repair> getAllRepairs();

    // 管理员按状态筛选报修单。
    List<Repair> getRepairsByStatus(RepairStatusEnum status);

    // 学生取消自己的报修单。
    boolean cancelRepair(Long repairId, String currentUserId);

    // 管理员更新报修状态。
    boolean updateStatus(Long repairId, RepairStatusEnum status);

    // 管理员删除报修单。
    boolean deleteById(Long repairId);
}

