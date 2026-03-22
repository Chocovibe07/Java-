package com.gdut.repair.mapper;

import com.gdut.repair.entity.Repair;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RepairMapper {
    // 新建报修单。
    int insert(Repair repair);

    // 查询某个学生账号自己的报修单。
    List<Repair> findByUserId(String userId);

    // 查询全部报修单。
    List<Repair> findAll();

    // 按状态筛选报修单。
    List<Repair> findByStatus(String status);

    // 按主键查询单条报修单。
    Repair findById(Long id);

    // 更新状态并写入 updateTime。
    int updateStatus(@Param("id") Long id,
                     @Param("status") String status,
                     @Param("updateTime") LocalDateTime updateTime);

    // 按主键删除报修单。
    int deleteById(Long id);

    // 统计 repair.student_id 中仍是纯数字的记录数（旧数据特征）。
    int countNumericStudentIdRows();

    // 统计可通过 user.id 匹配并转换成账号的记录数。
    int countConvertibleRows();

    // 将 student_id 字段改为字符串类型，便于存储学生账号。
    int alterStudentIdColumnToVarchar();

    // 一次性迁移：把 repair.student_id 的旧数值ID替换成 user.account。
    int migrateStudentIdToAccount();

    // 统计 repair.student_id 无法匹配任何 user.account 的记录数（迁移后核对用）。
    int countRowsNotMappedToAccount();
}

