package com.gdut.repair.controller;

import com.gdut.repair.entity.User;
import com.gdut.repair.service.UserService;
import com.gdut.repair.util.RegexUtil;

import java.util.Scanner;

public class UserController {

    // 依赖用户业务层，Controller 只负责输入输出与流程编排。
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void register(Scanner scanner) {

        System.out.println("===== 用户注册 =====");
        // role: 1 学生，2 维修人员。
        Integer role;
        // account: 学号/工号。
        String account;
        // password: 明文密码，Service 层会加密后入库。
        String password;

        // 外层循环：角色选择步骤。
        roleStep:
        while (true) {
            System.out.println("请选择角色（1-学生，2-维修人员，0-返回上一步）：");
            System.out.print("> ");
            String roleInput = scanner.nextLine().trim();
            if ("0".equals(roleInput)) {
                // 从注册页返回主菜单。
                return;
            }
            try {
                // 尝试把字符串角色转成整数。
                role = Integer.valueOf(roleInput);
            } catch (NumberFormatException e) {
                System.out.println("角色输入无效，请输入1或2。\n");
                continue;
            }
            // 只允许 1 或 2。
            if (role != 1 && role != 2) {
                System.out.println("角色输入无效，请输入1或2。\n");
                continue;
            }

            // 中层循环：账号输入步骤（学生输入学号，维修输入工号）。
            while (true) {
                if (role == 1) {
                    System.out.println("请输入学号（前缀3125或3225，输入0返回上一步）：");
                } else {
                    System.out.println("请输入工号（前缀0025，输入0返回上一步）：");
                }
                System.out.print("> ");
                account = scanner.nextLine().trim();

                if ("0".equals(account)) {
                    // 从账号步骤回到角色步骤。
                    continue roleStep;
                }

                // 按角色选择不同正则规则。
                boolean accountOk = role == 1
                        ? RegexUtil.isValidStudentId(account)
                        : RegexUtil.isValidStaffId(account);
                if (!accountOk) {
                    if (role == 1) {
                        System.out.println("学号格式错误，需为10位数字且前缀为3125或3225。\n");
                    } else {
                        System.out.println("工号格式错误，需为10位数字且前缀为0025。\n");
                    }
                    continue;
                }

                // 内层循环：密码与确认密码步骤。
                while (true) {
                    System.out.println("请输入密码（输入0返回上一步）：");
                    System.out.print("> ");
                    password = scanner.nextLine().trim();
                    if ("0".equals(password)) {
                        // 从密码步骤回到账号步骤。
                        break;
                    }
                    // 密码长度至少 6。
                    if (!RegexUtil.isValidPassword(password)) {
                        System.out.println("密码格式错误，至少6位。\n");
                        continue;
                    }

                    System.out.println("请确认密码（输入0返回上一步）：");
                    System.out.print("> ");
                    String confirmPassword = scanner.nextLine().trim();
                    if ("0".equals(confirmPassword)) {
                        // 从确认步骤回到密码输入步骤。
                        continue;
                    }
                    // 两次密码必须一致。
                    if (!password.equals(confirmPassword)) {
                        System.out.println("两次输入的密码不一致，请重新输入密码。\n");
                        continue;
                    }

                    // 提交注册：Service 负责查重、加密和入库。
                    boolean success = userService.register(account, password, role);

                    if (success) {
                        System.out.println("注册成功！请返回主界面登录。");
                    } else {
                        System.out.println("账号已存在！");
                    }
                    // 注册流程结束，返回主菜单。
                    return;
                }
            }
        }
    }

    /**
     * 登录流程：支持逐步返回。
     */
    public User login(Scanner scanner) {

        System.out.println("===== 用户登录 =====");
        while (true) {
            System.out.println("请输入账号（输入0返回上一步）：");
            System.out.print("> ");
            String account = scanner.nextLine().trim();
            if ("0".equals(account)) {
                // 返回主菜单。
                return null;
            }

            // 当前账号下循环输入密码，允许返回到账号输入步骤。
            while (true) {
                System.out.println("请输入密码（输入0返回上一步）：");
                System.out.print("> ");
                String password = scanner.nextLine().trim();
                if ("0".equals(password)) {
                    // 返回账号输入步骤。
                    break;
                }

                // Service 层完成账号查询和密码比对。
                User user = userService.login(account, password);

                if (user == null) {
                    System.out.println("账号或密码错误！");
                } else {
                    System.out.println("登录成功！角色：" + toRoleName(user.getRole()));
                }

                // 成功返回用户对象；失败返回 null。
                return user;
            }
        }
    }

    /**
     * 绑定宿舍到当前登录用户。
     */
    public boolean bindDorm(User currentUser, Scanner scanner) {
        if (currentUser == null || currentUser.getId() == null) {
            System.out.println("请先登录后再绑定宿舍。");
            return false;
        }

        while (true) {
            System.out.println("请输入宿舍号（格式如 A-101，输入0返回上一步）：");
            System.out.print("> ");
            String dormInput = scanner.nextLine().trim().toUpperCase();

            if ("0".equals(dormInput)) {
                // 返回学生菜单。
                return false;
            }

            // 宿舍格式必须是 A-101 这种格式。
            if (!RegexUtil.isValidDormRoom(dormInput)) {
                System.out.println("宿舍格式错误，请按 A-101 重新输入。\n");
                continue;
            }

            // 拆分宿舍号：building=A, room=101。
            String[] parts = dormInput.split("-");
            String dormBuilding = parts[0];
            String dormRoom = parts[1];

            // 调用 Service 更新 user 表的 dorm_building/dorm_room。
            boolean success = userService.bindDorm(currentUser.getId(), dormBuilding, dormRoom);
            if (success) {
                // 同步更新内存中的 currentUser，避免本次会话里数据不一致。
                currentUser.setDormBuilding(dormBuilding);
                currentUser.setDormRoom(dormRoom);
                System.out.println("宿舍绑定成功：" + dormInput);
            } else {
                System.out.println("宿舍绑定失败，请稍后重试。");
            }
            return success;
        }
    }

    /**
     * 将数据库角色编号转成中文展示文本。
     */
    private String toRoleName(Integer role) {
        if (role != null && role == 1) {
            return "学生";
        }
        if (role != null && role == 2) {
            return "维修人员";
        }
        return "未知";
    }
}
