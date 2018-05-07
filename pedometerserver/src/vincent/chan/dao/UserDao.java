package vincent.chan.dao;

import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;

import vincent.chan.pojo.User;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class UserDao {
	private QueryRunner qr = new TxQueryRunner();

	public User findUserByNameAndPassword(String name, String password) {
		String sql = "select * from user where name=? and password=?";
		User user = null;
		try {
			Map<String, Object> beanMap = qr.query(sql, new MapHandler(), name,
					password);
			if (beanMap == null)
				return null;
			user = CommonUtils.toBean(beanMap, User.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;

	}

	public int createUser(User user) {
		int reuslt = 0;
		try {
			String sql = "insert into user(uid,name,password,email,register_time,gender,code) values(?,?,?,?,?,?,?) ";
			Object[] paras = { user.getUid(), user.getName(),
					user.getPassword(), user.getEmail(),
					user.getRegisterTime(), user.isGender(), "0" };

			reuslt = qr.update(sql, paras);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reuslt;
	}

	public User selectUserByUid(String uid) {
		String sql = "select * from user where uid = ?";
		User user = null;
		try {
			Map<String, Object> beanMap = qr.query(sql, new MapHandler(), uid);
			if (beanMap == null)
				return null;
			user = CommonUtils.toBean(beanMap, User.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}

	public boolean updateTotalStepsByUid(String uid, String steps)
			throws SQLException {
		String sql = "update user set totalstep = ? where uid = ?";
		int update = qr.update(sql, steps, uid);
		return update > 0 ? true : false;
	}

	public boolean updatePassword(User user, String newPassword)
			throws SQLException {
		String sql = "update user set password = ? where name =? and password = ?";
		int update = qr.update(sql, newPassword, user.getName(),
				user.getPassword());
		return update > 0 ? true : false;
	}
}
