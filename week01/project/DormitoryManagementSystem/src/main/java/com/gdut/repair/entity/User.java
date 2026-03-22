package com.gdut.repair.entity;

import java.time.LocalDateTime;

public class User {

    // 用户主键ID。
    private Long id;
    // 登录账号（学生为学号，维修人员为工号）。
    private String account;
    // 密码摘要（MD5），不存明文。
    private String password;
    // 角色编号：1学生，2维修/管理员。
    private Integer role; // 1学生 2管理员
    // 宿舍楼栋，如 A。
    private String dormBuilding;
    // 宿舍房间号，如 101。
    private String dormRoom;
    // 创建时间。
    private LocalDateTime createTime;
    // 最后更新时间。
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getDormBuilding() {
        return dormBuilding;
    }

    public void setDormBuilding(String dormBuilding) {
        this.dormBuilding = dormBuilding;
    }

    public String getDormRoom() {
        return dormRoom;
    }

    public void setDormRoom(String dormRoom) {
        this.dormRoom = dormRoom;
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

