package vincent.chan.services;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import vincent.chan.dao.RoleDao;
import vincent.chan.pojo.Role;
import vincent.chan.pojo.User;

public class RoleService {
	RoleDao roleDao = new RoleDao();

	public boolean createRole(Role role) {
		boolean createRole = false;
		try {
			createRole = roleDao.createRole(role);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return createRole;
	}

	public List<Role> getMyRole(String uid) {
		List<Role> roleList = null;
		try {
			roleList = roleDao.findMyRoleByUid(uid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return roleList;
	}

	public List<User> getRole(String uid) {
		List<Map<String, Object>> userNroleMaps = null;
		List<User> userList = new ArrayList<User>();
		try {
			userNroleMaps = roleDao.findRoleByUid(uid);
			for (Map<String, Object> map : userNroleMaps) {
				User user = new User();
				Role role = new Role();
				try {
					BeanUtils.populate(user, map);
					BeanUtils.populate(role, map);
					user.setRole(role);
				} catch (Exception e) {
					e.printStackTrace();
				}
				userList.add(user);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userList;

	}

	public User getMyRole(User user) {
		User getUser = new User();
		Role role = new Role();
		try {
			Map<String, Object> map = roleDao
					.findRoleByUserNameAndPassword(user);
			BeanUtils.populate(getUser, map);

			BeanUtils.populate(role, map);
			BeanUtils.setProperty(role, "healthPoint", map.get("health_point"));
			BeanUtils.setProperty(role, "magicPoint", map.get("magic_point"));
			getUser.setRole(role);
			return getUser;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String updateRole(String steps, String uid) {
		Role role = new Role();
		role.setLevel(1);
		Integer Exp = Integer.valueOf(steps);
		int upgradeEX = 0;
		for (int i = 100; i > 0; i--) {
			upgradeEX = i * (i * i + 5) * 10;
			if (upgradeEX - Exp < 0) {
				role.setLevel(i);
				break;
			}
		}
		int attack = (int) Math.round(role.getLevel() * 1.4d + 10);
		int defend = (int) Math.round(role.getLevel() * 1.1d + 5);
		int magic = role.getLevel() + 30;
		int health = (role.getLevel() + 9) * 10;
		int skill = role.getLevel() / 3;
		role.setAttack(attack);
		role.setDefend(defend);
		role.setMagicPoint(magic);
		role.setHealthPoint(health);
		role.setSkill(skill);
		try {
			roleDao.updataRoleByUid(uid, role);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return role.getLevel() + "";

	}
}
