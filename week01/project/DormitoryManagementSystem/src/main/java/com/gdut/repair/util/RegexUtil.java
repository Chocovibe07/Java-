package com.gdut.repair.util;

import java.util.regex.Pattern;

public final class RegexUtil {
    // 通用用户名规则：4-20 位，字母/数字/下划线。
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");
    // 学号规则：以 3125 或 3225 开头，总计 10 位。
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^(3125|3225)\\d{6}$");
    // 维修人员工号规则：以 0025 开头，总计 10 位。
    private static final Pattern STAFF_ID_PATTERN = Pattern.compile("^0025\\d{6}$");
    // 宿舍号规则：楼栋字母 + '-' + 三位房间号，例如 A-101。
    private static final Pattern DORM_ROOM_PATTERN = Pattern.compile("^[A-Z]-\\d{3}$");

    private RegexUtil() {
        // 工具类不允许实例化。
    }

    public static boolean isValidUsername(String username) {
        // 非空且满足 USERNAME_PATTERN 即为合法。
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    public static boolean isValidStudentId(String studentId) {
        // 学号格式校验。
        return studentId != null && STUDENT_ID_PATTERN.matcher(studentId).matches();
    }

    public static boolean isValidStaffId(String staffId) {
        // 工号格式校验。
        return staffId != null && STAFF_ID_PATTERN.matcher(staffId).matches();
    }

    public static boolean isValidPassword(String password) {
        // 当前规则仅要求长度至少 6。
        return password != null && password.length() >= 6;
    }

    public static boolean isValidDormRoom(String dormRoom) {
        // 宿舍号格式校验。
        return dormRoom != null && DORM_ROOM_PATTERN.matcher(dormRoom).matches();
    }

    public static boolean isValidDescription(String description) {
        // 报修描述不能为空白，且长度不超过 200。
        return description != null && !description.trim().isEmpty() && description.length() <= 200;
    }
}

