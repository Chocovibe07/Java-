package com.gdut.repair.entity;

import com.gdut.repair.constant.RepairStatusEnum;

import java.time.LocalDateTime;

public class Repair {

	// 报修单主键ID。
	private Long id;
	// 提交报修的学生账号（对应 user.account）。
	private String userId;
	// 宿舍号（例如 A-101，当前可作展示/逻辑字段）。
	private String dormRoom;
	// 报修描述。
	private String description;
	// 报修状态枚举。
	private RepairStatusEnum status;
	// 创建时间。
	private LocalDateTime createTime;
	// 更新时间。
	private LocalDateTime updateTime;

	public Repair() {
	}

	public Repair(Long id, String userId, String dormRoom, String description, RepairStatusEnum status) {
		this.id = id;
		this.userId = userId;
		this.dormRoom = dormRoom;
		this.description = description;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDormRoom() {
		return dormRoom;
	}

	public void setDormRoom(String dormRoom) {
		this.dormRoom = dormRoom;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RepairStatusEnum getStatus() {
		return status;
	}

	public void setStatus(RepairStatusEnum status) {
		this.status = status;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public LocalDateTime getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(LocalDateTime updateTime) {
		this.updateTime = updateTime;
	}
}

