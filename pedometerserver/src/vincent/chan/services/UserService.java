package vincent.chan.services;

import java.sql.SQLException;

import cn.itcast.jdbc.JdbcUtils;

import vincent.chan.dao.RoleDao;
import vincent.chan.dao.UserDao;
import vincent.chan.pojo.Role;
import vincent.chan.pojo.User;
import vincent.chan.utils.Tool;

public class UserService {
	UserDao userDao = new UserDao();
	RoleDao roleDao = new RoleDao();

	public User findUserByNameAndPassword(String name, String password) {
		User user = userDao.findUserByNameAndPassword(name, password);
		return user;
	}

	public User findUserByUid(String uid) {
		User user = userDao.selectUserByUid(uid);
		return user;
	}

	public boolean createUser(User user) {
		Role role = Tool.getInitRole(1, user.getUid(), user.getName());

		try {
			JdbcUtils.beginTransaction();
			int result = userDao.createUser(user);
			roleDao.createRole(role);
			JdbcUtils.commitTransaction();
			if (result > 0) {
				return true;
			}

		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return false;
	}

	public void updateTotalstepsByUid(String uid, String steps) {
		try {
			boolean updateTotalStepsByUid = userDao.updateTotalStepsByUid(uid,
					steps);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String updatePassword(User user, String newPassword) {
		try {
			boolean updatePassword = userDao.updatePassword(user, newPassword);
			if (updatePassword)
				return "success";
			else
				return "faild";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "faild";
	}
}
