package com.gdut.repair.service.impl;

import com.gdut.repair.constant.RepairStatusEnum;
import com.gdut.repair.entity.Repair;
import com.gdut.repair.mapper.RepairMapper;
import com.gdut.repair.service.RepairService;
import com.gdut.repair.util.RegexUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.time.LocalDateTime;
import java.util.List;

public class RepairServiceImpl implements RepairService {
    // 统一使用同一个 SqlSessionFactory 创建会话。
    private final SqlSessionFactory factory;

    public RepairServiceImpl(SqlSessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public Repair createRepair(String userId, String dormRoom, String description) {
        // userId 这里承载的是学生账号，不能为空。
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }
        // 宿舍号必须满足 A-101 这种格式。
        if (!RegexUtil.isValidDormRoom(dormRoom)) {
            return null;
        }
        // 描述不能为空且长度受限。
        if (!RegexUtil.isValidDescription(description)) {
            return null;
        }

        // 自动提交模式：插入后立即提交。
        try (SqlSession session = factory.openSession(true)) {
            RepairMapper mapper = session.getMapper(RepairMapper.class);

            // 组装新报修单。
            Repair repair = new Repair();
            repair.setUserId(userId);
            repair.setDormRoom(dormRoom);
            repair.setDescription(description);
            // 新建单默认待处理。
            repair.setStatus(RepairStatusEnum.PENDING);
            repair.setCreateTime(LocalDateTime.now());
            repair.setUpdateTime(LocalDateTime.now());

            // 插入成功则返回对象（含数据库回填的 id）。
            int affected = mapper.insert(repair);
            return affected > 0 ? repair : null;
        }
    }

    @Override
    public List<Repair> getRepairsByUser(String userId) {
        // 只读查询可不开启自动提交。
        try (SqlSession session = factory.openSession()) {
            RepairMapper mapper = session.getMapper(RepairMapper.class);
            return mapper.findByUserId(userId);
        }
    }

    @Override
    public List<Repair> getAllRepairs() {
        try (SqlSession session = factory.openSession()) {
            RepairMapper mapper = session.getMapper(RepairMapper.class);
            return mapper.findAll();
        }
    }

    @Override
    public List<Repair> getRepairsByStatus(RepairStatusEnum status) {
        // status 为空时按“全部”处理，提升调用方容错。
        if (status == null) {
            return getAllRepairs();
        }
        try (SqlSession session = factory.openSession()) {
            RepairMapper mapper = session.getMapper(RepairMapper.class);
            return mapper.findByStatus(status.name());
        }
    }

    @Override
    public boolean cancelRepair(Long repairId, String currentUserId) {
        // 取消需要报修单ID和当前登录用户账号。
        if (repairId == null || currentUserId == null || currentUserId.trim().isEmpty()) {
            return false;
        }

        try (SqlSession session = factory.openSession(true)) {
            RepairMapper mapper = session.getMapper(RepairMapper.class);
            // 先查原始报修单，用于做权限和状态校验。
            Repair repair = mapper.findById(repairId);
            if (repair == null) {
                return false;
            }
            // 只能取消自己账号提交的单。
            if (!currentUserId.equals(repair.getUserId())) {
                return false;
            }
            // 已完成或已取消都不允许再次取消。
            if (repair.getStatus() == RepairStatusEnum.FINISHED || repair.getStatus() == RepairStatusEnum.CANCELED) {
                return false;
            }

            // 更新状态为 CANCELED，并刷新更新时间。
            return mapper.updateStatus(repairId, RepairStatusEnum.CANCELED.name(), LocalDateTime.now()) > 0;
        }
    }

    @Override
    public boolean updateStatus(Long repairId, RepairStatusEnum status) {
        // 管理员更新状态时，id 和状态都必须有效。
        if (repairId == null || status == null) {
            return false;
        }

        try (SqlSession session = factory.openSession(true)) {
            RepairMapper mapper = session.getMapper(RepairMapper.class);
            // 受影响行数 > 0 表示更新成功。
            return mapper.updateStatus(repairId, status.name(), LocalDateTime.now()) > 0;
        }
    }

    @Override
    public boolean deleteById(Long repairId) {
        // 删除前先做空值保护。
        if (repairId == null) {
            return false;
        }
        try (SqlSession session = factory.openSession(true)) {
            RepairMapper mapper = session.getMapper(RepairMapper.class);
            // 主键删除，返回是否删除成功。
            return mapper.deleteById(repairId) > 0;
        }
    }
}

