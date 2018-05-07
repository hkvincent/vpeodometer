package vincent.chan.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import vincent.chan.pojo.Role;
import vincent.chan.pojo.User;

import cn.itcast.jdbc.TxQueryRunner;

public class RoleDao {
	private QueryRunner qr = new TxQueryRunner();

	public boolean createRole(Role role) throws SQLException {
		int reuslt = 0;
		String sql = "insert into role(rid,name,level,attack,defend,health_point,magic_point,skill,user_uid) values(?,?,?,?,?,?,?,?,?) ";
		Object[] paras = { role.getRid(), role.getName(), role.getLevel(),
				role.getAttack(), role.getDefend(), role.getHealthPoint(),
				role.getMagicPoint(), role.getSkill(), role.getUserUid() };
		reuslt = qr.update(sql, paras);

		return reuslt >= 0 ? true : false;

	}

	public List<Map<String, Object>> findRoleByUid(String uid)
			throws SQLException {
		String sql = "SELECT * FROM role  LEFT JOIN user ON role.user_uid = user.uid WHERE role.user_uid != ?";
		List<User> myRole = new ArrayList<User>();
		return qr.query(sql, new MapListHandler(), uid);
	}

	public List<Role> findMyRoleByUid(String uid) throws SQLException {
		String sql = "SELECT * FROM role  WHERE user_uid = ?";
		List<User> myRole = new ArrayList<User>();
		return qr.query(sql, new BeanListHandler<Role>(Role.class), uid);
	}

	public Map<String, Object> findRoleByUserNameAndPassword(User user)
			throws SQLException {
		String sql = "SELECT * FROM role LEFT JOIN user ON role.user_uid = user.uid WHERE user.name = ? and user.password = ?";
		Map<String, Object> query = qr.query(sql, new MapHandler(),
				user.getName(), user.getPassword());
		return query;
	}

	public boolean updataRoleByUid(String uid, Role role) throws SQLException {
		int reuslt = 0;
		String sql = "UPDATE role SET level = ?,attack = ?,defend=?,health_point=?,magic_point=?,skill=? where user_uid = ?";
		Object[] paras = { role.getLevel(), role.getAttack(), role.getDefend(),
				role.getHealthPoint(), role.getMagicPoint(), role.getSkill(),
				uid };
		reuslt = qr.update(sql, paras);
		return reuslt >= 0 ? true : false;

	}
}
