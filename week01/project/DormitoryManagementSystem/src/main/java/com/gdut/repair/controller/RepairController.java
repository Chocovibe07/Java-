package com.gdut.repair.controller;

import com.gdut.repair.constant.RepairStatusEnum;
import com.gdut.repair.entity.Repair;
import com.gdut.repair.service.RepairService;

import java.util.List;

public class RepairController {
    // 依赖 RepairService，Controller 只负责编排，不写业务规则。
    private final RepairService repairService;

    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    // 创建报修单。
    public Repair createRepair(String userId, String dormRoom, String description) {
        return repairService.createRepair(userId, dormRoom, description);
    }

    // 查询某个用户（学生）自己的报修单列表。
    public List<Repair> getRepairsByUser(String userId) {
        return repairService.getRepairsByUser(userId);
    }

    // 查询全量报修单（管理员功能）。
    public List<Repair> getAllRepairs() {
        return repairService.getAllRepairs();
    }

    // 按状态筛选报修单（管理员功能）。
    public List<Repair> getRepairsByStatus(RepairStatusEnum status) {
        return repairService.getRepairsByStatus(status);
    }

    // 取消报修单（仅学生本人可取消，具体校验在 Service 层）。
    public boolean cancelRepair(Long repairId, String currentUserId) {
        return repairService.cancelRepair(repairId, currentUserId);
    }

    // 修改报修状态（管理员功能）。
    public boolean updateStatus(Long repairId, RepairStatusEnum status) {
        return repairService.updateStatus(repairId, status);
    }

    // 删除报修单（管理员功能）。
    public boolean deleteById(Long repairId) {
        return repairService.deleteById(repairId);
    }
}

