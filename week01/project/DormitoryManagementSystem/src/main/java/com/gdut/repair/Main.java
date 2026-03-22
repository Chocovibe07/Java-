package com.gdut.repair;

import com.gdut.repair.constant.RepairStatusEnum;
import com.gdut.repair.controller.RepairController;
import com.gdut.repair.controller.UserController;
import com.gdut.repair.entity.Repair;
import com.gdut.repair.entity.User;
import com.gdut.repair.service.impl.RepairServiceImpl;
import com.gdut.repair.service.impl.UserServiceImpl;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.Reader;
import java.util.List;
import java.util.Scanner;

public class Main {

    /**
     * 程序入口：负责初始化 MyBatis、组装三层对象，并驱动控制台主菜单。
     */
    public static void main(String[] args) throws Exception {

        // 读取 MyBatis 全局配置（数据源、Mapper 映射等都在这里配置）。
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        // 根据配置构建 SqlSessionFactory，后续所有数据库会话都由它创建。
        SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);

        // 组装 Service 层（业务逻辑层）。
        UserServiceImpl userService = new UserServiceImpl(factory);
        RepairServiceImpl repairService = new RepairServiceImpl(factory);
        // 组装 Controller 层（控制台输入输出编排层）。
        UserController userController = new UserController(userService);
        RepairController repairController = new RepairController(repairService);

        // 保存当前登录用户；未登录时为 null。
        User currentUser = null;
        // 整个程序共享一个 Scanner，避免多 Scanner 争抢 System.in。
        Scanner scanner = new Scanner(System.in);

        // 主循环：持续显示主菜单，直到用户选择退出。
        while (true) {

            System.out.println("===========================");
            System.out.println("🏠 宿舍报修管理系统");
            System.out.println("===========================");
            System.out.println("1. 登录");
            System.out.println("2. 注册");
            System.out.println("3. 退出");
            System.out.println("请选择操作（输入 1-3）：");

            // 读取主菜单编号。
            int choice = scanner.nextInt();
            // nextInt 不会消费换行，这里手动吃掉，避免后续 nextLine 读到空串。
            scanner.nextLine();

            switch (choice) {
                case 1:
                    // 进入登录流程，登录成功后返回用户对象。
                    currentUser = userController.login(scanner);
                    if (currentUser != null) {
                        // role=1 视为学生；其余角色走管理员/维修人员菜单。
                        if (Integer.valueOf(1).equals(currentUser.getRole())) {
                            studentMenu(scanner, currentUser, userController, repairController);
                        } else {
                            adminMenu(scanner, repairController);
                        }
                    }
                    break;
                case 2:
                    // 进入注册流程（流程内部包含校验与返回上一步逻辑）。
                    userController.register(scanner);
                    break;
                case 3:
                    // 直接结束进程。
                    System.exit(0);
                default:
                    // 非法编号提示后继续下一轮。
                    System.out.println("输入无效，请输入 1-3。\n");
            }
        }
    }

    /**
     * 学生菜单：仅允许操作“自己的”宿舍与报修单。
     */
    private static void studentMenu(Scanner scanner, User currentUser, UserController userController, RepairController repairController) {
        while (true) {
            System.out.println("===== 学生功能 =====");
            System.out.println("1. 绑定宿舍");
            System.out.println("2. 创建报修单");
            System.out.println("3. 查看我的报修单");
            System.out.println("4. 取消报修单");
            System.out.println("0. 退出登录");
            System.out.print("请选择操作：");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    // 绑定宿舍信息到当前登录用户。
                    userController.bindDorm(currentUser, scanner);
                    break;
                case "2":
                    // 创建新报修单（默认待处理）。
                    createRepair(scanner, currentUser, repairController);
                    break;
                case "3":
                    // 只查当前登录用户的报修单。
                    printRepairs(repairController.getRepairsByUser(currentUser.getAccount()));
                    break;
                case "4":
                    System.out.print("请输入要取消的报修单ID（输入0返回上一步）：");
                    String cancelInput = scanner.nextLine().trim();
                    if ("0".equals(cancelInput)) {
                        break;
                    }
                    Long cancelId = parseLong(cancelInput);
                    if (cancelId == null) {
                        System.out.println("ID格式错误。");
                        break;
                    }
                    // 取消时会在 Service 层校验：必须是本人且状态不能已完成/已取消。
                    boolean canceled = repairController.cancelRepair(cancelId, currentUser.getAccount());
                    System.out.println(canceled ? "取消成功。" : "取消失败（只能取消自己的未完成报修单）。");
                    break;
                case "0":
                    // 返回主菜单（相当于退出登录）。
                    return;
                default:
                    System.out.println("输入无效，请重试。");
            }
        }
    }

    /**
     * 管理员菜单：可查看全部、按状态筛选、更新状态、删除报修单。
     */
    private static void adminMenu(Scanner scanner, RepairController repairController) {
        while (true) {
            System.out.println("===== 管理员功能 =====");
            System.out.println("1. 查看所有报修单");
            System.out.println("2. 按状态筛选");
            System.out.println("3. 修改报修状态");
            System.out.println("4. 删除报修单");
            System.out.println("0. 退出登录");
            System.out.print("请选择操作：");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    // 查询所有报修单。
                    printRepairs(repairController.getAllRepairs());
                    break;
                case "2":
                    // 读取状态编号并转换成枚举。
                    RepairStatusEnum filter = inputStatus(scanner);
                    if (filter != null) {
                        printRepairs(repairController.getRepairsByStatus(filter));
                    }
                    break;
                case "3":
                    System.out.print("请输入报修单ID（输入0返回上一步）：");
                    String repairIdInput = scanner.nextLine().trim();
                    if ("0".equals(repairIdInput)) {
                        break;
                    }
                    Long repairId = parseLong(repairIdInput);
                    if (repairId == null) {
                        System.out.println("ID格式错误。");
                        break;
                    }
                    // 读取要更新成的目标状态。
                    RepairStatusEnum status = inputStatus(scanner);
                    if (status == null) {
                        break;
                    }
                    // 执行状态更新（同时写入 update_time）。
                    boolean updated = repairController.updateStatus(repairId, status);
                    System.out.println(updated ? "状态更新成功。" : "状态更新失败。");
                    break;
                case "4":
                    System.out.print("请输入要删除的报修单ID（输入0返回上一步）：");
                    String deleteInput = scanner.nextLine().trim();
                    if ("0".equals(deleteInput)) {
                        break;
                    }
                    Long deleteId = parseLong(deleteInput);
                    if (deleteId == null) {
                        System.out.println("ID格式错误。");
                        break;
                    }
                    // 按主键删除报修单。
                    boolean deleted = repairController.deleteById(deleteId);
                    System.out.println(deleted ? "删除成功。" : "删除失败。");
                    break;
                case "0":
                    // 返回主菜单（相当于退出登录）。
                    return;
                default:
                    System.out.println("输入无效，请重试。");
            }
        }
    }

    /**
     * 创建报修单：从当前用户中拼接宿舍号，读取描述并提交。
     */
    private static void createRepair(Scanner scanner, User currentUser, RepairController repairController) {
        // 宿舍号由 user.dormBuilding + "-" + user.dormRoom 组合得到。
        String dormRoom = currentUser.getDormBuilding() != null && currentUser.getDormRoom() != null
                ? currentUser.getDormBuilding() + "-" + currentUser.getDormRoom()
                : null;

        // 未绑定宿舍时禁止提单，先引导用户绑定。
        if (dormRoom == null) {
            System.out.println("请先绑定宿舍，再创建报修单。");
            return;
        }

        System.out.println("请输入报修描述（输入0返回上一步）：");
        System.out.print("> ");
        String description = scanner.nextLine().trim();
        if ("0".equals(description)) {
            // 返回学生菜单。
            return;
        }

        // 调用业务层创建报修单。
        Repair repair = repairController.createRepair(currentUser.getAccount(), dormRoom, description);
        if (repair == null) {
            // 返回 null 表示校验未通过或数据库写入失败。
            System.out.println("创建报修单失败，请检查输入后重试。");
            return;
        }
        System.out.println("创建成功！报修单状态：" + repair.getStatus());
    }

    /**
     * 将控制台输入的状态编号映射到 RepairStatusEnum。
     */
    private static RepairStatusEnum inputStatus(Scanner scanner) {
        System.out.println("请输入状态编号：1-PENDING 2-PROCESSING 3-FINISHED 4-CANCELED（输入0返回上一步）");
        System.out.print("> ");
        String statusInput = scanner.nextLine().trim();
        switch (statusInput) {
            case "0":
                // 返回 null 表示“返回上一步”。
                return null;
            case "1":
                return RepairStatusEnum.PENDING;
            case "2":
                return RepairStatusEnum.PROCESSING;
            case "3":
                return RepairStatusEnum.FINISHED;
            case "4":
                return RepairStatusEnum.CANCELED;
            default:
                System.out.println("状态编号无效。");
                return null;
        }
    }

    /**
     * 统一打印报修单列表。
     */
    private static void printRepairs(List<Repair> repairs) {
        if (repairs == null || repairs.isEmpty()) {
            System.out.println("暂无报修单。");
            return;
        }
        // 逐条输出核心字段，便于控制台快速查看。
        for (Repair repair : repairs) {
            System.out.println("ID=" + repair.getId()
                    + " 学生账号=" + repair.getUserId()
                    + " 宿舍=" + repair.getDormRoom()
                    + " 状态=" + repair.getStatus()
                    + " 描述=" + repair.getDescription());
        }
    }

    /**
     * 安全地把字符串解析成 Long；解析失败返回 null。
     */
    private static Long parseLong(String input) {
        try {
            return Long.valueOf(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
