package com.gdut.repair.mapper;

import com.gdut.repair.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

	// 按账号查询用户（用于注册查重与登录）。
	User selectByAccount(String account);

	// 按主键查询用户（用于扩展场景）。
	User selectById(Long id);

	// 插入新用户。
	int insert(User user);

	// 绑定宿舍：更新 dorm_building / dorm_room 字段。
	int bindDormById(@Param("id") Long id,
	                 @Param("dormBuilding") String dormBuilding,
	                 @Param("dormRoom") String dormRoom);
}

