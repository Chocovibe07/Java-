package com.gdut.repair.util;

import com.gdut.repair.mapper.RepairMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.Reader;

/**
 * 报修单 student_id 历史数据迁移工具（一次性执行）。
 *
 * 迁移目标：
 * 1) 将 repair.student_id 列类型改为 VARCHAR（若此前是数值类型）。
 * 2) 将旧数据中 student_id=用户数值ID 的记录，替换为 student_id=用户账号。
 */
public class RepairStudentIdMigrationRunner {

	public static void main(String[] args) throws Exception {
		// 复用项目已有 mybatis-config.xml，避免重复维护数据库连接参数。
		Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(reader);

		// 开启自动提交，确保 DDL/DML 执行后立即生效。
		try (SqlSession session = factory.openSession(true)) {
			RepairMapper mapper = session.getMapper(RepairMapper.class);

			// 迁移前统计：有多少旧格式（纯数字）数据。
			int beforeNumeric = mapper.countNumericStudentIdRows();
			// 迁移前统计：有多少旧数据可通过 user.id 找到对应账号。
			int convertible = mapper.countConvertibleRows();

			System.out.println("===== repair.student_id 数据迁移开始 =====");
			System.out.println("迁移前纯数字记录数: " + beforeNumeric);
			System.out.println("可转换记录数: " + convertible);

			// 先调整列类型，确保 student_id 可以写入账号字符串。
			mapper.alterStudentIdColumnToVarchar();

			// 执行批量迁移：旧ID -> 学生账号。
			int updated = mapper.migrateStudentIdToAccount();

			// 迁移后统计：无法匹配任何账号的记录数（这才是真正的脏数据指标）。
			int notMappedToAccount = mapper.countRowsNotMappedToAccount();

			System.out.println("已更新记录数: " + updated);
			System.out.println("迁移后无法匹配账号的记录数: " + notMappedToAccount);

			if (notMappedToAccount > 0) {
				System.out.println("注意：仍有异常数据，请检查 repair.student_id 是否存在无法匹配 user.account 的记录。");
			} else {
				System.out.println("迁移完成：repair.student_id 已按学生账号语义修正。");
			}
			System.out.println("===== repair.student_id 数据迁移结束 =====");
		}
	}
}

